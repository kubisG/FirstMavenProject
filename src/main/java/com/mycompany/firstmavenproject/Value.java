/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.firstmavenproject;

/**
 *
 * @author Jakub
 */
public class Value {
    private String value;
    private int beginLine = 0;
    private int endLine = 0;

    public Value() {
    }

    public Value(String value, int beginLine) {
        this.value = value;
        this.beginLine = beginLine;
    }
    
    public Value(String value, int beginLine, int endLine) {
        this.value = value;
        this.beginLine = beginLine;
        this.endLine = endLine;
    }
    /**
     * @return the beginLine
     */
    public int getBeginLine() {
        return beginLine;
    }

    /**
     * @param beginLine the beginLine to set
     */
    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    /**
     * @return the endLine
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * @param endLine the endLine to set
     */
    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    /**
     * @return the stringValue
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the stringValue to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
}
