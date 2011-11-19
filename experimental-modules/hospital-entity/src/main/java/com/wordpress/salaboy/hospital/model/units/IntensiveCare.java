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
public class IntensiveCare extends AbstractUnit{
    private int availableBeds;

    public IntensiveCare() {
        this.id = UUID.randomUUID().toString();
        this.name = "Intensive Care Unit";
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }
    
    
    
}
