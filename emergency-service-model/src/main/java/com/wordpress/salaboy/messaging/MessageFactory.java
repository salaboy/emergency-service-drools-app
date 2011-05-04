/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSession.QueueQuery;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.integration.transports.netty.TransportConstants;

/**
 *
 * @author salaboy
 */
public class MessageFactory {
    
    public final static String ADDR_NAME = "BIG_BAG";
    
    public static MessageConsumer createMessageConsumer(String consumerId){
        createOnDemandQueue(consumerId);
        return new MessageConsumer(consumerId, createFactory());
    }
    
    public static MessageProducer createMessageProducer(){
        createOnDemandQueue(ADDR_NAME);
        return new MessageProducer(ADDR_NAME, createFactory());
    }
    
    public static void sendMessage(Serializable message) throws HornetQException{
        createMessageProducer().sendMessageAndDie(message);
    }
    
    private static void createOnDemandQueue(String queueName){
        try {
            ClientSession session = createFactory().createSession(true, true);
            
            QueueQuery queueQuery = session.queueQuery(new SimpleString(queueName));
            
            if (!queueQuery.isExists()){
                session.createQueue(ADDR_NAME, queueName);
            }
            session.close();
        } catch (HornetQException ex) {
            throw new IllegalStateException("Error while creating '"+queueName+"' queue",ex);
        }
    }
    
    private static ClientSessionFactory createFactory(){
        Map<String, Object> connectionParams = new HashMap<String, Object>();
        connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);
        TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory", connectionParams);
        return HornetQClient.createClientSessionFactory(transportConfiguration);
    }
}
