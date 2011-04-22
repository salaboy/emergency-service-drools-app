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
public class Location implements Serializable{
    private String locationX;
    private String locationY;

    public Location() {
    }

    
    public Location(String locationX, String locationY) {
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public String getLocationX() {
        return locationX;
    }

    public void setLocationX(String locationX) {
        this.locationX = locationX;
    }

    public String getLocationY() {
        return locationY;
    }

    public void setLocationY(String locationY) {
        this.locationY = locationY;
    }

    @Override
    public String toString() {
        return "Location{" + "locationX=" + locationX + ", locationY=" + locationY + '}';
    }

   
    
}
