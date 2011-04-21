/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.integration.transports.netty.TransportConstants;

/**
 *
 * @author salaboy
 */
public class MessageProducer {
    private ClientSession producerSession;
    private ClientProducer producer;
    private String queueName;
    
    public MessageProducer(String queueName) {
        this.queueName = queueName; 
        try {
            Map<String, Object> connectionParams = new HashMap<String, Object>();
            connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);
            TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory", connectionParams);
            ClientSessionFactory factory = HornetQClient.createClientSessionFactory(transportConfiguration);
            producerSession = factory.createSession();
            producer = producerSession.createProducer(queueName);
            producerSession.start();
        } catch (HornetQException ex) {
            Logger.getLogger(MessageProducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getQueueName() {
        return queueName;
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
    
    public void stop() throws HornetQException{
        producer.close();
        producerSession.stop();
    }
    
}
