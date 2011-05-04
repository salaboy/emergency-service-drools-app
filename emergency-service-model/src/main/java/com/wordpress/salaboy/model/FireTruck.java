package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class FireTruck implements Vehicle{

    private Long id;
    private String name;
    private Date departureTime;
    private Date emergencyReachedTime;
    private float positionX;
    private float positionY;
    public static AtomicLong incrementalId = new AtomicLong();
    


    public FireTruck(String name, Date departureTime) {
        this.id = FireTruck.incrementalId.getAndIncrement();
        this.name = name;
        this.departureTime = departureTime;
    }

    public FireTruck(String name) {
        this.id = FireTruck.incrementalId.getAndIncrement();
        this.name = name;
    }
    
    
    
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
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

    @Override
    public String toString() {
        return "FireTruck{" + "id=" + id + ", name=" + name + ", departureTime=" + departureTime + ", emergencyReachedTime=" + emergencyReachedTime + ", positionX=" + positionX + ", positionY=" + positionY + '}';
    }

   
    
    
}
