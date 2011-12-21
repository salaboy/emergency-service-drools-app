/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital.model.units;

import com.wordpress.salaboy.hospital.Bed;
import com.wordpress.salaboy.service.helpers.BedHelper;
import java.util.UUID;

/**
 *
 * @author salaboy
 */
public class EmergencyRoom extends AbstractUnit {
    private int numberOfEmergencyBeds;
    
    public EmergencyRoom(int numberOfEmergencyBeds) {
        this.numberOfEmergencyBeds = numberOfEmergencyBeds;
        this.id = UUID.randomUUID().toString();
        this.name = "Emergency Room Unit";
        this.beds = BedHelper.initializeBeds(numberOfEmergencyBeds);
        
    }

    public int getNumberOfEmergencyBeds() {
        return numberOfEmergencyBeds;
    }

    

    public int requestBed() {
        this.numberOfEmergencyBeds--;
        return this.beds.remove(0).getId();
    }

    public void returnBed(int id) {
        this.numberOfEmergencyBeds++;
        this.beds.add(new Bed(id));
    }
    
    
}
