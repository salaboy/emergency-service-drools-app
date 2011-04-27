/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.jbpm.task.AccessType;
import org.jbpm.task.service.ContentData;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Call;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import java.util.Date;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import org.drools.grid.SocketService;
import java.util.Map;
import com.wordpress.salaboy.services.HumanTaskServerService;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import com.wordpress.salaboy.grid.*;
import java.util.List;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import java.util.HashMap;
import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.model.events.PatientAtHospitalEvent;
import com.wordpress.salaboy.model.events.PatientPickUpEvent;
import com.wordpress.salaboy.model.serviceclient.InMemoryPersistenceService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import org.hornetq.api.core.HornetQException;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
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
public class DefaultHeartAttackProcedureTest extends GridBaseTest {

    private MessageConsumer consumer;
    private TaskClient client;

    public DefaultHeartAttackProcedureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        HumanTaskServerService.getInstance().initTaskServer();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        HumanTaskServerService.getInstance().stopTaskServer();
    }

    @Before
    public void setUp() throws Exception {
        InMemoryPersistenceService.getInstance().storeCall(new Call(1,1,new Date()));
        InMemoryPersistenceService.getInstance().storeEmergency(new Emergency(1L));
        InMemoryPersistenceService.getInstance().storeVehicle(new Ambulance("My Ambulance Test"));
        MessageServerSingleton.getInstance().start();
        consumer = MessageFactory.createMessageConsumer("IncomingCall");


        this.coreServicesMap = new HashMap();
        createRemoteNode();

    }

    @After
    public void tearDown() throws Exception {
        MessageServerSingleton.getInstance().stop();
        if (remoteN1 != null) {
            remoteN1.dispose();
        }
        if (grid1 != null) {
            grid1.get(SocketService.class).close();
        }
    }

    @Test
    public void defaultHeartAttackSimpleTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException {

        //   MessageProducer producer = MessageFactory.createMessageProducer("phoneCalls");
        //  producer.sendMessage(new Call(1,2,new Date()));
        //  producer.stop();
        client = HumanTaskServerService.getInstance().initTaskClient("client test defaultHeartAttackSimpleTest");

//        Call call = (Call) consumer.receiveMessage();
//        assertNotNull(call);
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call.id", 1L);
        parameters.put("emergency.id", 1L);



        ProceduresMGMTService.getInstance().newRequestedProcedure((Long) parameters.get("call.id"),"DefaultHeartAttackProcedure", parameters);

        Thread.sleep(4000);

        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("garage_emergency_service", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());
        TaskSummary taskSum = sums.get(0); // getting the first task


        client.start(taskSum.getId(), "garage_emergency_service", null);


        // I need to get the Content Data and check the values of the Emergency and Call Ids.
        // Using that I need to select one vehicle ID from the list of all the vehicles. 
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskSum.getId(), getTaskHandler);
        Task realTask = getTaskHandler.getTask();
        assertNotNull(realTask);
        long contentId = realTask.getTaskData().getDocumentContentId();
        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentHandler);

        Content content = getContentHandler.getContent();

        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        String taskinfo = (String) ois.readObject();
        assertNotNull(taskinfo);
        System.out.println("Task Info: " + taskinfo);
        assertNotNull(taskinfo, "1,1");

        ObjectOutputStream out = null;
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.vehicle", new Ambulance("My Ambulance", new Date()));


        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bos);
        out.writeObject(info);
        result.setContent(bos.toByteArray());


        client.complete(taskSum.getId(), "garage_emergency_service", result, null);



        ProceduresMGMTService.getInstance().patientPickUpNotification(new PatientPickUpEvent(1L, 1L, new Date()));

        Thread.sleep(4000);

        handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("doctor", "en-UK", handler);
        sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());
        taskSum = sums.get(0); // getting the first task

        client.start(taskSum.getId(), "doctor", null);

         out = null;
        info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);


        result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        bos = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bos);
        out.writeObject(info);
        result.setContent(bos.toByteArray());

        client.complete(taskSum.getId(), "doctor", result, null);

        Thread.sleep(4000);

        ProceduresMGMTService.getInstance().patientAtHospitalNotification(new PatientAtHospitalEvent(1L, 1L, 1L, new Date()));

        Thread.sleep(4000);


    }
}