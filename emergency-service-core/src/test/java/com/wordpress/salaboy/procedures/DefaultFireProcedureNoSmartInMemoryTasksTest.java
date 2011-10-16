/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.services.HumanTaskServerService;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

/**
 *
 * @author esteban
 */
public class DefaultFireProcedureNoSmartInMemoryTasksTest extends DefaultFireProcedureBaseTest {

    private TaskClient client;

    public DefaultFireProcedureNoSmartInMemoryTasksTest() {
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
        try{
            super.tearDown();
        } finally{
            this.client.disconnect();
        }
    }

    

    @Override
    protected void testGarageTask(Emergency emergency, List<Vehicle> selectedVehicles) throws Exception {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("garage_emergency_service", "en-UK", handler);
        List<TaskSummary> tasks = handler.getResults();
        
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0); // getting the first task
        Assert.assertEquals(" Select Vehicle For " + emergency.getId() + " ", taskSummary.getName());

        //Garage team starts working on the task
        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(tasks.get(0).getId(), "garage_emergency_service", startTaskOperationHandler);

        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(tasks.get(0).getId(), getTaskHandler);
        Task garageTask = getTaskHandler.getTask();

        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(garageTask.getTaskData().getDocumentContentId(), getContentHandler);
        Content content = getContentHandler.getContent();

        Assert.assertNotNull(content);

        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, Object> value = (Map<String, Object>) ois.readObject();


        Assert.assertNotNull(value);

        String procedureId = (String) value.get("procedureId");

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.vehicles", selectedVehicles);
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

        Thread.sleep(3000);
    }

    @Override
    protected Map<String, String> getFirefighterTasks() throws Exception {
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        List<TaskSummary> sums = handler.getResults();
        
        Map<String, String> ids = new HashMap<String, String>();
        for (TaskSummary taskSummary : sums) {
            ids.put(taskSummary.getId()+"", taskSummary.getName());
        }
        
        return ids;
    }

    @Override
    protected void completeTask(String user, String taskId) throws Exception {
        Long taskIdAsLong = Long.parseLong(taskId);

        BlockingTaskOperationResponseHandler startTaskOperationHandler = new BlockingTaskOperationResponseHandler();
        client.start(taskIdAsLong, user, startTaskOperationHandler);

        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskIdAsLong, getTaskHandler);
        Task task = getTaskHandler.getTask();

        BlockingGetContentResponseHandler getContentHandler = new BlockingGetContentResponseHandler();
        client.getContent(task.getTaskData().getDocumentContentId(), getContentHandler);
        Content content = getContentHandler.getContent();

        Assert.assertNotNull(content);

        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Map<String, Object> value = (Map<String, Object>) ois.readObject();

        Assert.assertNotNull(value);

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
        client.complete(task.getId(), user, result, completeTaskOperationHandler);
        
        Thread.sleep(2000);
    }

    @Override
    protected void initializePersistenceAndTracking() {
        persistenceService = PersistenceServiceProvider.getPersistenceService();
        trackingService = ContextTrackingProvider.getTrackingService();
//        try {
//           Map<String, Object> params = new HashMap<String, Object>();
//           params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
//           PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
//           persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);
//
//           trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));
//        } catch (IOException ex) {
//            Logger.getLogger(DefaultFireProcedureNoSmartInMemoryTasksTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}