/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import com.mycompany.firstmavenproject.command.CommandFascade;
import java.awt.AWTException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Jakub
 */
public class Test {
    private final WebDriver driver;
    private final Deque<CommandFascade> commandFascades;

    public Test(WebDriver driver) {
        this.driver = driver;
        this.commandFascades = new ArrayDeque<>();
    }
    
    public void addCommandFascade(CommandFascade commandFascade){
        commandFascades.addLast(commandFascade);
    }
    
    public void runTest() throws AWTException, InterruptedException{
        start();
        
        while(!commandFascades.isEmpty()){   
            commandFascades.removeFirst().execute(driver);
        }
        quit();
    }
    
    public void start(){
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("https://www.google.cz/?gfe_rd=cr&ei=Hbm3VqLHKKyG8QedkLeABw&gws_rd=ssl");      
    }
    
    public void quit(){
        driver.quit();
    }
}
//Object o1 = ((JavascriptExecutor) driver).executeScript("return document.documentElement.clientWidth");
//        long l = (long) o1;
//        Object o2 = ((JavascriptExecutor) driver).executeScript("return document.documentElement.clientHeight");
//        long l1 = (long) o2;
//        Point p = driver.manage().window().getPosition();
//        Dimension d  = driver.manage().window().getSize();