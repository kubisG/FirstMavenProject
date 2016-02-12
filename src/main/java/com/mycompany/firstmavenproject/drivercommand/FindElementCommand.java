/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.drivercommand;

import com.mycompany.firstmavenproject.ByFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Jakub
 */
public class FindElementCommand implements IDriverCommandStrategy {
    private final String innerMethod;
    private final String param;
    
    public FindElementCommand(String innerMethod, String param) {
        this.innerMethod = innerMethod;
        this.param = param;
    }
    
    @Override
    public Object execute(WebDriver driver) {
        By by = ByFactory.create(innerMethod, param);
        return driver.findElement(by);
    }
    
}
