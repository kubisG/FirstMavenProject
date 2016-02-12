/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.drivercommand;

import org.openqa.selenium.WebDriver;

/**
 *
 * @author Jakub
 */
public interface IDriverCommandStrategy {
    public Object execute(WebDriver driver);
}
