/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;

/**
 *
 * @author salaboy
 */
public class MessageProducer {
    private ClientSession producerSession;
    private ClientProducer producer;
    private String addressName;
    
    public MessageProducer(String addressName, ClientSessionFactory factory) {
        this.addressName = addressName; 
        try {
            producerSession = factory.createSession(true,true);
            producer = producerSession.createProducer(addressName);
            producerSession.start();
        } catch (HornetQException ex) {
            Logger.getLogger(MessageProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getQueueName() {
        return addressName;
    }
    
    public void sendMessage(Object object) throws HornetQException{
        ObjectOutputStream oos = null;
        try {
            
            ClientMessage clientMessage = producerSession.createMessage(true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.flush();
            
            byte[] data = baos.toByteArray();
            
            clientMessage.getBodyBuffer().writeInt(data.length);
            clientMessage.getBodyBuffer().writeBytes(data);
            producer.send(clientMessage);
            
        } catch (IOException ex) {
            Logger.getLogger(MessageProducer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(MessageProducer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void sendMessageAndDie(Object object) throws HornetQException{
        try{
            this.sendMessage(object);
        } finally{
            this.stop();
        }
    }
    
    public void stop() throws HornetQException{
        producer.close();
        producerSession.stop();
        producerSession.close();
    }
    
}
