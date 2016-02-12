package com.mycompany.firstmavenproject;

import java.awt.AWTException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws AWTException, IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, InstantiationException, InterruptedException
    {
        SeleniumTestParser parser = new SeleniumTestParser(
                "C:\\Users\\Jakub\\Documents\\NetBeansProjects\\zezula1.java");
        List<String> testMethodNames = parser.getMethodNames("@Test");
        String testName = testMethodNames.get(0);
        Deque<Command> inputCommands = parser.parse(testName);
        String testType = parser.getDriverType(testName);
        inputCommands.removeFirst();
        Test myTest = TestBuilder.create(testType, inputCommands);
        myTest.runTest();             
   }
    
    public static <T> T convertInstanceOfObject(Object o) {
            return (T)o;
    }
    
//    public static interface Command {
//        boolean executeCommand(String param);
//    }    
}


