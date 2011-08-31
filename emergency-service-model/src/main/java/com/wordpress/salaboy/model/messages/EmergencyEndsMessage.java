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
public class EmergencyEndsMessage implements Serializable, EmergencyInterchangeMessage {
    private String emergencyId;
    private Date time;
    
    public EmergencyEndsMessage(String emergencyId, Date time) {
        this.emergencyId = emergencyId;
        this.time = time;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

   

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "EmergencyEndsMessage{" + "emergencyid=" + emergencyId + ", time=" + time + '}';
    }

    
  

   
    
}
