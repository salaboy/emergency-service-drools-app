/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.grid;

import java.util.ArrayList;
import com.wordpress.salaboy.model.ActivePatients;
import com.wordpress.salaboy.model.SuggestedProcedures;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.jbpm.task.AccessType;
import org.jbpm.task.service.ContentData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Location;
import java.util.Map;
import org.junit.Ignore;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.messaging.MessageFactory;
import java.util.List;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.drools.grid.SocketService;
import java.util.HashMap;
import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.messaging.MessageProducer;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.events.AllProceduresEndedEvent;
import com.wordpress.salaboy.services.IncomingCallsMGMTService;
import java.util.Date;
import org.hornetq.api.core.HornetQException;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
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
public class IncomingCallsMGMTServiceTest extends GridBaseTest{

    private MessageConsumer consumer;
    private TaskClient client;
    public IncomingCallsMGMTServiceTest() {
        
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
        
        
        client =  HumanTaskServerService.getInstance().initTaskClient();
        

    }

    @After
    public void tearDown() throws Exception {
        
        HumanTaskServerService.getInstance().stopTaskServer();
        
        MessageServerSingleton.getInstance().stop();
        if(remoteN1 != null){
            remoteN1.dispose();
        }
        if(grid1 != null){
            grid1.get(SocketService.class).close();
        }
        
        
    }

    @Ignore
    public void phoneCallsMGMTServiceTest() throws HornetQException, InterruptedException{
        
        MessageProducer producer = MessageFactory.createMessageProducer();
        producer.sendMessage(new Call(1,2,new Date()));
        producer.stop();
        
        Call call = (Call) consumer.receiveMessage();
        assertNotNull(call);
        
        IncomingCallsMGMTService.getInstance().newPhoneCall(call);
        
        Thread.sleep(1000);
        
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("operator", "en-UK", handler);
         List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());
        
        
        
    }
    
    
    @Test
    public void genericEmergencyProcedureTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException{
        
        MessageProducer producer = MessageFactory.createMessageProducer();
        producer.sendMessage(new Call(1,2,new Date()));
        producer.stop();
        
        Call call = (Call) consumer.receiveMessage();
        assertNotNull(call);
        
        IncomingCallsMGMTService.getInstance().newPhoneCall(call);
        
        Thread.sleep(1000);
        
        doOperatorTask();
        
        Thread.sleep(1000);
        
        doControlTask();
        
        Thread.sleep(1000);
        //I should have one task here, that has been created by the specific procedure started
        doGarageTask();
       
        Thread.sleep(1000);
        // I can asume that all the procedures are ended, we need to delegate this to the external component
        AllProceduresEndedEvent allProceduresEndedEvent = new AllProceduresEndedEvent(null, new ArrayList<String>());
        IncomingCallsMGMTService.getInstance().allProceduresEnded(allProceduresEndedEvent);
        // We should see the report in the console
        Thread.sleep(10000);
        
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
        Map<String, Object> deserializedContent = (Map<String, Object>)ois.readObject();
        Call retrivedCall = (Call) deserializedContent.get("call");
        
        
        //I shoudl call the tracking component here and register the new emerency
        Emergency emergency = new Emergency(1L);
        emergency.setCall(retrivedCall);
        emergency.setLocation(new Location(1,2));
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

    private void doControlTask() throws IOException, ClassNotFoundException {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("control", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());
        
        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(sums.get(0).getId(), "control", startTaskOperationHandler);
        
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(sums.get(0).getId(), getTaskHandler);
        Task controlTask = getTaskHandler.getTask();
        
        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(controlTask.getTaskData().getDocumentContentId(), getContentHandler);
        Content content = getContentHandler.getContent();
        
        assertNotNull(content);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, Object> deserializedContent = (Map<String, Object>)ois.readObject();
        Emergency retrivedEmergency = (Emergency) deserializedContent.get("emergency");
        assertNotNull(retrivedEmergency);
        ActivePatients retrivedActivePatients = (ActivePatients) deserializedContent.get("activePatients");
        assertNotNull(retrivedActivePatients);
        assertEquals(1, retrivedActivePatients.size());
        SuggestedProcedures retrivedSuggestedProcedures = (SuggestedProcedures) deserializedContent.get("suggestedProcedures");
        assertNotNull(retrivedSuggestedProcedures);
        assertEquals("[DefaultHeartAttackProcedure: ]", retrivedSuggestedProcedures.getSuggestedProceduresString());
        
        
        
        Map<String,Object> info = new HashMap<String, Object>();
        List<String> selectedProcedures = new ArrayList<String>();
        selectedProcedures.add("DefaultHeartAttackProcedure");
        info.put("selectedProcedures", selectedProcedures);
        
        
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(info);
        out.close();
        result.setContent(bos.toByteArray());
        
        BlockingTaskOperationResponseHandler completeTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.complete(sums.get(0).getId(), "control", result, completeTaskOperationHandler);
        
        
        
    }

    private void doGarageTask() {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("garage_emergency_service", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());
    }
 

}