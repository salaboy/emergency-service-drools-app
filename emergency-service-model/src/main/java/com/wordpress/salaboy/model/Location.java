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
    private Integer locationX;
    private Integer locationY;

    public Location() {
    }

    
    public Location(Integer locationX, Integer locationY) {
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public Integer getLocationX() {
        return locationX;
    }

    public void setLocationX(Integer locationX) {
        this.locationX = locationX;
    }

    public Integer getLocationY() {
        return locationY;
    }

    public void setLocationY(Integer locationY) {
        this.locationY = locationY;
    }

    @Override
    public String toString() {
        return "Location{" + "locationX=" + locationX + ", locationY=" + locationY + '}';
    }

   
    
}
