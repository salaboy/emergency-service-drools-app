/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages.patient;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class PatientMonitorAlertMessage implements Serializable {
    private Long callId;
    private Long vehicleId;
    private String message;
    private Date time;

    public PatientMonitorAlertMessage(Long callId, Long vehicleId, Date time, String message) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.time = time;
        this.message = message;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
