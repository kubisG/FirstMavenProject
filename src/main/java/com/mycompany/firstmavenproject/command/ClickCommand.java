/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.command;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

/**
 *
 * @author Jakub
 */
public class ClickCommand implements ICommandStrategy{

    @Override
    public void execute(Object driverObject) {
        WebElement we = (WebElement) driverObject;

        we.click();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClickCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
