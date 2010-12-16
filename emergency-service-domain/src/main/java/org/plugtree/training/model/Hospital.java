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
    private static Long lastId = 0l;
    private Long id;
    private int availableBeds;
    private List<EmergencyType> specialities;
    private int rank;
    private String name;
    private float positionX;
    private float positionY;
    private List<Patient> patients;
    
//    // graphicable 
//    private transient Animation animation;
//    private transient Polygon polygon;
    
    public Hospital() {
    }

    public Hospital(String name, float positionX, float positionY) {
        this.id = lastId++;
        this.name = name;
        this.positionX = positionX;
        this.positionY = positionY;
        this.patients = new ArrayList<Patient>();
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

    public void addPatient(Patient patient){
        if(patients == null){
            patients = new ArrayList<Patient>();
        }
        patients.add(patient);
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

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
    
    
    
    @Override
    public String toString() {
        return "Hospital{" + "id=" + id + ", availableBeds=" + availableBeds + ", specialities=" + specialities + ", rank=" + rank + ", name=" + name + ", X = "+positionX + ", Y =" +positionY+"}";
    }

//    public Animation getAnimation() {
//        return this.animation;
//    }
//
//    public Polygon getPolygon() {
//        return this.polygon;
//    }
//
//    public void setAnimation(Animation animation) {
//        this.animation = animation;
//    }
//
//    public void setPolygon(Polygon polygon) {
//        this.polygon = polygon;
//    }

    
    
    
}
