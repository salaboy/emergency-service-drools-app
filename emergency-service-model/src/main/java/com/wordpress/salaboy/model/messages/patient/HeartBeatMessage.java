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
    private String emergencyId;
    private String vehicleId;
    private double heartBeatValue;
    private Date time;

    public HeartBeatMessage(String emergencyId, String vehicleId, double heartBeatValue, Date time) {
        this.emergencyId = emergencyId;
        this.vehicleId = vehicleId;
        this.heartBeatValue = heartBeatValue;
        this.time = time;
    }

    @Override
    public String getEmergencyId() {
        return emergencyId;
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
        return "HeartBeatMessage{" + "emergencyId=" + emergencyId + ", vehicleId=" + vehicleId + ", heartBeatValue=" + heartBeatValue + ", time=" + time + '}';
    }
    
}
