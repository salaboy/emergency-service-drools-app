/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author salaboy
 */
public class ProceduresMGMTService {

    private static ProceduresMGMTService instance;
    private Map<String, List<ProcedureService>> proceduresByEmergency;
    private ProceduresMGMTService() {
        proceduresByEmergency = new HashMap<String, List<ProcedureService>>();

    }

    public static ProceduresMGMTService getInstance() {
        if (instance == null) {
            instance = new ProceduresMGMTService();
        }
        return instance;
    }
    
    public static void clear(){
        instance = null;
    }

    public void newRequestedProcedure(final String emergencyId, String procedureName, Map<String, Object> parameters) throws IOException {
        
        if (!proceduresByEmergency.containsKey(emergencyId)){
            proceduresByEmergency.put(emergencyId, new ArrayList<ProcedureService>());
        }
        
        List<ProcedureService> procedures = proceduresByEmergency.get(emergencyId);
        procedures.add(ProcedureServiceFactory.createProcedureService(emergencyId, procedureName, parameters));

    }
 
    /**
     * Notifies all procedures of an emergency about a particular message.
     * The emergency is taken from {@link EmergencyInterchangeMessage#getCallId()}
     * Here is where message -> event -> service mapping is created
     * @param callId
     * @param event
     * @return 
     */
    public void notifyProcedures(EmergencyEvent event){
        
        String emergencyId = event.getEmergencyId();
        
        if (!this.proceduresByEmergency.containsKey(emergencyId)){
            throw new IllegalStateException("Unknown emergency "+emergencyId);
        }
        
        System.out.printf("Notify procedures about %s\n",event);
        System.out.printf("Procedures registered to emergency '%s': %s \n", emergencyId, this.proceduresByEmergency.get(emergencyId).size());
        
        //notify each of the processes involved in the call
        for (ProcedureService procedureService : this.proceduresByEmergency.get(emergencyId)) {
            
            //Emergency Ends event has the same behaviour for all procedures
            if (event instanceof EmergencyEndsEvent){
                procedureService.procedureEndsNotification((EmergencyEndsEvent)event);
                continue;
            }
            
            //TODO: change all these logic to something that doesn't hurt my eyes :)
            if (procedureService instanceof DefaultHeartAttackProcedure){
                DefaultHeartAttackProcedure heartAttackProcedure = (DefaultHeartAttackProcedure)procedureService;
                if (event instanceof VehicleHitsEmergencyEvent){
                    heartAttackProcedure.patientPickUpNotification((VehicleHitsEmergencyEvent)event);
                }else if( event instanceof VehicleHitsHospitalEvent){
                    heartAttackProcedure.patientAtHospitalNotification((VehicleHitsHospitalEvent)event);
                }
            }else if (procedureService instanceof DefaultFireProcedure){
                DefaultFireProcedure fireProcedure = (DefaultFireProcedure)procedureService;
                if (event instanceof VehicleHitsEmergencyEvent){
                    fireProcedure.vehicleReachesEmergencyNotification((VehicleHitsEmergencyEvent)event);
                }else if (event instanceof FireTruckOutOfWaterEvent){
                    fireProcedure.fireTruckOutOfWaterNotification((FireTruckOutOfWaterEvent)event);
                }else if (event instanceof FireExtinctedEvent){
                    fireProcedure.fireExtinctedNotification((FireExtinctedEvent)event);
                }
            }
        }
        
    }
    
}
