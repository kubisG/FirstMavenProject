/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.command;

import com.mycompany.firstmavenproject.Command;

/**
 *
 * @author Jakub
 */
public class CommandStrategyFactory {
    
    public static ICommandStrategy create(Command command){
        ICommandStrategy strategy = null;
        String type = command.getMethodName();
        
        if(type.equals("sendKeys")){
            strategy = new SendKeysCommand(command.getParam());
        } else if(type.equals("click")){
            strategy = new ClickCommand();
        } else if(type.equals("clear")){
            strategy = new ClearCommand();
        } else if(type.equals("submit")){
            strategy = new SubmitCommand();
        } 
        
    return strategy;
    } 
}
