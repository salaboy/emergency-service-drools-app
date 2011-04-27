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
public class PoliceCar implements Vehicle{

    private Long id;
    private String description;
   
    private Date departureTime;
    private Date emergencyReachedTime;
    private float positionX;
    private float positionY;
    public static AtomicLong incrementalId = new AtomicLong();
    


    public PoliceCar(String description, Date departureTime) {
        this.id = PoliceCar.incrementalId.getAndIncrement();
        this.description = description;
        this.departureTime = departureTime;
    }

    public PoliceCar(String description) {
        this.id = PoliceCar.incrementalId.getAndIncrement();
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

    public Date getEmergencyReachedTime() {
        return emergencyReachedTime;
    }

    public void setEmergencyReachedTime(Date emergencyReachedTime) {
        this.emergencyReachedTime = emergencyReachedTime;
    }
    
    

    @Override
    public String toString() {
        return "PoliceCar{" + "id=" + id + ", description=" + description + ", departureTime=" + departureTime + ", emergencyReachedTime=" + emergencyReachedTime + ", positionX=" + positionX + ", positionY=" + positionY + '}';
    }

   

    
    
}
