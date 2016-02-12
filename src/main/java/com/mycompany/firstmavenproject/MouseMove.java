/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import java.awt.AWTException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.awt.Robot;
import org.openqa.selenium.Point;

/**
 *
 * @author Jakub
 */
public class MouseMove {
    public void move(Object driverObject) throws AWTException, InterruptedException{
    Robot robot = new Robot();
    WebElement we  = (WebElement) driverObject;
    Point p = we.getLocation();
    Thread.sleep(500);
    for (int i=0; i<20000; i++){
        int x = ((p.getX() * i) / 20000) + ((1290/2)*(20000-i)/20000);
        int y = (((p.getY()+80) *i) / 20000) + ((1040/2)*(20000-i)/20000);
        robot.mouseMove(x,y);
    }
    Thread.sleep(1000);
    }
}
