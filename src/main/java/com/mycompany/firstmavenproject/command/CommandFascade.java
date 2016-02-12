/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.command;

import com.mycompany.firstmavenproject.HighLighter;
import com.mycompany.firstmavenproject.MouseMove;
import com.mycompany.firstmavenproject.drivercommand.IDriverCommandStrategy;
import java.awt.AWTException;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Jakub
 */
public class CommandFascade {
    private final MouseMove mouseMove;
    private final HighLighter highlighter;
    private final ICommandStrategy strategy;
    private final IDriverCommandStrategy driverStrategy;
    private Object driverObject;
    
    public CommandFascade(IDriverCommandStrategy driverStrategy, 
            ICommandStrategy strategy, MouseMove mouseMove, HighLighter highLighter) {
        this.driverStrategy = driverStrategy;
        this.strategy = strategy;
        this.mouseMove = mouseMove;
        this.highlighter = highLighter;
    }
   
    public void execute(WebDriver driver) throws AWTException, InterruptedException{
        driverObject = driverStrategy.execute(driver);
        mouseMove.move(driverObject);
        //highlighter.highlight(driverObject);
        strategy.execute(driverObject);
    }
}
