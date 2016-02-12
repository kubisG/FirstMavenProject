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
public class SubmitCommand implements ICommandStrategy {

    @Override
    public void execute(Object driverObject) {
        WebElement we = (WebElement) driverObject;
        we.submit();
    }

}
