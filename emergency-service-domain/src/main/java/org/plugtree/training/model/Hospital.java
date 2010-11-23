/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.plugtree.training.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.plugtree.training.model.Emergency.EmergencyType;

/**
 *
 * @author salaboy
 */
public class Hospital implements Serializable{
    private Long id;
    private int availableBeds;
    private List<EmergencyType> specialities;
    private int rank;
    private String name;
    public Hospital() {
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addSpeciality(EmergencyType emergencyType){
        if(specialities == null){
            specialities = new ArrayList<EmergencyType>();
        }
        specialities.add(emergencyType);
    }

    public List<EmergencyType> getSpecialities() {
        return specialities;
    }

    public void setSpecialities(List<EmergencyType> specialities) {
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
    

}
