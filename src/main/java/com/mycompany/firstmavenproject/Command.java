/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import java.util.Deque;

/**
 *
 * @author Jakub
 */
public class Command {
    private final Deque<String> methodNames;
    private final Deque params;

    public Command(Deque<String> methodNames, Deque<String> params) {
        this.methodNames =  methodNames;
        this.params = params;
    }
    
    public Deque<String> getMethodNames() {
        return methodNames;
    }

    public Deque<String> getParams() {
        return params;
    }
    
    public int getCount() {
        return methodNames.size();
    }
}
