/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import org.openqa.selenium.By;

/**
 *
 * @author Jakub
 */
public class ByFactory {
    
    public static By create(String type, String param){
        By by = null;
        
        if((type).equals("id")){
            by = new By.ById(param);
        } else if(type.equals("cssSelector")){
            by = new By.ByCssSelector(param);
        } else if(type.equals("className")){
            by = new By.ByClassName(param);
        } else if(type.equals("linkText")){
            by = new By.ByLinkText(param);
        } else if(type.equals("xpath")){
            by = new By.ByXPath(param);
        } else if(type.equals("tagName")){
            by = new By.ByTagName(param);
        } else if(type.equals("partialLinkName")){
            by = new By.ByPartialLinkText(param);
        } else if(type.equals("name")){
            by = new By.ByName(param);
        }
        
    return by;
    }
    
}
