/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author salaboy
 */
public class Emergency implements Serializable {

    public enum EmergencyType {

        FIRE, CAR_CRASH, HEART_ATTACK, ROBBERY
    };
  
    
    private String id;
    private EmergencyType type;
    private Location location;
    private int nroOfPeople;
    private Call call;
    private long processInstanceId;
    
    //this is a map that contains the updates that the emercency will send.
    private Map<String, List<VehicleUpdate>> updates = new HashMap<String, List<VehicleUpdate>>();
    
    private Emergency(Long callId, EmergencyType type, String location, int nroOfPeople, Date date){
        this.type = type;
        this.nroOfPeople = nroOfPeople;
        
    }

    public Emergency() {
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
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

   public void addUpdate(String vehicleId, VehicleUpdate update) {
	  List<VehicleUpdate> currentUpdates = this.updates.get(vehicleId);
	  if (currentUpdates == null) {
		  currentUpdates = new ArrayList<VehicleUpdate>();
		  this.updates.put(vehicleId, currentUpdates);
	  }
	  currentUpdates.add(update);
   }
    
   public List<VehicleUpdate> getUpdatesForVehicle(String vehicleId) {
	   return this.updates.get(vehicleId);
   }
}
