package com.wordpress.salaboy.model;

import java.util.Date;

/**
 *
 * @author salaboy
 */
public class FireTruck implements Vehicle {

    private String id;
    private String name;
    private Date departureTime;
    private Date emergencyReachedTime;
    private float positionX;
    private float positionY;
    private int tankSize;
    private int tankLevel;
    
    public FireTruck() {
    }

    public FireTruck(String name, Date departureTime) {
        this.name = name;
        this.departureTime = departureTime;
    }

    public FireTruck(String name) {

        this.name = name;
    }

    public FireTruck(String name, int tankSize, int tankLevel) {
        this.name = name;
        this.tankSize = tankSize;
        this.tankLevel = tankLevel;
    }
    
    

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setPatientPickedUpTime(Date patientPickedUpTime) {
        this.emergencyReachedTime = patientPickedUpTime;
    }

    public Date getPatientPickedUpTime() {
        return emergencyReachedTime;
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

    public int getTankSize() {
        return tankSize;
    }

    public void setTankSize(int tankSize) {
        this.tankSize = tankSize;
    }

    public int getTankLevel() {
        return tankLevel;
    }

    public void setTankLevel(int tankLevel) {
        this.tankLevel = tankLevel;
    }

    public Date getEmergencyReachedTime() {
        return emergencyReachedTime;
    }

    public void setEmergencyReachedTime(Date emergencyReachedTime) {
        this.emergencyReachedTime = emergencyReachedTime;
    }

    @Override
    public String toString() {
        return "FireTruck{" + "id=" + id + ", name=" + name + ", departureTime=" + departureTime + ", emergencyReachedTime=" + emergencyReachedTime + ", positionX=" + positionX + ", positionY=" + positionY + ", tankSize=" + tankSize + ", tankLevel=" + tankLevel + '}';
    }
    
    

}
