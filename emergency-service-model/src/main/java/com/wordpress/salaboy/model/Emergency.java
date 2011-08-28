/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author salaboy
 */
public class Emergency implements Serializable {

    public enum EmergencyType {

        FIRE, CAR_CRASH, HEART_ATTACK, ROBBERY
    };
   // public static AtomicLong incrementalId = new AtomicLong();
    
    private String id;
    private EmergencyType type;
    private Location location;
    private int nroOfPeople;
    private Call call;
    private long processInstanceId;
    
    
    private Emergency(String id, Long callId, EmergencyType type, String location, int nroOfPeople, Date date){
      //  this.id = id;
        this.type = type;
        this.nroOfPeople = nroOfPeople;
        
    }

    public Emergency() {
      //  this.id = Emergency.incrementalId.getAndIncrement();
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    

    public Emergency(String id){
        this.id = id;
    }
    public void setNroOfPeople(int nroOfPeople) {
        this.nroOfPeople = nroOfPeople;
    }

    public int getNroOfPeople() {
        return nroOfPeople;
    }
    
  
    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EmergencyType getType() {
        return type;
    }

    public void setType(EmergencyType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = EmergencyType.valueOf(type);
    }

    @Override
    public String toString() {
        return "Emergency{" + "id=" + id + ", type=" + type + ", location=" + location + ", nroOfPeople=" + nroOfPeople + ", call=" + call + ", processInstanceId=" + processInstanceId + '}';
    }

   
    
    
}
