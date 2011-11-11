package com.wordpress.salaboy.model.vehicles;

import java.util.Date;

/**
 *
 * @author salaboy
 *
 * This entity represent an Ambulance vehicle
 */
public class Ambulance implements Vehicle {

    private String id;
    private String name;
    private float positionX;
    private float positionY;

    public Ambulance() {
    }

    public Ambulance(String name) {
        this.name = name;
    }

    public Ambulance(String name, Date departureTime) {
        this.name = name;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public float getPositionX() {
        return positionX;
    }

    @Override
    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    @Override
    public float getPositionY() {
        return positionY;
    }

    @Override
    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ambulance{" + "id=" + id + ", name=" + name + ", positionX=" + positionX + ", positionY=" + positionY + '}';
    }

   

    
}
