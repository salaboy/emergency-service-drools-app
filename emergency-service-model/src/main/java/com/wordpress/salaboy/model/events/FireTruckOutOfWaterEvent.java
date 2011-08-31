/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model.events;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class FireTruckOutOfWaterEvent implements EmergencyEvent, Serializable {
    private String emergencyId;
    private String vehicleId;
    
    private Date time;

    public FireTruckOutOfWaterEvent() {
    }

    public FireTruckOutOfWaterEvent(Date time) {
        this.time = time;
    }
    
    public FireTruckOutOfWaterEvent(String emergencyId, String vehicleId, Date time) {
        this.emergencyId = emergencyId;
        this.vehicleId = vehicleId;
        this.time = time;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
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

    
    
    

}
