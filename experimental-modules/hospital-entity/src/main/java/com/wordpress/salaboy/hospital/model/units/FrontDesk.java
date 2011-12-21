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
public class FrontDesk extends AbstractUnit{
    private int activeReceptionists;

    public FrontDesk() {
        this.id = UUID.randomUUID().toString();
        this.name = "Front Desk Unit";
    }

    public int getActiveReceptionists() {
        return activeReceptionists;
    }

    public void setActiveReceptionists(int activeReceptionists) {
        this.activeReceptionists = activeReceptionists;
    }

    public int requestBed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void returnBed(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
   
    
}
