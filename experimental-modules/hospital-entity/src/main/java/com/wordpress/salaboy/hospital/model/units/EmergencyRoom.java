/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital.model.units;

import java.util.UUID;

/**
 *
 * @author salaboy
 */
public class EmergencyRoom extends AbstractUnit {
    private int numberOfEmergencyBeds;

    public EmergencyRoom() {
        this.id = UUID.randomUUID().toString();
        this.name = "Emergency Room Unit";
    }

    public int getNumberOfEmergencyBeds() {
        return numberOfEmergencyBeds;
    }

    public void setNumberOfEmergencyBeds(int numberOfEmergencyBeds) {
        this.numberOfEmergencyBeds = numberOfEmergencyBeds;
    }
    
    
}
