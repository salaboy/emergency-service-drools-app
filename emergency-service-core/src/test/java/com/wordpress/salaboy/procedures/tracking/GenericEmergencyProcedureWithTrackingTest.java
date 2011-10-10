/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.procedures.tracking;


import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider.ContextTrackingServiceType;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.grid.*;
import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
import com.wordpress.salaboy.messaging.*;
import com.wordpress.salaboy.model.*;
import com.wordpress.salaboy.model.serviceclient.DistributedMapPeristenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.jbpm.task.AccessType;
import org.jbpm.task.service.ContentData;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import java.util.Map;
import java.util.List;
import org.jbpm.task.query.TaskSummary;
import java.util.HashMap;
import org.hornetq.api.core.HornetQException;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.neo4j.cypher.ExecutionEngine;
import org.neo4j.cypher.ExecutionResult;
import org.neo4j.cypher.commands.Query;
import org.neo4j.cypher.javacompat.CypherParser;
import org.neo4j.graphdb.Node;
import scala.collection.Iterator;
/**
 *
 * @author salaboy
 */
public class GenericEmergencyProcedureWithTrackingTest extends GridBaseTest{

    private MessageConsumer consumer;
    private AsyncTaskService client;
    private MessageConsumerWorker asynchProcedureStartWorker;
    private PersistenceService persistenceService;
    private ContextTrackingService trackingService;
    public GenericEmergencyProcedureWithTrackingTest() {
        
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        
        
    }

    @Before
    public void setUp() throws Exception {
        
        
        
//        HumanTaskServerService.getInstance().initTaskServer();
//        
//        MessageServerSingleton.getInstance().start();
//        
//        consumer = MessageFactory.createMessageConsumer("IncomingCall");
//        
//        
//        this.coreServicesMap = new HashMap();
//       
//        
//        createRemoteNode();
//        
//        
//        client =  HumanTaskServerService.getInstance().initTaskClient();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
        persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);
        
        trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType)conf.getParameters().get("ContextTrackingImplementation"));  
        
        for (Vehicle vehicle : CityEntities.vehicles) {
            persistenceService.storeVehicle(vehicle);
            System.out.println("Initializing Vehicle into the Cache & Graph - >" + vehicle.toString());
        }
        
        CypherParser parser = new CypherParser();
        
        ExecutionEngine engine = new ExecutionEngine(ContextTrackingProvider.getTrackingService(ContextTrackingServiceType.IN_MEMORY).getGraphDb());
        
        Query query = parser.parse("start n=(vehicles, 'vehicleId:*')  return n");

        ExecutionResult result = engine.execute(query);
        Iterator<Node> n_column = result.columnAs("n");


        System.out.println("results: " + result);
        while (n_column.hasNext()) {
            Node currentNode = n_column.next();
            for (String key : currentNode.getPropertyKeys()) {
                System.out.println("Property (" + key + "): " + currentNode.getProperty(key));
            }
        }
        assertEquals(CityEntities.vehicles.size(), result.size());
        
        
//        for (Hospital hospital : CityEntities.hospitals) {
//            System.out.println("Initializing Hospital into the Cache - >" + hospital.toString());
//            DistributedPeristenceServerService.getInstance().storeHospital(hospital);
//        }
        
        
        

    }

    @After
    public void tearDown() throws Exception {
        
//        HumanTaskServerService.getInstance().stopTaskServer();
//        
//        MessageServerSingleton.getInstance().stop();
//        if(remoteN1 != null){
//            remoteN1.dispose();
//        }
//        if(grid1 != null){
//            grid1.get(SocketService.class).close();
//        }
        
        
    }

   
    
    
    @Test
    public void genericEmergencyProcedureTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException{
        System.out.println("Running Tests! ");
//          asynchProcedureStartWorker = new MessageConsumerWorker("asyncProcedureStartCoreServer", new MessageConsumerWorkerHandler<AsyncProcedureStartMessage>() {
//
//                @Override
//                public void handleMessage(AsyncProcedureStartMessage message) {
//                      System.out.println(">>>>>>>>>>>Creating a new Procedure = "+message.getProcedureName());
//                      ProceduresMGMTService.getInstance().newRequestedProcedure(message.getEmergencyId(), message.getProcedureName(), message.getParameters());
//                }
//            });
//          
//          asynchProcedureStartWorker.start();
//          
//        
//        MessageProducer producer = MessageFactory.createMessageProducer();
//        Call initialCall = new Call(1,2,new Date());
//        String callId = ContextTrackingServiceImpl.getInstance().newCallId();
//        initialCall.setId(callId);
//        producer.sendMessage(initialCall);
//        producer.stop();
//        
//        Call call = (Call) consumer.receiveMessage();
//        assertNotNull(call);
//        
//        GenericEmergencyProcedureImpl.getInstance().newPhoneCall(call);
//        
//        Thread.sleep(1000);
//        
//        doOperatorTask();
//        
//        Thread.sleep(1000);
//        
//        doControlTask();
//        
//        Thread.sleep(3000);
//        //I should have one task here, that has been created by the specific procedure started
//        doGarageTask();
//       
//        Thread.sleep(3000);
//        // I can asume that all the procedures are ended, we need to delegate this to the external component
//        AllProceduresEndedEvent allProceduresEndedEvent = new AllProceduresEndedEvent(null, new ArrayList<String>());
//        GenericEmergencyProcedureImpl.getInstance().allProceduresEnededNotification(allProceduresEndedEvent);
//        // We should see the report in the console
//        Thread.sleep(10000);
//        
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
        Call restoredCall = (Call) deserializedContent.get("call");
        
        
        //I shoudl call the tracking component here and register the new emerency
        Emergency emergency = new Emergency();
        String emergencyId = trackingService.newEmergencyId();
        emergency.setId(emergencyId);
        emergency.setCall(restoredCall);
        trackingService.attachEmergency(restoredCall.getId(), emergencyId);
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
		SelectedProcedures selectedProcedures = new SelectedProcedures(
				retrivedEmergency.getId()); 
//        List<String> selectedProceduresList = new ArrayList<String>();
//        selectedProceduresList.add("DefaultHeartAttackProcedure");
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