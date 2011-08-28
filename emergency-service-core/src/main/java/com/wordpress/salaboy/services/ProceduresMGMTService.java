/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.CallEvent;
import com.wordpress.salaboy.model.events.VehicleHitsHospitalEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;
import com.wordpress.salaboy.model.messages.EmergencyInterchangeMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsHospitalMessage;
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
    //private Map<String, List<ProcedureService>> proceduresByCall;
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

    public void newRequestedProcedure(final String emergencyId, String procedureName, Map<String, Object> parameters) {
        
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
     * @param message
     * @return 
     */
    public void notifyProcedures(EmergencyInterchangeMessage message){
        
        String emergencyId = message.getEmergencyId();
        
        //convert from Message to CallEvent
        CallEvent event = this.convertMessageToEvent(message);
        
        //notify each of the processes involved in the call
        for (ProcedureService procedureService : this.proceduresByEmergency.get(emergencyId)) {
            
            //TODO: change all these logic to something that doesn't hurts my eyes :)
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
                }
            }
        }
        
    }
    
    private CallEvent convertMessageToEvent(EmergencyInterchangeMessage message){
        if (message instanceof VehicleHitsEmergencyMessage){
            VehicleHitsEmergencyMessage realMessage = (VehicleHitsEmergencyMessage)message;
            return new VehicleHitsEmergencyEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getTime());
        }else if (message instanceof VehicleHitsHospitalMessage){
            VehicleHitsHospitalMessage realMessage = (VehicleHitsHospitalMessage)message;
            return new VehicleHitsHospitalEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getHospital().getId(), realMessage.getTime());
        }
        
        throw new UnsupportedOperationException("Don't know how to convert "+message+" to CallEvent instance");
    }
}
