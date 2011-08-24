/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.grid.SocketService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordpress.salaboy.grid.GridBaseTest;
import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

/**
 *
 * @author salaboy
 */
public class AdHocProcedureTest extends GridBaseTest {

    private MessageConsumer consumer;
    private TaskClient client;
    private Emergency emergency = null;
    private Call call = null;
    public AdHocProcedureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        
    }
    

    @Before
    public void setUp() throws Exception {


        HumanTaskServerService.getInstance().initTaskServer();

        MessageServerSingleton.getInstance().start();

        consumer = MessageFactory.createMessageConsumer("IncomingCall");


        this.coreServicesMap = new HashMap();


        createRemoteNode();


        client = HumanTaskServerService.getInstance().initTaskClient();

    }

    @After
    public void tearDown() throws Exception {
        HumanTaskServerService.getInstance().stopTaskServer();

        MessageServerSingleton.getInstance().stop();
        if (remoteN1 != null) {
            remoteN1.dispose();
        }
        if (grid1 != null) {
            grid1.get(SocketService.class).close();
        }
    }

    @Test
    public void defaultAdHocSimpleTest() throws InterruptedException, ClassNotFoundException, IOException {
        emergency = new Emergency(1L);
        call = new Call(1, 2, new Date());
        call.setId(1L);
        emergency.setCall(call);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);
        parameters.put("procedureName", "DumbProcedure");


        ProceduresMGMTService.getInstance().newRequestedProcedure(((Call) parameters.get("call")).getId(), "AdHocProcedure", parameters);

        Thread.sleep(5000);



    }
    
     @Test
    public void defaultAdHocPlusTrackingTest() throws InterruptedException, ClassNotFoundException, IOException {
        emergency = new Emergency(1L);
        call = new Call(1, 2, new Date());
        call.setId(1L);
        emergency.setCall(call);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);
        parameters.put("procedureName", "DumbProcedure");


        ProceduresMGMTService.getInstance().newRequestedProcedure(((Call) parameters.get("call")).getId(), "AdHocProcedure", parameters);

        Thread.sleep(5000);



    }


    private void doOperatorTask() throws ClassNotFoundException, IOException {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("operator", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());

        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(sums.get(0).getId(), "operator", startTaskOperationHandler);

        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(sums.get(0).getId(), getTaskHandler);
        Task operatorTask = getTaskHandler.getTask();

        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(operatorTask.getTaskData().getDocumentContentId(), getContentHandler);
        Content content = getContentHandler.getContent();

        assertNotNull(content);

        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, Object> deserializedContent = (Map<String, Object>) ois.readObject();
        Call retrivedCall = (Call) deserializedContent.get("call");


        //I shoudl call the tracking component here and register the new emerency
        Emergency emergency = new Emergency(1L);
        emergency.setCall(retrivedCall);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);


        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency", emergency);


        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(info);
        out.close();
        result.setContent(bos.toByteArray());

        BlockingTaskOperationResponseHandler completeTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.complete(sums.get(0).getId(), "operator", result, completeTaskOperationHandler);
    }
}