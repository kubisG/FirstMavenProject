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
public interface ITestParser {
    public Deque<Command> parseTest(String testName);   
}
