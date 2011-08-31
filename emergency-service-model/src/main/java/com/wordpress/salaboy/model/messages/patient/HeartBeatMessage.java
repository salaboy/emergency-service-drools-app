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
public class HeartBeatMessage implements Serializable, EmergencyInterchangeMessage{
    private String callId;
    private String vehicleId;
    private double heartBeatValue;
    private Date time;

    public HeartBeatMessage(String callId, String vehicleId, double heartBeatValue, Date time) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.heartBeatValue = heartBeatValue;
        this.time = time;
    }

    @Override
    public String getEmergencyId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public double getHeartBeatValue() {
        return heartBeatValue;
    }

    public void setHeartBeatValue(double heartBeatValue) {
        this.heartBeatValue = heartBeatValue;
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

    @Override
    public String toString() {
        return "HeartBeatMessage{" + "callId=" + callId + ", vehicleId=" + vehicleId + ", heartBeatValue=" + heartBeatValue + ", time=" + time + '}';
    }
    
}
