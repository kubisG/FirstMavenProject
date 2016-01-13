/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

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
    
    public WebDriver createDriver(String type) {
        WebDriver driver = null;
        
        if(type.equals("FirefoxDriver")){
            driver = new FirefoxDriver();
        } else if(type.equals("ChromeDriver")){
            driver = new ChromeDriver();
        } else if(type.equals("InternetExplorerDriver")){
            driver = new InternetExplorerDriver();
        } else if(type.equals("OperaDriver")){
            driver = new OperaDriver();
        } else if(type.equals("SafariDriver")){
            driver = new SafariDriver();
        }
    return driver;
    }
    
}
