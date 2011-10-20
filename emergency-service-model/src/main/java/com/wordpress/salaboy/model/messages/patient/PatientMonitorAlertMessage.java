/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages.patient;

import com.wordpress.salaboy.model.messages.EmergencyInterchangeMessage;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author esteban
 */
public class PatientMonitorAlertMessage implements Serializable, EmergencyInterchangeMessage {
    private String callId;
    private String vehicleId;
    private String message;
    private Date time;

    public PatientMonitorAlertMessage(String callId, String vehicleId, String message, Date time) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.time = time;
        this.message = message;
    }

    public String getEmergencyId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PatientMonitorAlertMessage{" + "callId=" + callId + ", vehicleId=" + vehicleId + ", message=" + message + ", time=" + time + '}';
    }
    
    
    
}
