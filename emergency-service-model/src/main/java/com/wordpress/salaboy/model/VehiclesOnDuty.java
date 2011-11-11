/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import com.wordpress.salaboy.model.vehicles.Vehicle;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class VehiclesOnDuty implements Serializable {
    private String emergencyId;
    private List<Vehicle> vehicles;

    public VehiclesOnDuty(String emergencyId, List<Vehicle> vehicles) {
        this.emergencyId = emergencyId;
        this.vehicles = vehicles;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public int size(){
        return this.vehicles.size();
    }
    
    
    
}
