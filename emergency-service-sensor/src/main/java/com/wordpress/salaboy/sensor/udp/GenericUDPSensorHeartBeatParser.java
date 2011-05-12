/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.udp;

import com.wordpress.salaboy.sensor.SensorHeartBeatParser;


/**
 *
 * @author esteban
 */
public class GenericUDPSensorHeartBeatParser implements SensorHeartBeatParser  {
    
    @Override
    public double getHeartBeatValue(String data){
        
        //split the data using ',' as a separator
        String[] split = data.split(",");
       
        //get the last value of the string
        return Double.parseDouble(split[split.length-1]);
    }
    
}
