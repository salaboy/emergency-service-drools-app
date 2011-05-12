/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor;

/**
 *
 * @author esteban
 */
public interface SensorDataParser {
    
    //TODO this method should return a SensorMessage
    public double parseData(String data);
}
