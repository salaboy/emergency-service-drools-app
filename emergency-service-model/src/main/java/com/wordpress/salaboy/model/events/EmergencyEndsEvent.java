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
public class EmergencyEndsEvent implements EmergencyEvent, Serializable {
    private String emergencyId;
    
    private Date time;

    public EmergencyEndsEvent() {
    }

    public EmergencyEndsEvent(Date time) {
        this.time = time;
    }
    
    public EmergencyEndsEvent(String emergencyId, Date time) {
        this.emergencyId = emergencyId;
        this.time = time;
    }

    @Override
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

}
