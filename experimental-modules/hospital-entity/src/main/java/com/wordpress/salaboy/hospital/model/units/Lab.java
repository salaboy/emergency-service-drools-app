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
public class Lab extends AbstractUnit{
    private int activeLabTests;

    public Lab() {
        this.id = UUID.randomUUID().toString();
        this.name = "Lab Unit";
    }

    public int getActiveLabTests() {
        return activeLabTests;
    }

    public void setActiveLabTests(int activeLabTests) {
        this.activeLabTests = activeLabTests;
    }
    
    
}
