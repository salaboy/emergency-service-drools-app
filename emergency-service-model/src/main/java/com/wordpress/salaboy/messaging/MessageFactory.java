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
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.netty.TransportConstants;

/**
 *
 * @author salaboy
 */
public class MessageFactory {
    
	private static ServerLocator serverLocator;

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
        	ClientSessionFactory factory = createFactory();
            ClientSession session = factory.createSession(true, true);
            
            QueueQuery queueQuery = session.queueQuery(new SimpleString(queueName));
            
            if (!queueQuery.isExists()){
                session.createQueue(ADDR_NAME, queueName);
            }
            session.close();
            factory.close();
        } catch (HornetQException ex) {
            throw new IllegalStateException("Error while creating '"+queueName+"' queue",ex);
        }
    }
    
    private static ClientSessionFactory createFactory(){
    	try {
			Map<String, Object> connectionParams = new HashMap<String, Object>();
			connectionParams.put(TransportConstants.PORT_PROP_NAME, 8050);
			TransportConfiguration transportConfiguration = new TransportConfiguration(
					"org.hornetq.core.remoting.impl.netty.NettyConnectorFactory",
					connectionParams);
			if (serverLocator == null) {
			serverLocator = HornetQClient
					.createServerLocatorWithoutHA(transportConfiguration);
			}
			ClientSessionFactory factory = serverLocator.createSessionFactory();
			return factory;
		} catch (Exception e) {
			throw new IllegalStateException("Error while creating factory", e);
		}
    }

    public void dispose() {
    	serverLocator.close();
    }
}

