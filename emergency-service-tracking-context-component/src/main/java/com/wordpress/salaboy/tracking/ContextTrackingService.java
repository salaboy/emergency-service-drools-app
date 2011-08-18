/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.tracking;

/**
 *
 * @author salaboy
 */
public interface ContextTrackingService  {

    public String newCall();

    public String newEmergency();

    public void attachEmergency(String callId, String emergencyId);

    public String newProcedure();

    public void attachProcedure(String emergencyId, String procedureId);

    public String newVehicle();

    public void attachVehicle(String procedureId, String vehicleId);

    public String newServiceChannel();

    public void attachServiceChannel(String emergencyId, String channelId);

    public void detachVehicle(String vehicleId);

    public void detachProcedure(String procedureId);

    public void detachEmergency(String emergencyId);
    
    
    
}
