/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import java.util.LinkedList;
import java.util.Queue;
import org.eclipse.jetty.util.ArrayQueue;

/**
 *
 * @author Jakub
 */
public class Variable {
    private String type;
    private String name;
    private Queue<String> values = new LinkedList<>();
        
    public Variable(String type, String name, String value) {
        this.type = type;
        this.name = name;
        this.values.add(value);
    }

    public Variable(String type, String name) {
        this.type = type;
        this.name = name;        
    }

    public Variable(Variable variable) {
        this.type = variable.getType();
        this.name = variable.getName();
        this.values = new LinkedList<>(variable.getValues());
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        if(!containsValue(value))
            this.values.add(value);
    }

    public String getValue() {
        return this.values.poll();
    }
    
    public Queue<String> getValues() {
        return this.values;
    }
    private boolean containsValue(String value) {
        return this.values
                .stream()
                .anyMatch(e -> e.equals(value));
    }
}
