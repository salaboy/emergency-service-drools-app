/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.util.ArrayList;
import java.util.List;
import com.wordpress.salaboy.model.Emergency.EmergencyType;

/**
 *
 * @author salaboy
 */
public class Hospital implements EmergencyEntityBuilding {

    private String id;
    private int availableBeds;
    private List<EmergencyType> specialities;
    private int rank;
    private String name;
    private int x;
    private int y;
    private List<Patient> patients;

    public Hospital() {
    }

    public Hospital(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.patients = new ArrayList<Patient>();
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addPatient(Patient patient) {
        if (patients == null) {
            patients = new ArrayList<Patient>();
        }
        patients.add(patient);
    }

    public void addSpeciality(EmergencyType emergencyType) {
        if (specialities == null) {
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

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int positionX) {
        this.x = positionX;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int positionY) {
        this.y = positionY;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    @Override
    public String toString() {
        return "Hospital{" + "id=" + id + ", availableBeds=" + availableBeds + ", specialities=" + specialities + ", rank=" + rank + ", name=" + name + ", x=" + x + ", y=" + y + ", patients=" + patients + '}';
    }

    @Override
    public EntityBuildingType getType() {
        return EntityBuildingType.HOSPITAL;
    }
}
