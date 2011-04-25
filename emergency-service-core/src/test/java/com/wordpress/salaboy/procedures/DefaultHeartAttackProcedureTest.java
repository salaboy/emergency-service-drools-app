/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.services.HumanTaskServerService;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import com.wordpress.salaboy.grid.*;
import java.util.List;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.drools.grid.SocketService;
import java.util.HashMap;
import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import org.hornetq.api.core.HornetQException;
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
public class DefaultHeartAttackProcedureTest extends GridBaseTest{

    private MessageConsumer consumer;
    private TaskClient client;
    public DefaultHeartAttackProcedureTest() {
    
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
       // MessageServerSingleton.getInstance().start();
      //  consumer = MessageFactory.createMessageConsumer("phoneCalls");
        
//        //Start one task server
//        HumanTaskServerService.getInstance().initTaskServer();
//        
//        
//        this.coreServicesMap = new HashMap();
//        createRemoteNode();
//        
//        client =  HumanTaskServerService.getInstance().initTaskClient("client DefaultHeartAttackProcedureTest");

    }

    @After
    public void tearDown() throws Exception {
       // MessageServerSingleton.getInstance().stop();
//        
//        if(remoteN1 != null){
//            remoteN1.dispose();
//        }
//        if(grid1 != null){
//            grid1.get(SocketService.class).close();
//        }
//        
//        HumanTaskServerService.getInstance().stopTaskServer();
    }

    @Test
    public void defaultHeartAttackSimpleTest() throws HornetQException, InterruptedException{
//        MessageProducer producer = MessageFactory.createMessageProducer("phoneCalls");
//        producer.sendMessage(new Call(1,2,new Date()));
//        producer.stop();
//        
//        Call call = (Call) consumer.receiveMessage();
//        assertNotNull(call);
//        
//        ProceduresMGMTService.getInstance().newDefaultHeartAttackProcedure(1L);
//        
//        Thread.sleep(1000);
//        
//        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
//        client.getTasksAssignedAsPotentialOwner("garage_emergency_service", "en-UK", handler);
//        List<TaskSummary> sums = handler.getResults();
//        assertNotNull(sums);
//        assertEquals(1, sums.size());
        
        
        
    }

}