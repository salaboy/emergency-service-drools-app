/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;
import com.wordpress.salaboy.sensor.udp.SensorMessageProducer;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;

/**
 *
 * @author esteban
 */
public class SensorMessageProducer {
    private EmergencyInformationDataSource emergencyInformationDataSource;

    public SensorMessageProducer(EmergencyInformationDataSource emergencyInformationDataSource) {
        this.emergencyInformationDataSource = emergencyInformationDataSource;
    }
    
    //TODO: change double attribute to a SensorMessage object
    public void informMessage(double heartBeat) throws Exception {
        try {
            String callId = emergencyInformationDataSource.getCallId();
            String vehicleId = emergencyInformationDataSource.getVehicleId();
            
            if (callId == null){
                Logger.getLogger(SensorMessageProducer.class.getName()).log(Level.INFO, "No call id provided!");
                return;
            }
            String emergencyId = ContextTrackingProvider.getTrackingService().getEmergencyAttachedToCall(callId);
            
            if (emergencyId == null){
                Logger.getLogger(SensorMessageProducer.class.getName()).log(Level.INFO, "No emergency associated to call id "+callId);
                return;
            }
            
            if (vehicleId == null){
                Logger.getLogger(SensorMessageProducer.class.getName()).log(Level.INFO, "No vehicle id provided!");
                return;
            }
            MessageFactory.sendMessage(new HeartBeatMessage(emergencyId, vehicleId, heartBeat, new Date()));
        } catch (HornetQException ex) {
            throw new Exception("Unable to add Heart Beat message into queue!", ex);
        }
    }
        
}
