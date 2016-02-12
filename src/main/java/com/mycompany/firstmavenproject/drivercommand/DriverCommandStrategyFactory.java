/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject.drivercommand;

import com.mycompany.firstmavenproject.Command;

/**
 *
 * @author Jakub
 */

public class DriverCommandStrategyFactory {
    
    public static IDriverCommandStrategy create(Command command){
        IDriverCommandStrategy strategy = null;
        String type = command.getMethodName();
        
        if(type.equals("findElement")){
            strategy = new FindElementCommand(command.getParam(), command.getParam());
        } 
//        else if(type.equals("navigate")){
//            strategy = new NavigeCommand();
//        } else if(type.equals("switchTo")){
//            strategy = new SwitchToCommand;
//        } else if(type.equals("manage")){
//            strategy = new ManageCommand;
//        }

    return strategy;
    }
}
