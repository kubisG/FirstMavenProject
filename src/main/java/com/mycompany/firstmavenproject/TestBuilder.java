/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import com.mycompany.firstmavenproject.command.CommandFascade;
import com.mycompany.firstmavenproject.command.CommandStrategyFactory;
import com.mycompany.firstmavenproject.command.ICommandStrategy;
import com.mycompany.firstmavenproject.drivercommand.DriverCommandStrategyFactory;
import com.mycompany.firstmavenproject.drivercommand.IDriverCommandStrategy;
import java.util.Deque;
import org.openqa.selenium.WebDriver;

/**
 *
 * @author Jakub
 */
public class TestBuilder {
    private static HighLighter highLighter = new HighLighter();
    private static MouseMove mouseMove = new MouseMove();
    
    public static Test create(String driverType, Deque<Command> inputCommands){
        WebDriver driver = DriverFactory.create(driverType);
        ICommandStrategy strategy;
        IDriverCommandStrategy driverStrategy;
        Test test = new Test(driver);
        
        for (Command inputCommand : inputCommands) {
            prepareInputCommand(inputCommand);
            driverStrategy = DriverCommandStrategyFactory.create(inputCommand);
            strategy = CommandStrategyFactory.create(inputCommand);
            test.addCommandFascade(new CommandFascade(driverStrategy, strategy, 
                                                    mouseMove, highLighter));   
        }       
    return test;
    }
    
    // Upravit input commands (vynechat null hodnoty jsou zbytecny 
    private static void prepareInputCommand(Command command){
        String tmpParam;
        
        if(command.getCount() > 1){
            tmpParam = command.getMethodName();
            command.addParam(tmpParam);
        }
    }
 }
