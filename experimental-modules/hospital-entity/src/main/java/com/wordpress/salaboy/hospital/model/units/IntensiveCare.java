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
public class IntensiveCare extends AbstractUnit{
    private int availableBeds;
    
    public IntensiveCare(int availableBeds) {
        this.availableBeds = availableBeds;
        this.id = UUID.randomUUID().toString();
        this.name = "Intensive Care Unit";
        this.beds = BedHelper.initializeBeds(availableBeds);
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public int requestBed() {
        this.availableBeds--;
        return this.beds.remove(0).getId();
    }

    public void returnBed(int id) {
        this.availableBeds++;
        this.beds.add(new Bed(id));
    }
    
    
    
}
