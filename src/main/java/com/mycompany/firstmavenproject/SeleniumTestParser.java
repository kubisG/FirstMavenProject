/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import com.thoughtworks.selenium.webdriven.VariableDeclaration;
import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.text.Document;
import org.eclipse.jetty.util.ArrayQueue;

/**
 *
 * @author Jakub
 */
public class SeleniumTestParser {
    private final CompilationUnit cu;
    private final List<Variable> fields;
    private final boolean setUpMethod;
    private final String[] driverNames = initializeDriverNames();
    private final String filePath;
      
    public SeleniumTestParser(String filePath) {
        this.filePath = filePath;
        this.cu = initializeCompilationUnit(); 
        this.setUpMethod = hasSetUpMethod();
        this.fields = initializeFields();    
    }
    
    protected final CompilationUnit initializeCompilationUnit() {
        CompilationUnit cu = null;
        FileInputStream input = null;
        
        try {
            input = new FileInputStream(filePath);
            cu = JavaParser.parse(input);
        } catch (FileNotFoundException|ParseException ex) {
            System.out.println(ex);
        }
        finally {
            if (input != null)
                try {
                    input.close();
            } catch (IOException ex) {
               System.out.println(ex);
            }
        }
    return cu;    
    }        
    
    protected final String[]  initializeDriverNames () {
        String[] driverNames = new String[] {"WebDriver", "FirefoxDriver", 
                                        "ChromeDriver","iOSDriver", 
                                        "AndroidDriver", "OperaDriver"};
    return driverNames;
    }
    
    protected final List<Variable> initializeFields() {
        FieldSetterVisitor fvisitor = new FieldSetterVisitor();
        List<Variable> fields = new ArrayList<>();             
        MethodDeclaration method;
        
        fvisitor.visit(cu, fields);
        
        if(setUpMethod) {
            method = findSetUpMethod();
            resolveBindings(fields, method);
        }
        
    return fields;
    }
    
    // Vrací true pokud má Selenium test setUp metodu jinak false
    protected final boolean hasSetUpMethod() {
        return !findMethodsByAnnotation("@Before").isEmpty();
    }   
    
    public List<BodyDeclaration> filterMembers(Condition c) {
        List<BodyDeclaration> members = cu.getTypes().get(0).getMembers();

        List<BodyDeclaration> filteredMembers = members.stream()
                            .filter(e -> c.passedCondition(e))
                            .collect(Collectors.toList());
    return filteredMembers;
    }
    
    // Vratí metodu(y) jejichž anotace obsahují ve svém názvu parametr annotation
    // POZN: @Before, @BeforeClass...
    public List<MethodDeclaration> findMethodsByAnnotation(String annotation) {
        List<BodyDeclaration> members = filterMembers(o -> o instanceof MethodDeclaration);
        
        List<MethodDeclaration> methods = members
                .stream()
                .map(e -> (MethodDeclaration)e)
                .filter(e -> (e.getAnnotations() == null)? 
                        false : 
                        e.getAnnotations()
                            .stream()
                            .anyMatch(a-> a.toString().contains(annotation)))
                .collect(Collectors.toList());
        
    return methods;    
    }
    
    // Vrací metodu podle jejího názvu
    // Pozor: vice metod muze mit stejny nazev (UPRAVIT)
    public MethodDeclaration findMethodByName(String name) {
        List<BodyDeclaration> members = filterMembers(o -> o instanceof MethodDeclaration);
        
        List<MethodDeclaration> method = members
                .stream()
                .map(e -> (MethodDeclaration)e)
                .filter(e -> e.getName().equals(name))
                .collect(Collectors.toList());
        
    return method.get(0);    
    }
    
    // Vrací seznam jmen method podle dané anotace
    public List<String> getMethodNames(String annotation) {
        List<String> methodNames;
        List<MethodDeclaration> methods = findMethodsByAnnotation(annotation);
        
        methodNames = methods
                .stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
        
    return methodNames;
    }
    
    // Vrací seznam deklarovaných proměnných v rámci jedné metody
    private List<Variable> findMethodVariables(MethodDeclaration method) {
        VariableSetterVisitor vVisitor = new VariableSetterVisitor();
        List<Variable> testVariables = new ArrayList<>();
       
        vVisitor.visit(method, testVariables);
        
    return testVariables;
    }
    
    // Změnit název metody nebo promenné (UPRAVIT)
    // Vrací "metodu" v podobě seznamu stringů
    // Každý string obsahuje jeden řádek z těla metody
    public List<String> methodToStrings(MethodDeclaration method) {
        List<String> statements = method.getBody().getStmts()
                .stream()
                .map(e -> e.toString())
                .collect(Collectors.toList());
    return statements;
    }
    
    // Návázaní hodnot proměnných metody
    public void resolveBindings(List<Variable> variables, MethodDeclaration method) {
        List<String> statements = methodToStrings(method);
        List<Deque<MethodCallExpr>> commands = new ArrayList<>();
        List<String> values;
        
        for (Variable var: variables) {
            values = findVariableValues(statements, var.getName());
            if(values != null) {
                for (String value : values) {
                    var.setValue(value);    
                }
            }
            System.out.println(" ");
        }
        MethodCall mc = new MethodCall();
        mc.visit(method, commands);
        //filterCommands(variables, commands);
    }
    
    // Metoda 
    public List<Variable> resolveTestBindings(String testName) {
        MethodDeclaration testMethod = findMethodByName(testName);
        List<Variable> variables = findMethodVariables(testMethod);
        // K lokálním proměnným přidám ještě fieldy (instanční proměnné, které 
        // se mohou v metodé vyskytovat) 
        variables.addAll(fields);
        resolveBindings(variables, testMethod);
        
        //  CHYBÍ DODĚLAT VYHODNOCĚNÍ VÝRAZŮ "HODNOT" POKUD JE TŘEBA
        //
        
    return variables;
    }
    
    // Metoda která zjistí hodnoty proměnných v rámci Testu(testovací metody)
    // Zamyslet se nad vyjimkou, kterou by mohl vyhodit matcher
    private List<String> findVariableValues(List<String> statements, String varName) {
        Pattern p1 = Pattern.compile(varName + "\\s+\\=\\s.+\\;");
        Pattern p2 = Pattern.compile("[^\\s\\;]+\\s*[^\\s\\;]*");
                
        List<String> values = statements
                .stream()
                .filter(e -> p1.matcher(e).find())
                .flatMap(line -> Stream.of(line.split("=")))
                .filter(e -> !e.contains(varName))
                .map(e -> { Matcher m = p2.matcher(e);
                            m.find();
                            return m.group();})
                .collect(Collectors.toList());
        
    return values;
    }

    // Metoda vrací setUpMetodu s annotaci @Before nebo @BeforeClass
    // Je logické, aby byla použita jen jedna setUpMetoda (nebo žádná)
    private MethodDeclaration findSetUpMethod() {
        List<MethodDeclaration> beforeMethods = findMethodsByAnnotation("@Before");
        
        if(beforeMethods.size() != 1)
                throw new IllegalStateException("Vyzve uzivatele ke zvoleni "
                        + "jedne ze setUp metod jinak ukonci program");
        
    return beforeMethods.get(0);
    }
    
    // Vnitří třída (inner class), která má jedinou metodu visit
    // Visit je metoda která "navštíví" vsechny deklarovane promenné v rámci jedné
    // metody(Sel. testu) a dále využije metody setVariables k nastavení seznamu
    // testVariables, jejiž položky maji tvar Variable(typ, nazev, hodnota(y))
    private class VariableSetterVisitor extends VoidVisitorAdapter {      
        
        @Override
        public void visit(VariableDeclarationExpr n, Object testVariables) {      
            List<VariableDeclarator> myVars = n.getVars();
            String type = n.getType().toString();
            setVariables(myVars, testVariables, type);
        }
    }
    
    // Vnitří třída (inner class), která má jedinou metodu visit
    // Visit je metoda která "navštíví" vsechny deklarovane fieldy v rámci 
    // celeho souboru se Selenium testem(testy)
    private class FieldSetterVisitor extends VoidVisitorAdapter {      
        
        @Override
        public void visit(FieldDeclaration n, Object testVariables) {
            List<VariableDeclarator> myFields = n.getVariables();
            String type = n.getType().toString();
            setVariables(myFields, testVariables, type);
        }
    }  
    
    // Metoda visit navštíví a naplní seznam příkazů (List of commands)
    // Každý command může být složen z několika volání metod (Method call expr)
    // Př: driver.findElement(By.id(String)).click() --> skláda se z volání
    // tří metod
    private class MethodCall extends VoidVisitorAdapter {      
        private Deque<MethodCallExpr> command = new ArrayDeque<>();
        private int counter = 0;
        
        @Override
        public void visit(MethodCallExpr n, Object arg) {
            counter++;
            super.visit(n, arg);
            System.out.println(n.toString());
            command.push(n);
            counter--;
            if(counter == 0){
                List<Deque<MethodCallExpr>> tmpCommands = (List<Deque<MethodCallExpr>>) arg; 
                tmpCommands.add(new ArrayDeque<>(command));
                command.clear();
            }
        }      
    }      
    
    
    
    private void setVariables(List<VariableDeclarator> vars, Object testVariables, String type) {
        List<Variable> tmp = (List<Variable>)testVariables; 
            
        for (VariableDeclarator var: vars){
                if(var.getInit() != null)
                    tmp.add(new Variable(type, var.getId().toString(), 
                                                    var.getInit().toString()));
                else
                    tmp.add(new Variable(type, var.getId().toString()));
        }
    }
     
    public Variable initializeDriver(String testName ,List<Variable> variables) {
        Variable driver;
        MethodDeclaration testMethod = findMethodByName(testName);
        List<String> statements = methodToStrings(testMethod);
       
        driver = variables
                .stream()
                .filter(e -> Arrays.stream(driverNames)
                        .anyMatch(n -> e.getType().equals(n)))
                .filter(d -> statements
                        .stream()
                        .anyMatch(s -> s.contains(d.getName() + "."))) // Př: driver.
                .reduce(null, (a, v) -> new Variable(v));
    
    return driver;
    }
     
    // Vymyslet nazev pro seznam call expr
    public List<MethodCallExpr> filterIncompleteCommands(List<Variable> variables, List<Deque<MethodCallExpr>> exprs) {
        List<MethodCallExpr> result = exprs
                .stream()
                .map(e -> e.getFirst())
                .filter(m -> variables
                        .stream()
                        .filter(v -> !v.getType().contains("Driver"))
                        .anyMatch(v -> m.toString().contains(v.getName() + ".") || v.getValues()
                                    .stream()
                                    .anyMatch(val ->  val.equals(m.toString()))))
                .collect(Collectors.toList());
    
    return result;    
    }
    
    
    public List<ArrayDeque<MethodCallExpr>> filterSeleniumCommands(List<Variable> variables, 
            List<Deque<MethodCallExpr>> exprs) {
        
        List<MethodCallExpr> incompleteCommands = filterIncompleteCommands(variables, exprs);
        
        return null;
    }
    
    
    
//    private boolean compareDriverNames(String typeName) {
//        for (String webDriverName : driverNames) {
//            if(webDriverName.equals(typeName))
//                return true;
//        }
//        return false;
//    }        
        
    public static interface Condition {
        boolean passedCondition(Object o);
    }
    
    public static interface FieldCondition {
        boolean passedCondition(Variable v);
    }
    
    public static interface MethodCondition {
        boolean passedCondition(String s);
    }
}
    
    
   

