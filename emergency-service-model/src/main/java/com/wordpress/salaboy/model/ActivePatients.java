/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class ActivePatients implements Serializable{
    
    private List<Patient> activePatients = new ArrayList<Patient>();
    private Long emergencyId;
    public ActivePatients(Long emergencyId) {
        this.emergencyId = emergencyId;
    }

    public Long getEmergencyId() {
        return emergencyId;
    }
    
    
    public void addPatient(Patient patient){
        if(this.activePatients == null){
            this.activePatients = new ArrayList<Patient>();
        }
        this.activePatients.add(patient);
    }
    
    
    
    public int size(){
        return activePatients.size();
    }

    public List<Patient> getActivePatients() {
        return activePatients;
    }

    public void setActivePatients(List<Patient> activePatients) {
        this.activePatients = activePatients;
    }
    
    public Patient getPatient(Integer index){
        return this.activePatients.get(index);
    }
}
