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
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.Clock;
import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
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
    public void resolveBindings(List<Variable> vars, MethodDeclaration method) {
        List<Value> varValues;
        
        for (Variable var: vars) {
            varValues = findVariableValues(method, var.getVarName());
            if(varValues != null) {
                for (Value varValue : varValues) {
                    var.setValue(varValue);    
                }
            }
        }
    }
    
    // Metoda 
    public List<Variable> initializeVariables(String testName) {
        MethodDeclaration testMethod = findMethodByName(testName);
        List<Variable> vars = findMethodVariables(testMethod);
        // K lokálním proměnným přidám ještě fieldy (instanční proměnné, které 
        // se mohou v metodé vyskytovat) 
        vars.addAll(fields);
        resolveBindings(vars, testMethod);
        
        //  CHYBÍ DODĚLAT VYHODNOCĚNÍ VÝRAZŮ "HODNOT" POKUD JE TŘEBA
        //
    return vars;
    }
    
    // Metoda která zjistí hodnoty proměnných v rámci Testu(testovací metody)
    // Zamyslet se nad vyjimkou, kterou by mohl vyhodit matcher
    private List<Value> findVariableValues(MethodDeclaration method, String varName) {
        Pattern p1 = Pattern.compile(varName + "\\s+\\=\\s.+\\;");
        Pattern p2 = Pattern.compile("[^\\s\\;]+\\s*[^\\s\\;]*");
        List<Statement> statements = method.getBody().getStmts();
        List<Value> varValues;
        
        List<Statement> matchedStatements = statements
                .stream()
                .filter(e -> p1.matcher(e.toString()).find())
                .collect(Collectors.toList());
        
        if(matchedStatements.isEmpty())
            return null;
        
        List<String> values = matchedStatements
                .stream()
                .flatMap(line -> Stream.of(line.toString().split("=")))
                .filter(e -> !e.contains(varName))
                .map(e -> { Matcher m = p2.matcher(e);
                            m.find();
                            return m.group();})
                .collect(Collectors.toList());
        
        List<Integer> valuesBeginLine = matchedStatements
                .stream()
                .map(e -> e.getBeginLine())
                .collect(Collectors.toList());
     
        varValues = mergeVariableValues(values, valuesBeginLine);
        
    return varValues;
    }
    
    private List<Value> mergeVariableValues(List<String> values, List<Integer> valuesBeginLine) {
        List<Value> varValues = new ArrayList<>();
        Iterator<String> it1 = values.iterator();
        Iterator<Integer> it2 = valuesBeginLine.iterator();
        int beginLine = it2.next();
        int endLine;
        String value;
        
        while (it1.hasNext()) {
            value = it1.next();
            if(it2.hasNext()){
                endLine = it2.next();
                varValues.add(new Value(value, beginLine, endLine));
                beginLine = endLine;          
            }
            else
                varValues.add(new Value(value, beginLine));
        }
    return varValues;
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
            //System.out.println(n.getBeginLine() + " " + n.getName());
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
                                                    new Value(var.getInit().toString(),
                                                              var.getBeginLine())));
                else
                    tmp.add(new Variable(type, var.getId().toString()));
        }
    }
     
    public Variable initializeDriver(String testName ,List<Variable> vars) {
        Variable driver;
        MethodDeclaration testMethod = findMethodByName(testName);
        List<String> statements = methodToStrings(testMethod);
       
        driver = vars
                .stream()
                .filter(e -> Arrays.stream(driverNames)
                        .anyMatch(n -> e.getVarType().equals(n)))
                .filter(d -> statements
                        .stream()
                        .anyMatch(s -> s.contains(d.getVarName() + "."))) // Př: driver.
                .reduce(null, (a, v) -> new Variable(v));
    
    return driver;
    }
    
    // Potom upravím aby parametrem byla nejaka podmínka a ne jen string  
    private List<Variable> filterVariables(List<Variable> vars, String driverName){
        List<Variable> filtredVars = vars
                .stream()
                .filter(e -> e.getValues()
                        .stream()
                        .anyMatch(v -> v.getValue().contains(driverName)))
                .collect(Collectors.toList());
    
    return filtredVars;
    }
    
   // Vrací seznam commands týkající se pouze příkazů Selenia
    // tzn. příkazy volané nad WebDriverem, WebElement atd.
    private List<Deque<MethodCallExpr>> filterMethodCallExprs(List<Variable> vars, 
            MethodDeclaration method, String driverName) {       
        List<Deque<MethodCallExpr>> methodCallExprs;
        List<Deque<MethodCallExpr>> seleniumMethodCallExprs;
        
        methodCallExprs = new ArrayList<>();
        new MethodCall().visit(method, methodCallExprs);
        
        seleniumMethodCallExprs = methodCallExprs
                .stream()
                .filter(m -> vars
                        .stream()
                        .anyMatch(var -> m.getFirst().toString().contains(var.getVarName() + ".")
                                        || m.getFirst().toString().contains(driverName)))                
                .collect(Collectors.toList());
        
    return seleniumMethodCallExprs; 
    }
    
    private List<Deque<MethodCallExpr>> filterSeleniumMethodCallExprs(List<Variable> vars,
            List<Deque<MethodCallExpr>> seleniumMethodCallExprs, 
            MethodCallCondition c) {     
        List<Deque<MethodCallExpr>> driverMethodCalls;
        
        driverMethodCalls = seleniumMethodCallExprs
                .stream()
                .filter(m -> vars
                        .stream()
                        .flatMap(var -> var.getValues().stream())
                        .anyMatch(val -> c.passedCondition(m.getFirst().toString(),
                                val.getValue())))
                .collect(Collectors.toList());            
    
    return driverMethodCalls;
    }
    
    // POZN: PŘEDĚLAT ... natvrdo nastavena hodnota
    // pokud má var jen jednu hotnotu tak endline je nastaveny na 0
    // pri vytvaření poslední value (nebo jedine) zjistit endline cele metody
    private String findVarValue(Variable var, int lineNumber) {
        String value = var.getValues()
                .stream()
                .filter(val -> (val.getBeginLine() < lineNumber) 
                            && (100 > lineNumber))  // !!!!!!!
                .map(val -> val.getValue())
                .reduce("", String::concat);
    
    return value;
    }
      
    // Možná použít reduce s copy konstruktorem arraydeque ???
    private Deque<MethodCallExpr> getMethodCallExprsByVarValue(String varValue,
            List<Deque<MethodCallExpr>> tmpMethodCallExprs) {
        Deque<MethodCallExpr> methodCallExprs;
        
        methodCallExprs = tmpMethodCallExprs
                .stream()
                .filter(e -> e.getFirst().toString().equals(varValue))
                .flatMap(e -> e.stream())
                .collect(ArrayDeque::new, ArrayDeque::add, ArrayDeque::addAll);
    
    return methodCallExprs;
    }
    
    private void assignmentToFluent(
            List<Variable> seleniumVars,
            List<Deque<MethodCallExpr>> driverMethodCallExprs, 
            List<Deque<MethodCallExpr>> tmpMethodCallExprs) {       
        Deque<MethodCallExpr> tmpDequeMCExpr;
        MethodCallExpr tmpMCExpr;
        Variable tmpVar;
        String tmpStr;
        String varValue;
        
        for (Deque<MethodCallExpr> dmce : driverMethodCallExprs) {
            tmpMCExpr = dmce.getFirst();
            tmpStr = tmpMCExpr.toString();
            
            for (Variable var : seleniumVars) {
                if(tmpStr.contains(var.getVarName())) {
                    int lineNumber = tmpMCExpr.getBeginLine();
                    varValue = findVarValue(var, lineNumber);
                    tmpDequeMCExpr = getMethodCallExprsByVarValue(varValue, 
                                tmpMethodCallExprs);
                    dmce.addAll(tmpDequeMCExpr);
                } 
            }
        }
    }
    
    // Vrací seznam methodcalls (selenium prikazu), které slouží jako vstup pro 
    // vytvoření mých Commands
    public List<Deque<MethodCallExpr>> prepareDriverMethodCalls(
            String testName,
            List<Variable> vars){       
        List<Deque<MethodCallExpr>> driverMethodCallExprs;
        List<Deque<MethodCallExpr>> seleniumMethodCallExprs;
        List<Deque<MethodCallExpr>> tmpMethodCallExprs;
        List<Variable> seleniumVars;
        MethodDeclaration method;
        String driverName = "driver"; // NEMIT TO NA PEVNO NASTAVENE !!!!

        method = findMethodByName(testName);
        seleniumVars = filterVariables(vars, driverName);
        if (!seleniumVars.isEmpty()) {
            seleniumMethodCallExprs = filterMethodCallExprs(seleniumVars, method, driverName);
            driverMethodCallExprs = filterSeleniumMethodCallExprs(seleniumVars, 
                                   seleniumMethodCallExprs, (m, val) -> !m.equals(val));
            tmpMethodCallExprs = filterSeleniumMethodCallExprs(seleniumVars, 
                                seleniumMethodCallExprs, (m, val) -> m.equals(val));
        
            assignmentToFluent(seleniumVars, driverMethodCallExprs, tmpMethodCallExprs);
        }
        else
            driverMethodCallExprs = filterMethodCallExprs(vars, method, driverName);
        
    return driverMethodCallExprs;
    }
    
    public String resolveParametrBinding(int beginLine, String param, 
            List<Variable> vars){
        Variable var = vars
                .stream()
                .filter(v -> v.getVarName().equals(param))
                .reduce(null, (a, v) -> new Variable(v));
        
        if (var != null) 
            return findVarValue(var, beginLine);
        else 
            return param;
    }
    
    public boolean removeParam(String methodNameExpr, 
            Deque<MethodCallExpr> driverMethodCallExprs) {       
        Iterator<Expression> it;
        List<Expression> paramExprs;
        String param;
        
        for (MethodCallExpr driverMethodCallExpr : driverMethodCallExprs) {
            paramExprs = driverMethodCallExpr.getArgs();
            if (paramExprs != null) {
                it = paramExprs.iterator();
                while (it.hasNext()) {
                    param = it.next().toString();
                    if (param.equals(methodNameExpr)) {
                        it.remove();
                        return true;
                   }
                }
            }
        }
    return false;
    }
    
    // UPRAVIT ABYCH NEMEL V PARAMS VOLÁNÍ VNITRNICH METOD !!! ZÍTRA
    public List<Deque<String>> prepareCommand(List<Variable> vars,
            Deque<MethodCallExpr> driverMethodCallExprs){
        
        List<Deque<String>> preparedCommand = new ArrayList<>();
        List<Expression> paramExprs;        
        Deque<String> methodNames = new ArrayDeque<>();
        Deque<String> params = new ArrayDeque<>();
        MethodCallExpr tmp;
        String currentMethodName;
        String tmpParam;
        int beginLine;
       
       while (!driverMethodCallExprs.isEmpty()){
            tmp = driverMethodCallExprs.pollLast();
            currentMethodName = tmp.getName();
            paramExprs = tmp.getArgs();
            beginLine = tmp.getBeginLine();
            methodNames.addLast(currentMethodName);
             
            if (paramExprs != null) {
                for (Expression paramExpr : paramExprs) {
                    tmpParam = paramExpr.toString();
                      params.addLast(resolveParametrBinding(beginLine, tmpParam, vars));
                }
            }           
            if (!removeParam(tmp.toString(), driverMethodCallExprs) ) {
               methodNames.addLast("null");
               params.addLast("null");
            }
        }
        preparedCommand.add(methodNames);
        preparedCommand.add(params);
        
    return preparedCommand;      
    }
    
    public Deque<Command> initializeCommands(String testName, List<Variable> vars){
        List<Deque<MethodCallExpr>> driverMethodCallExprs = prepareDriverMethodCalls(testName, vars);
        Deque<Command> commands = new ArrayDeque<>();
        
        for (Deque<MethodCallExpr> driverMethodCallExpr : driverMethodCallExprs) {
                List<Deque<String>> preparedCommand = prepareCommand(vars, driverMethodCallExpr);
                commands.addLast(new Command(preparedCommand.get(0),
                                        preparedCommand.get(1)));
        }
        
    return commands;
    }
//    
//    // METODY KTERÉ BUDOU PATŘIT DO COMMAND EXECUTORU !!!!!!!
//    public Command recursionMethod(Command command) {
//        Command tmpCommand;
//        if(command.getCount() == 0)
//            return command;
//        tmpCommand = funkce(Comma);
//        recursionMethod(command);
//    return command;    
//    }
    
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
    
    public static interface MethodCallCondition {
        boolean passedCondition(String mce, String val);
    }
    
    public static interface MethodCondition {
        boolean passedCondition(String s);
    }
}
    
    
   

