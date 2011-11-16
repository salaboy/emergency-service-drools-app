/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.hospital;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Hospital {

    private String id;
    private int availableBeds;
    private List<String> specialities;
    private int rank;
    private String name;
    private int x;
    private int y;
    

    public Hospital() {
    }

    public Hospital(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

  

    public void addSpeciality(String emergencyType) {
        if (specialities == null) {
            specialities = new ArrayList<String>();
        }
        specialities.add(emergencyType);
    }

    public List<String> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<String> specialities) {
        this.specialities = specialities;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public int getX() {
        return x;
    }

    public void setX(int positionX) {
        this.x = positionX;
    }

    
    public int getY() {
        return y;
    }

    public void setY(int positionY) {
        this.y = positionY;
    }

    

    

  
}
