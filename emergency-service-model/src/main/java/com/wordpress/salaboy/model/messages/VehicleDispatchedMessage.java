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
public class VehicleDispatchedMessage implements Serializable {
    private Long callId;
    private Long vehicleId;

    public VehicleDispatchedMessage(Long callId, Long vehicleId) {
        this.callId = callId;
        this.vehicleId = vehicleId;
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
    
     
}
