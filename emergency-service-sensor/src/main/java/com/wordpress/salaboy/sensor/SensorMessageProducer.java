/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor;

import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;
import com.wordpress.salaboy.sensor.udp.UDPSensorServer;
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
            Long callId = emergencyInformationDataSource.getCallId();
            Long vehicleId = emergencyInformationDataSource.getVehicleId();
            
            if (callId == null){
                Logger.getLogger(UDPSensorServer.class.getName()).log(Level.INFO, "No call id provided!");
                return;
            }
            if (vehicleId == null){
                Logger.getLogger(UDPSensorServer.class.getName()).log(Level.INFO, "No vehicle id provided!");
                return;
            }
            MessageFactory.sendMessage(new HeartBeatMessage(callId, vehicleId, heartBeat, new Date()));
        } catch (HornetQException ex) {
            throw new Exception("Unable to add Heart Beat message into queue!", ex);
        }
    }
        
}
