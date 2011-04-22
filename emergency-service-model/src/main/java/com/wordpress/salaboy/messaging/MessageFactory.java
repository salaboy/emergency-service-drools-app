/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.messaging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    
    
    public static MessageConsumer createMessageConsumer(String queueName){
        createOnDemandQueue(queueName);
        return new MessageConsumer(queueName, createFactory());
    }
    
    public static MessageProducer createMessageProducer(String queueName){
        createOnDemandQueue(queueName);
        return new MessageProducer(queueName, createFactory());
    }
    
    private static void createOnDemandQueue(String queueName){
        try {
            ClientSession session = createFactory().createSession();
            
            QueueQuery queueQuery = session.queueQuery(new SimpleString(queueName));
            
            if (!queueQuery.isExists()){
                session.createQueue(queueName, queueName);
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
