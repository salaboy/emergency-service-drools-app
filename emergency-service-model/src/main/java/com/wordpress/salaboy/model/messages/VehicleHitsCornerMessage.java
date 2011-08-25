/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class VehicleHitsCornerMessage implements Serializable, EmergencyInterchangeMessage {
    private String callId;
    private Long vehicleId;
    private int cornerX;
    private int cornery;

    public VehicleHitsCornerMessage(String callId, Long vehicleId, int cornerX, int cornery) {
        this.callId = callId;
        this.vehicleId = vehicleId;
        this.cornerX = cornerX;
        this.cornery = cornery;
    }
    
    
    public int getCornerX() {
        return cornerX;
    }

    public void setCornerX(int cornerX) {
        this.cornerX = cornerX;
    }

    public int getCornerY() {
        return cornery;
    }

    public void setCornery(int cornery) {
        this.cornery = cornery;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    @Override
    public String toString() {
        return "VehicleHitsCornerMessage{" + "callId=" + callId + ", vehicleId=" + vehicleId + ", cornerX=" + cornerX + ", cornery=" + cornery + '}';
    }
     
    
}
