/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.plugtree.training.model.Ambulance;
import org.plugtree.training.model.Call;
import org.plugtree.training.model.events.PatientAtTheHospitalEvent;
import org.plugtree.training.model.events.PatientPickUpEvent;

/**
 *
 * @author salaboy
 */
public class BasicProcessTest {

    TaskClient client;

    public BasicProcessTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        MyDroolsUtilities.initTaskServer();
        client = MyDroolsUtilities.initTaskClient();

    }

    @After
    public void tearDown() throws Exception {
        client.disconnect();
    }

    @Test
    public void processTest() throws InterruptedException, IOException, ClassNotFoundException {
        
        
        
        
        
        Call call = new Call(2,4, new Date());
        EmergencyService.getInstance().newEmergency(call);

        Thread.sleep(5000);

        
        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
       
        handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("operator", "en-UK", handler);
         List<TaskSummary> taskSums = handler.getResults();
        TaskSummary taskSum = taskSums.get(0);
        

       

        Assert.assertNotNull(taskSum);

        //Start the Get Emergency Information Task
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start(taskSum.getId(), "operator", responseHandler);


        Map<String, Object> info = new HashMap<String, Object>();

        
        
        info.put("emergency.location", "emergency Location");
        info.put("emergency.type", "FIRE"); // emergencyTypeJComboBox.getSelectedIndex())
        info.put("patient.name", "salaboy");
        info.put("patient.age", "27");
        info.put("patient.gender", "MALE");

        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(info);
        out.close();
        result.setContent(bos.toByteArray());
        
        client.complete(taskSum.getId(),"operator", result, null);
        
        Thread.sleep(5000);
        
        handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("control_operator", "en-UK", handler);
        taskSums = handler.getResults();
        taskSum = taskSums.get(0);
        
        client.start(taskSum.getId(), "control_operator", null);
        BlockingGetTaskResponseHandler handlerT = new BlockingGetTaskResponseHandler();
        client.getTask(taskSum.getId(), handlerT);
        Task task2 = handlerT.getTask();
        TaskData taskData = task2.getTaskData();
        
        System.out.println("TaskData = "+taskData);
        BlockingGetContentResponseHandler handlerC = new BlockingGetContentResponseHandler();
        client.getContent(taskData.getDocumentContentId(), handlerC);
        Content content = handlerC.getContent();
        
        System.out.println("Content= "+content);
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
		
	ObjectInputStream ois = new ObjectInputStream(bais);
        String taskinfo =(String) ois.readObject(); 
        System.out.println("TASKINFO = "+taskinfo);
        //#{doctor.id}, #{ambulance.id},  #{patient.id}, #{patient.name}, #{patient.age}, #{patient.gender}, #{emergency.location}, #{emergency.type}
        String[] values= taskinfo.split(",");
        
        Assert.assertEquals(8 , values.length);
        
        //I must test for changes here..
        client.complete(taskSum.getId(), "control_operator", null, null);
        
        
        Thread.sleep(5000);
       
        
       // UI SIDE.. needs access to the ksession to propagate the event
        

        Ambulance ambulance = EmergencyService.getInstance().getAmbulance(call.getProcessId());
         ambulance.setPositionX(33);
        ambulance.setPositionY(12);
        EmergencyService.getInstance().updateAmbualancePosition(ambulance);
        EmergencyService.getInstance().sendPatientPickUpEvent(new PatientPickUpEvent(new Date()),call.getProcessId());
        
        
        Thread.sleep(5000);
        
        //Now back to the client/task client side
        
        handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("doctor", "en-UK", handler);
        taskSums = handler.getResults();
        taskSum = taskSums.get(0);
        
        client.start(taskSum.getId(), "doctor", null);
        handlerT = new BlockingGetTaskResponseHandler();
        client.getTask(taskSum.getId(), handlerT);
        Task task3 = handlerT.getTask();
        taskData = task3.getTaskData();
        
        System.out.println("TaskData = "+taskData);
        handlerC = new BlockingGetContentResponseHandler();
        client.getContent(taskData.getDocumentContentId(), handlerC);
        content = handlerC.getContent();
        
        System.out.println("Content= "+content);
        bais = new ByteArrayInputStream(content.getContent());
		
	ois = new ObjectInputStream(bais);
        String taskinfo2 =(String) ois.readObject(); 
        
        System.out.println("TaskInfo 2= "+taskinfo2);
        
        info = new HashMap<String, Object>();

        
        // From 0 to 5 -> 0 == most urgent
        info.put("emergency.priority", "3");
        

        result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.util.Map");
        bos = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bos);
        out.writeObject(info);
        out.close();
        result.setContent(bos.toByteArray());
        
        client.complete(taskSum.getId(), "doctor", result , null);
        
        
        
        
        
        
        Thread.sleep(3000);
//        
//         //UI SIDE.. needs access to the ksession to propagate the event
        EmergencyService.getInstance().sendPatientAtTheHospitalEvent( new PatientAtTheHospitalEvent() ,call.getProcessId());

        Thread.sleep(5000);

        System.out.println("\n\nLogs:");
        for (String message : EmergencyService.logger.getLogs()) {
            System.out.println("\t"+message);
        }
        
        
    }
}