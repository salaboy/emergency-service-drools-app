/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.grid;

import java.util.List;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.drools.grid.SocketService;
import java.util.HashMap;
import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.messaging.MessageConsumerFactory;
import com.wordpress.salaboy.messaging.MessageProducer;
import com.wordpress.salaboy.messaging.MessageProducerFactory;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.services.PhoneCallsMGMTService;
import java.util.Date;
import org.hornetq.api.core.HornetQException;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author salaboy
 */
public class PhoneCallsMGMTServiceTest extends GridBaseTest{

    private MessageConsumer consumer;
    private TaskClient client;
    public PhoneCallsMGMTServiceTest() {
    
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        MessageServerSingleton.getInstance().start();
        consumer = MessageConsumerFactory.createMessageConsumer("phoneCalls");
        
        //Start one task server
        HumanTaskHelper.getInstance().taskServerStart();
        
        
        this.coreServicesMap = new HashMap();
        createRemoteNode();
        
        client =  HumanTaskHelper.getInstance().initTaskClient();

    }

    @After
    public void tearDown() throws Exception {
        MessageServerSingleton.getInstance().stop();
        if(remoteN1 != null){
            remoteN1.dispose();
        }
        if(grid1 != null){
            grid1.get(SocketService.class).close();
        }
        
        HumanTaskHelper.getInstance().taskServerStop();
    }

    @Test
    public void phoneCallsMGMTServiceTest() throws HornetQException, InterruptedException{
        MessageProducer producer = MessageProducerFactory.createMessageProducer("phoneCalls");
        producer.sendMessage(new Call(1,2,new Date()));
        producer.stop();
        
        Call call = (Call) consumer.receiveMessage();
        assertNotNull(call);
        
        PhoneCallsMGMTService.getInstance().newPhoneCall(call);
        
        Thread.sleep(1000);
        
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("operator", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());
        
        
        
    }

}