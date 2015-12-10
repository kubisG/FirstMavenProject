/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

import java.util.LinkedList;
import java.util.Queue;
/**
 *
 * @author Jakub
 */
public class Variable {
    private String varType;
    private String varName;
    private Queue<Value> varValues = new LinkedList<>();
        
    public Variable(String type, String name, Value varValue) {
        this.varType = type;
        this.varName = name;
        this.varValues.add(varValue);
    }

    public Variable(String type, String name) {
        this.varType = type;
        this.varName = name;        
    }

    public Variable(Variable variable) {
        this.varType = variable.getVarType();
        this.varName = variable.getVarName();
        this.varValues = new LinkedList<>(variable.getValues());
    }
    
    public String getVarType() {
        return varType;
    }

    public void setVarType(String varType) {
        this.varType = varType;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    public void setValue(Value value) {
        if(!containsValue(value))
            this.varValues.add(value);
    }

    public Value getValue() {
        return this.varValues.poll();
    }
    
    public Queue<Value> getValues() {
        return this.varValues;
    }
    
    // Vraci true pokud varValues již obsahují hodnotu varValue a navíc tato
    // hodnota byla přiřazena na stejném řádku (tzn. duplicitní hodnota)
    private boolean containsValue(Value varValue) {
        return this.varValues
                .stream()
                .anyMatch(v -> v.getValue().equals(varValue.getValue())
                            && v.getBeginLine() == varValue.getBeginLine());
    }
    
}
