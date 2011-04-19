/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        ClientMessage clientMessage = producerSession.createMessage(true);
        //clientMessage.getBodyBuffer().writeBytes(IOUtils.toByteArray(object));
        clientMessage.getBodyBuffer().writeString(object.toString());
        producer.send(clientMessage);
    }
    
    public void stop() throws HornetQException{
        producer.close();
        producerSession.stop();
    }
    
}
