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
import org.hornetq.api.core.HornetQException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordpress.salaboy.grid.GridBaseTest;
import com.wordpress.salaboy.messaging.MessageConsumer;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsHospitalMessage;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import com.wordpress.salaboy.tracking.ContextTrackingServiceImpl;
import com.wordpress.salaboy.tracking.ContextTrackingSimpleGraphServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;

/**
 *
 * @author salaboy
 */
public class DefaultHeartAttackProcedureNoSmartTasksTest extends GridBaseTest {

    private MessageConsumer consumer;
    private TaskClient client;
    private Emergency emergency = null;
    private Call call = null;

    public DefaultHeartAttackProcedureNoSmartTasksTest() {
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
        emergency = new Emergency();
        String emergencyId = ContextTrackingServiceImpl.getInstance().newEmergency();
        emergency.setId(emergencyId);
        call = new Call(1, 2, new Date());
        String callId = ContextTrackingServiceImpl.getInstance().newCall();
        call.setId(callId);
        emergency.setCall(call);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);

        ContextTrackingServiceImpl.getInstance().attachEmergency(callId, emergencyId);

        DistributedPeristenceServerService.getInstance().storeHospital(new Hospital("My Hospital", 12, 1));
        DistributedPeristenceServerService.getInstance().storeEmergency(emergency);
        DistributedPeristenceServerService.getInstance().storeVehicle(new Ambulance("My Ambulance Test"));

        MessageServerSingleton.getInstance().start();


        this.coreServicesMap = new HashMap();
        createRemoteNode();

        client = HumanTaskServerService.getInstance().initTaskClient();
        this.coreServicesMap = new HashMap();




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


        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);




        ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(), "DefaultHeartAttackProcedure", parameters);
        Thread.sleep(5000);

        String ambulanceId = doGarageTask();

        Thread.sleep(5000);







        //The vehicle reaches the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulanceId, emergency.getId(), new Date()));

        Thread.sleep(4000);

        doDoctorTask();

        Thread.sleep(4000);


        //The vehicle reaches the hospital
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsHospitalMessage(ambulanceId, new Hospital("Hospital A", 0, 0), emergency.getId(), new Date()));

        Thread.sleep(4000);
        
        String result = new ContextTrackingSimpleGraphServiceImpl(ContextTrackingServiceImpl.getInstance().getGraphDb()).graphEmergency(emergency.getId());
        System.out.println("result = "+result);
        
        

    }

    private String doGarageTask() throws IOException, ClassNotFoundException {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("garage_emergency_service", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());

        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(sums.get(0).getId(), "garage_emergency_service", startTaskOperationHandler);

        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(sums.get(0).getId(), getTaskHandler);
        Task garageTask = getTaskHandler.getTask();

        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(garageTask.getTaskData().getDocumentContentId(), getContentHandler);
        Content content = getContentHandler.getContent();

        assertNotNull(content);

        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, Object> value = (Map<String, Object>) ois.readObject();


        assertNotNull(value);

        String procedureId = (String) value.get("procedureId");

        Map<String, Object> info = new HashMap<String, Object>();
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        Ambulance ambulance = new Ambulance("My Ambulance", new Date());
        String ambulanceId = ContextTrackingServiceImpl.getInstance().newVehicle();
        ambulance.setId(ambulanceId);
        vehicles.add(ambulance);
        ContextTrackingServiceImpl.getInstance().attachVehicle(procedureId, ambulanceId);
        info.put("emergency.vehicles", vehicles);
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(info);
        out.close();
        result.setContent(bos.toByteArray());

        BlockingTaskOperationResponseHandler completeTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.complete(garageTask.getId(), "garage_emergency_service", result, completeTaskOperationHandler);

        return ambulanceId;

    }

    private void doDoctorTask() throws IOException, ClassNotFoundException {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("doctor", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        assertNotNull(sums);
        assertEquals(1, sums.size());

        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(sums.get(0).getId(), "doctor", startTaskOperationHandler);

        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(sums.get(0).getId(), getTaskHandler);
        Task doctorTask = getTaskHandler.getTask();

        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(doctorTask.getTaskData().getDocumentContentId(), getContentHandler);
        Content content = getContentHandler.getContent();

        assertNotNull(content);

        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, Object> value = (Map<String, Object>) ois.readObject();

        assertNotNull(value);

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(info);
        out.close();
        result.setContent(bos.toByteArray());

        BlockingTaskOperationResponseHandler completeTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.complete(doctorTask.getId(), "doctor", result, completeTaskOperationHandler);

    }
}