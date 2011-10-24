/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services.util;

import com.wordpress.salaboy.model.events.*;
import com.wordpress.salaboy.model.messages.*;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;

/**
 * Utility class to convert from EmergencyInterchangeMessage to EmergencyEvent
 * @author esteban
 */
public class MessageToEventConverter {
    
    public static EmergencyEvent convertMessageToEvent(EmergencyInterchangeMessage message){
        if (message instanceof VehicleHitsEmergencyMessage){
            VehicleHitsEmergencyMessage realMessage = (VehicleHitsEmergencyMessage)message;
            return new VehicleHitsEmergencyEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getTime());
        }else if (message instanceof VehicleHitsHospitalMessage){
            VehicleHitsHospitalMessage realMessage = (VehicleHitsHospitalMessage)message;
            return new VehicleHitsHospitalEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getHospital().getId(), realMessage.getTime());
        }else if (message instanceof VehicleHitsFireDepartmentMessage){
            VehicleHitsFireDepartmentMessage realMessage = (VehicleHitsFireDepartmentMessage)message;
            return new VehicleHitsFireDepartmentEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getFirefightersDepartment().getId(), realMessage.getTime());
        }else if (message instanceof EmergencyEndsMessage){
            EmergencyEndsMessage realMessage = (EmergencyEndsMessage)message;
            return new EmergencyEndsEvent(realMessage.getEmergencyId(), realMessage.getTime());
        }else if (message instanceof FireTruckOutOfWaterMessage){
            FireTruckOutOfWaterMessage realMessage = (FireTruckOutOfWaterMessage)message;
            return new FireTruckOutOfWaterEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getTime());
        }else if (message instanceof FireExtinctedMessage){
            FireExtinctedMessage realMessage = (FireExtinctedMessage)message;
            return new FireExtinctedEvent(realMessage.getEmergencyId(), realMessage.getTime());
        }else if (message instanceof FireTruckWaterRefilledMessage){
            FireTruckWaterRefilledMessage realMessage = (FireTruckWaterRefilledMessage)message;
            return new FireTruckWaterRefilledEvent(realMessage.getEmergencyId(), realMessage.getVehicleId(), realMessage.getTime());
        }else if (message instanceof HeartBeatMessage){
            HeartBeatMessage realMessage = (HeartBeatMessage)message;
            PulseEvent event = new PulseEvent((int)realMessage.getHeartBeatValue());
            event.setEmergencyId(realMessage.getEmergencyId());
            event.setVehicleId(realMessage.getVehicleId());
            return event;
        }
        
        
        throw new UnsupportedOperationException("Don't know how to convert "+message+" to CallEvent instance");
    }
}
