/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.command;

import org.openqa.selenium.WebElement;

/**
 *
 * @author Jakub
 */
public class SendKeysCommand implements ICommandStrategy {
    private final String param;

    public SendKeysCommand(String param) {
        this.param = param;
    }
  
    @Override
    public void execute(Object driverObject) {
        WebElement we = (WebElement) driverObject;
        we.sendKeys(param);
    }
    
}
