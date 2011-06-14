/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.udp;

import com.wordpress.salaboy.sensor.SensorDataParser;


/**
 *
 * @author esteban
 */
public class GDroidUDPAccelerometerSensorParser implements SensorDataParser  {
    
    @Override
    public double parseData(String data){
        
        if (data.contains(",")){
            //split the data using ',' as a separator
            String[] split = data.split(",");

            //get the last value of the string
            return Double.parseDouble(split[split.length-1])-9.8;
        }
        
        return Double.parseDouble(data);
    }
    
}
