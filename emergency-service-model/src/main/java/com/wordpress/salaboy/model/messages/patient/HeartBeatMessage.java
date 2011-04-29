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
public class HeartBeatMessage implements Serializable{
    private Long callId;
    private Long vehicleId;
    private double heartBeatValue;
    private Date time;

    public HeartBeatMessage(Long callId, Long vehicleId, double heartBeatValue, Date time) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.heartBeatValue = heartBeatValue;
        this.time = time;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
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

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }
    
}
