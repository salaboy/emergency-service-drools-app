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
    private String description;
    private Date departureTime;
    private Date emergencyReachedTime;
    private float positionX;
    private float positionY;
    public static AtomicLong incrementalId = new AtomicLong();
    


    public FireTruck(String description, Date departureTime) {
        this.id = FireTruck.incrementalId.getAndIncrement();
        this.description = description;
        this.departureTime = departureTime;
    }

    public FireTruck(String description) {
        this.id = FireTruck.incrementalId.getAndIncrement();
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "FireTruck{" + "id=" + id + ", description=" + description + ", departureTime=" + departureTime + ", emergencyReachedTime=" + emergencyReachedTime + ", positionX=" + positionX + ", positionY=" + positionY + '}';
    }

   
    
    
}
