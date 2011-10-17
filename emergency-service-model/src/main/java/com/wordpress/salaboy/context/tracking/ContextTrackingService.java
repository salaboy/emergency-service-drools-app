/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.context.tracking;

import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author salaboy
 */
public interface ContextTrackingService  {

    //Creation
    public String newCallId();
    
    public String newEmergencyId();
    
    public String newProcedureId();
    
    public String newVehicleId();
    
    public String newServiceChannelId();
    
    public String newPatientId();
    
    public String newEmergencyEntityBuildingId();
    
    
    
    //Attach
    public void attachEmergency(String callId, String emergencyId);

    public void attachProcedure(String emergencyId, String procedureId);

    public void attachVehicle(String procedureId, String vehicleId);
    
    public void attachProcedures(String parentProcedureId, String childProcedureId);

    public void attachServiceChannel(String emergencyId, String channelId);

    
    //Detach
    public void detachVehicle(String vehicleId);

    public void detachProcedure(String procedureId);

    public void detachEmergency(String emergencyId);
    
    public void detachServiceChannel(String serviceChannelId);
    
    public void detachPatient(String patientId);
    
    public void detachEmergencyEntityBuilding(String entityBuildingId);

    //Helper
    
    public GraphDatabaseService getGraphDb();
    
    //Query
    public String getProcedureAttachedToVehicle(String vehicleId);
    
    public String getCallAttachedToEmergency(String emergencyId);
            
    public String getEmergencyAttachedToCall(String callId);
    
    public void clear();
    
}
