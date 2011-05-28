/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.wii;

/**
 *
 * @author salaboy
 * This class now represents a bluetooth device but it can be extended to represent
 * different types of devices
 */
public class Device {
    private String id;

    public Device() {
    }

    public Device(String id) {
        this.id = id;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
}
