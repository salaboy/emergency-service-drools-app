/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.CallEvent;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.model.events.FireTruckOutOfWaterEvent;
import com.wordpress.salaboy.model.events.VehicleHitsHospitalEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;
import com.wordpress.salaboy.model.messages.EmergencyEndsMessage;
import com.wordpress.salaboy.model.messages.EmergencyInterchangeMessage;
import com.wordpress.salaboy.model.messages.FireTruckOutOfWaterMessage;
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
    private Map<Long, List<ProcedureService>> proceduresByCall;

    private ProceduresMGMTService() {
        proceduresByCall = new HashMap<Long, List<ProcedureService>>();

    }

    public static ProceduresMGMTService getInstance() {
        if (instance == null) {
            instance = new ProceduresMGMTService();
        }
        return instance;
    }

    public void newRequestedProcedure(final Long callId, String procedureName, Map<String, Object> parameters) {
        
        if (!proceduresByCall.containsKey(callId)){
            proceduresByCall.put(callId, new ArrayList<ProcedureService>());
        }
        
        List<ProcedureService> procedures = proceduresByCall.get(callId);
        procedures.add(ProcedureServiceFactory.createProcedureService(callId, procedureName, parameters));

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
        
        Long callId = message.getCallId();
        
        //convert from Message to CallEvent
        CallEvent event = this.convertMessageToEvent(message);
        
        //notify each of the processes involved in the call
        for (ProcedureService procedureService : this.proceduresByCall.get(callId)) {
            
            //Emergency Ends event has the same behaviour for all procedures
            if (event instanceof EmergencyEndsEvent){
                procedureService.procedureEndsNotification((EmergencyEndsEvent)event);
                continue;
            }
            
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
                }else if (event instanceof FireTruckOutOfWaterEvent){
                    fireProcedure.fireTruckOutOfWaterNotification((FireTruckOutOfWaterEvent)event);
                }
            }
        }
        
    }
    
    private CallEvent convertMessageToEvent(EmergencyInterchangeMessage message){
        if (message instanceof VehicleHitsEmergencyMessage){
            VehicleHitsEmergencyMessage realMessage = (VehicleHitsEmergencyMessage)message;
            return new VehicleHitsEmergencyEvent(realMessage.getCallId(), realMessage.getVehicleId(), realMessage.getTime());
        }else if (message instanceof VehicleHitsHospitalMessage){
            VehicleHitsHospitalMessage realMessage = (VehicleHitsHospitalMessage)message;
            return new VehicleHitsHospitalEvent(realMessage.getCallId(), realMessage.getVehicleId(), realMessage.getHospital().getId(), realMessage.getTime());
        }else if (message instanceof EmergencyEndsMessage){
            EmergencyEndsMessage realMessage = (EmergencyEndsMessage)message;
            return new EmergencyEndsEvent(realMessage.getCallId(), realMessage.getTime());
        }else if (message instanceof FireTruckOutOfWaterMessage){
            FireTruckOutOfWaterMessage realMessage = (FireTruckOutOfWaterMessage)message;
            return new FireTruckOutOfWaterEvent(realMessage.getCallId(), realMessage.getVehicleId(), realMessage.getTime());
        }
        
        throw new UnsupportedOperationException("Don't know how to convert "+message+" to CallEvent instance");
    }
}
