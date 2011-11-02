/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.grid;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.messaging.*;
import com.wordpress.salaboy.model.*;
import com.wordpress.salaboy.model.events.AllProceduresEndedEvent;
import com.wordpress.salaboy.model.messages.AsyncProcedureStartMessage;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.services.GenericEmergencyProcedureImpl;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.grid.SocketService;
import org.hornetq.api.core.HornetQException;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author salaboy
 */
public class GenericEmergencyProcedureTest extends GridBaseTest {

    private MessageConsumer consumer;
    private AsyncTaskService client;
    private MessageConsumerWorker asynchProcedureStartWorker;
    private PersistenceService persistenceService;
    private ContextTrackingService trackingService; 

    public GenericEmergencyProcedureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        persistenceService = PersistenceServiceProvider.getPersistenceService();
        trackingService = ContextTrackingProvider.getTrackingService();

        
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
        PersistenceServiceProvider.clear();
        ContextTrackingProvider.clear();

    }

    @Test
    public void genericEmergencyProcedureTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException {

        asynchProcedureStartWorker = new MessageConsumerWorker("asyncProcedureStartCoreServer", new MessageConsumerWorkerHandler<AsyncProcedureStartMessage>() {

            @Override
            public void handleMessage(AsyncProcedureStartMessage message) {
                System.out.println(">>>>>>>>>>>Creating a new Procedure = " + message.getProcedureName());
                try {
                    ProceduresMGMTService.getInstance().newRequestedProcedure(message.getEmergencyId(), message.getProcedureName(), message.getParameters());
                } catch (IOException ex) {
                    Logger.getLogger(GenericEmergencyProcedureTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        asynchProcedureStartWorker.start();




        MessageProducer producer = MessageFactory.createMessageProducer();
        Call initialCall = new Call(1, 2, new Date());
       
        producer.sendMessage(initialCall);
        producer.stop();

        Call call = (Call) consumer.receiveMessage();
        assertNotNull(call);

        GenericEmergencyProcedureImpl.getInstance().newPhoneCall(call);

        Thread.sleep(5000);

        doOperatorTask();

        //QUERY TO SEE THAT WE HAVE AN EMERGENCY ATTACHED TO THE CALL

        Thread.sleep(2000);

        doControlTask();

        Thread.sleep(6000);
        //I should have one task here, that has been created by the specific procedure started
        doGarageTask();

        Thread.sleep(3000);
        // I can asume that all the procedures are ended, we need to delegate this to the external component
        AllProceduresEndedEvent allProceduresEndedEvent = new AllProceduresEndedEvent(null, new ArrayList<String>());
        GenericEmergencyProcedureImpl.getInstance().allProceduresEnededNotification(allProceduresEndedEvent);
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
        Map<String, Object> deserializedContent = (Map<String, Object>) ois.readObject();
        Call restoredCall = (Call) deserializedContent.get("call");
        persistenceService.storeCall(restoredCall);

      
        Emergency emergency = new Emergency();
      
        emergency.setCall(restoredCall);
      
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);
        persistenceService.storeEmergency(emergency);
        
        trackingService.attachEmergency(restoredCall.getId(), emergency.getId());

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
        Map<String, Object> deserializedContent = (Map<String, Object>) ois.readObject();
        Emergency retrivedEmergency = (Emergency) deserializedContent.get("emergency");
        assertNotNull(retrivedEmergency);
        ActivePatients retrivedActivePatients = (ActivePatients) deserializedContent.get("activePatients");
        assertNotNull(retrivedActivePatients);
        assertEquals(1, retrivedActivePatients.size());
        SuggestedProcedures retrivedSuggestedProcedures = (SuggestedProcedures) deserializedContent.get("suggestedProcedures");
        assertNotNull(retrivedSuggestedProcedures);
        assertEquals("[DefaultHeartAttackProcedure: ]", retrivedSuggestedProcedures.getSuggestedProceduresString());

        Map<String, Object> info = new HashMap<String, Object>();
        SelectedProcedures selectedProcedures = new SelectedProcedures(
                retrivedEmergency.getId());

        selectedProcedures.addSelectedProcedureName("DefaultHeartAttackProcedure");
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

        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(sums.get(0).getId(), "garage_emergency_service", startTaskOperationHandler);


    }
}
