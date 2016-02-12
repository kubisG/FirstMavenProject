/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.safari.SafariDriver;

/**
 *
 * @author Jakub
 */
public class DriverFactory {
    
    public static WebDriver create(String type) {
        WebDriver driver = null;
        
        if(type.contains("FirefoxDriver")){
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        } else if(type.contains("ChromeDriver")){
            driver = new ChromeDriver();
        } else if(type.contains("InternetExplorerDriver")){
            driver = new InternetExplorerDriver();
        } else if(type.contains("OperaDriver")){
            driver = new OperaDriver();
        } else if(type.contains("SafariDriver")){
            driver = new SafariDriver();
        }
    return driver;
    }
    
}
