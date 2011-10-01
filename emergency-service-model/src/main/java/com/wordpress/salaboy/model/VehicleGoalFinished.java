/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class VehicleGoalFinished implements Serializable{
    private String emergencyId;

    public VehicleGoalFinished(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    @Override
    public String toString() {
        return "VehicleGoalFinished{" + "emergencyId=" + emergencyId + '}';
    }
    
    
}
