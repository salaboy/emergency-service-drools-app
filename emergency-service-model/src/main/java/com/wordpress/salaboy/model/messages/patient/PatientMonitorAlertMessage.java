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
    private String emergencyId;
    private String vehicleId;
    private String message;
    private Date time;

    public PatientMonitorAlertMessage(String emergencyId, String vehicleId, String message, Date time) {
        this.emergencyId = emergencyId;
        this.vehicleId = vehicleId;
        this.time = time;
        this.message = message;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
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
        return "PatientMonitorAlertMessage{" + "emergencyId=" + emergencyId + ", vehicleId=" + vehicleId + ", message=" + message + ", time=" + time + '}';
    }
    
    
    
}
