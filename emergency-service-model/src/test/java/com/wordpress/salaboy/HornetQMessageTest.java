/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.messaging.MessageProducer;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.hornetq.api.core.HornetQException;
import org.hornetq.core.server.HornetQServer;
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
public class HornetQMessageTest {

    private HornetQServer server;
    private MessageConsumer consumer;

    public HornetQMessageTest() {
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
        MessageServerSingleton.getInstance().start();

        //Consumer Configuration
        consumer = MessageFactory.createMessageConsumer("IncomingCall");
    }

    @After
    public void tearDown() throws Exception {
        consumer.stop();
        MessageServerSingleton.getInstance().stop();
    }

    @Test
    public void hornetQSimple() throws HornetQException {
        MessageProducer producer = MessageFactory.createMessageProducer();

        String message = "Hello HornetQ!";
        producer.sendMessage(message);
        System.out.println(">> Sending Message: " + message);

        producer.stop();
        
        Object object = consumer.receiveMessage();
        Assert.assertNotNull(object);
        Assert.assertEquals(message, object.toString());

    }
    @Test
    public void messagingWithRulesTest() throws HornetQException {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        String rules = "package org.test;\n"
                + "import com.wordpress.salaboy.messaging.MessageProducer;"
                + "global MessageProducer messageProducer;\n"
                + "rule \"test messageProducer as global\"\n"
                + " when\n"
                + "     eval(true)\n"
                + " then\n"
                + "     messageProducer.sendMessage(\"Hello Message Producer\");\n"
                + "end\n";

        kbuilder.add(new ByteArrayResource(rules.getBytes()), ResourceType.DRL);

        if (kbuilder.getErrors().size() > 0) {
            throw new IllegalStateException("Error = " + kbuilder.getErrors().iterator().next().getMessage());
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        MessageProducer producer = MessageFactory.createMessageProducer();

        ksession.setGlobal("messageProducer", producer);
        
        
        ksession.fireAllRules();
        
        producer.stop();
        
        Object object = consumer.receiveMessage();
        Assert.assertNotNull(object);
        Assert.assertEquals("Hello Message Producer", object.toString());
        
    }
    
  
}