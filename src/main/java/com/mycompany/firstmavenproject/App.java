package com.mycompany.firstmavenproject;

import com.gargoylesoftware.htmlunit.javascript.host.URL;
import com.google.common.collect.HashBiMap;
import com.thoughtworks.selenium.webdriven.WebDriverCommandProcessor;
import java.awt.AWTException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

/**
 * Hello world!
 *
 */
public class App 
{
    private WebDriver driver;
    private String baseUrl;
    
    public static void main( String[] args ) throws AWTException, IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException
    {
        System.out.println( "Hello World!" );
        String commandName = "isDisplayed";
        String commandNamem = "sendKeys";
        RemoteWebElement rElement = new RemoteWebElement();
        //java.lang.reflect.Method method = null;
        SeleniumTestParser sl = new SeleniumTestParser("C:\\Users\\Jakub\\Documents\\NetBeansProjects\\tets.txt");
        //WebDriverCommandProcessor w = new WebDriverCommandProcessor("http://www.google.com", new FirefoxDriver());
//        CharSequence[] sek = new String[2];
//        sek[0] = "ahoj";
//        sek[1] = " kokote";
//        Class[] cArgs = new Class[1];
//        cArgs[0] = CharSequence[].class;
//        Object[] argss = null;
//        Object[] argsForSecondM = {sek};
//        
//        FirefoxDriver driver = new FirefoxDriver();
//        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//        driver.get("https://www.google.cz/");
//        WebElement element = driver.findElement(By.id("lst-ib"));
//        if(element instanceof RemoteWebElement)
//            method = element.getClass().getDeclaredMethod(commandName, (Class<?>[]) argss);
//        Object result = method.invoke(element);
//        if(element instanceof RemoteWebElement)
//            method = element.getClass().getDeclaredMethod(commandNamem, (Class<?>[]) cArgs);
//        Object results = method.invoke(element, (Object) sek);
//        driver.quit();
        
        List<String> list = new ArrayList<>();
        Object str = list;
        
//        String first = "we.click()";
//        String second = "driver.FindEl(By.id(dfs))";
//        String s = second.concat(first.substring(2));
//       
//        Class<?> clazz=  Class.forName("java.util.List");
//        
//        Method[] methods = clazz.getDeclaredMethods();
//        for (Method method : methods) {
//            System.out.println(method.getName());
//        }
        sl.findMethodsByAnnotation("@Test");
        List<String> names = sl.getMethodNames("@Test");
        List<Variable> variables = sl.resolveTestBindings(names.get(0));
        
        sl.prepareCommands(names.get(0), variables);
        sl.initializeDriver(names.get(0), variables);
           
   }
    
    public static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
            try {
                return clazz.cast(o);
    } catch(ClassCastException e) {
        return null;
    }
    
}
}
