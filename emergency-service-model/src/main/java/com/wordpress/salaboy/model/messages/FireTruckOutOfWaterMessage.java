/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class FireTruckOutOfWaterMessage implements Serializable ,EmergencyInterchangeMessage{
    private String emergencyId;
    private String vehicleId;
    private Date time;

    public FireTruckOutOfWaterMessage(String emergencyId, String vehicleId, Date time) {
        this.emergencyId = emergencyId;
        this.vehicleId = vehicleId;
        this.time = time;
    }

  
    
    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }
    
    
    
    @Override
    public String toString() {
        return "FireTruckOutOfWaterMessage{" + "emergencyId=" + emergencyId + ", vehicleId=" + vehicleId + ", time=" + time + '}';
    }

   
    
    
     
}
