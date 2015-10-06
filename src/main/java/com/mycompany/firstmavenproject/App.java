package com.mycompany.firstmavenproject;

import java.awt.AWTException;
import java.awt.Robot;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws AWTException
    {
        System.out.println( "Hello World!" );
        
        Robot robot = new Robot();
        
        WebDriver driver = new FirefoxDriver();
        
        driver.get("http://www.google.com");
        driver.manage().window().maximize();
        Point p = driver.findElement(By.id("lst-ib")).getLocation();
        
        robot.mouseMove(p.getX(), p.getY());
        robot.delay(1000);
        
        driver.close();
    }
}
