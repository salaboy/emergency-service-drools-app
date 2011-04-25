/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model.events;

import java.io.Serializable;
import java.util.Date;
import com.wordpress.salaboy.model.Ambulance;

/**
 *
 * @author salaboy
 */
public class PatientPickUpEvent implements Serializable {
    private Long callId;
    private Long vehicleId;
    
    private Date time;
    

    public PatientPickUpEvent() {
    }

    public PatientPickUpEvent(Date time) {
        this.time = time;
    }

    
    
    public PatientPickUpEvent(Long callId, Long vehicleId, Date time) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.time = time;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    
    
    

}
