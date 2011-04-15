/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.integration.transports.netty.NettyAcceptorFactory;
import org.hornetq.integration.transports.netty.TransportConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public class HornetQMessageTests {

    private HornetQServer server;
    private ClientSession producerSession;
    private ClientSession consumerSession;
    private ClientProducer producer;
    private ClientConsumer consumer;

    public HornetQMessageTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        //Server Configuration
        Configuration configuration = new ConfigurationImpl();
        configuration.setPersistenceEnabled(false);
        configuration.setSecurityEnabled(false);

        Map<String, Object> connectionParams = new HashMap<String, Object>();
        connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);

        TransportConfiguration transpConf = new TransportConfiguration(NettyAcceptorFactory.class.getName(), connectionParams);

        HashSet<TransportConfiguration> setTransp = new HashSet<TransportConfiguration>();
        setTransp.add(transpConf);

        configuration.setAcceptorConfigurations(setTransp);

        server = HornetQServers.newHornetQServer(configuration);

        server.start();

        System.out.println(">> HornetQ Server Started!");
        
        //Consumer Configuration
        TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory", connectionParams);
        ClientSessionFactory factory = HornetQClient.createClientSessionFactory(transportConfiguration);
        consumerSession = factory.createSession();
        consumerSession.createQueue("events", "events", true);
        consumer = consumerSession.createConsumer("events");
        consumerSession.start();
    }

    @After
    public void tearDown() throws Exception {
        consumerSession.stop();
        producerSession.stop();
        consumer.close();
        producer.close();
        server.stop();
    }

    @Test
    public void hornetQSimple() throws HornetQException {
        //Initialize Client
        Map<String, Object> connectionParams = new HashMap<String, Object>();
        connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);
        TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory", connectionParams);
        ClientSessionFactory factory = HornetQClient.createClientSessionFactory(transportConfiguration);
        producerSession = factory.createSession();
        producer = producerSession.createProducer("events");
        producerSession.start();
        
        String message = "Hello HornetQ!";
        ClientMessage clientMessage = producerSession.createMessage(true);
        clientMessage.getBodyBuffer().writeString(message);
        producer.send(clientMessage);
        System.out.println(">> Sending Message: " + message);

        ClientMessage msgReceived = consumer.receive();
        Assert.assertNotNull(msgReceived);
        Assert.assertEquals(message, msgReceived.getBodyBuffer().readString());

    }
}