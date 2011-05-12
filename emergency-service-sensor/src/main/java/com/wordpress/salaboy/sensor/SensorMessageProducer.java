/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor;

import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;
import java.util.Date;
import org.hornetq.api.core.HornetQException;

/**
 *
 * @author esteban
 */
public class SensorMessageProducer {

    //TODO: change double attribute to a SensorMessage object
    public void informMessage(double heartBeat) throws Exception {
        try {
            MessageFactory.sendMessage(new HeartBeatMessage(0L, 0L, heartBeat, new Date()));
        } catch (HornetQException ex) {
            throw new Exception("Unable to add Heart Beat message into queue!", ex);
        }
    }
}
