
package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.services.HumanTaskServerService;


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
public class DefaultHeartAttackProcedureNoSmartTasksTest extends DefaultHeartAttackProcedureBaseTest {

    private TaskClient client;

    public DefaultHeartAttackProcedureNoSmartTasksTest() {
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        client = HumanTaskServerService.getInstance().initTaskClient();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected String doGarageTask(Emergency emergency) throws Exception {
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
        String ambulanceId = ContextTrackingServiceImpl.getInstance().newVehicleId();
        ambulance.setId(ambulanceId);
        vehicles.add(ambulance);

//        ContextTrackingServiceImpl.getInstance().attachVehicle(procedureId, ambulanceId);

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

    @Override
    protected void doDoctorTask() throws IOException, ClassNotFoundException {
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