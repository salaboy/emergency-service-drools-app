/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.drools.grid.SocketService;
import org.example.ws_ht.api.TTaskAbstract;
import org.example.ws_ht.api.wsdl.IllegalAccessFault;
import org.example.ws_ht.api.wsdl.IllegalArgumentFault;
import org.example.ws_ht.api.wsdl.IllegalStateFault;
import org.hornetq.api.core.HornetQException;
import org.jbpm.task.AccessType;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordpress.salaboy.api.HumanTaskService;
import com.wordpress.salaboy.api.HumanTaskServiceFactory;
import com.wordpress.salaboy.conf.HumanTaskServiceConfiguration;
import com.wordpress.salaboy.grid.GridBaseTest;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FireTruck;
import com.wordpress.salaboy.model.FirefightersDepartment;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.messages.EmergencyEndsMessage;
import com.wordpress.salaboy.model.messages.FireTruckOutOfWaterMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;
import com.wordpress.salaboy.tracking.ContextTrackingServiceImpl;

/**
 * 
 * @author esteban
 */
public class DefaultFireProcedureNoSmartTasksTest extends GridBaseTest {

	
	private Emergency emergency = null;
	private FireTruck fireTruck = null;
	private FirefightersDepartment firefightersDepartment = null;
	private TaskClient client;

	private Call call = null;

	public DefaultFireProcedureNoSmartTasksTest() {
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
		String emergencyId = ContextTrackingServiceImpl.getInstance()
				.newEmergency();
		emergency.setId(emergencyId);

		fireTruck = new FireTruck("FireTruck 1");
		fireTruck.setId("FT1");

		call = new Call(1, 2, new Date());

		String callId = ContextTrackingServiceImpl.getInstance().newCall();
		call.setId(callId);
		emergency.setCall(call);
		emergency.setLocation(new Location(1, 2));
		emergency.setType(Emergency.EmergencyType.FIRE);
		emergency.setNroOfPeople(1);

		firefightersDepartment = new FirefightersDepartment(1L,
				"Firefighter Department 1", 12, 1);

		DistributedPeristenceServerService.getInstance()
				.storeFirefightersDepartment(firefightersDepartment);
		DistributedPeristenceServerService.getInstance().storeEmergency(
				emergency);
		DistributedPeristenceServerService.getInstance()
				.storeVehicle(fireTruck);
		MessageServerSingleton.getInstance().start();

		this.coreServicesMap = new HashMap();
		createRemoteNode();

		client = HumanTaskServerService.getInstance().initTaskClient();
		HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();

		taskClientConf
				.addHumanTaskClientConfiguration("jBPM5-HT-Client",
						new JBPM5HornetQHumanTaskClientConfiguration(
								"127.0.0.1", 5446));

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
		this.client.disconnect();

	}

	
	@Test
	public void fireTruckOutOfWaterTest() throws HornetQException,
			InterruptedException, IOException, ClassNotFoundException,
			IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("call", call);
		parameters.put("emergency", emergency);
		parameters.put("vehicle", fireTruck);

		ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(),
				"DefaultFireProcedure", parameters);

		// The fire truck doesn't reach the emergency yet. No task for
		// the firefighter.
		BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        List<TaskSummary> tasks = handler.getResults();

		Assert.assertTrue(tasks.isEmpty());
                Thread.sleep(2000);
		// Now the fire truck arrives to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertEquals(1, tasks.size());

		TaskSummary firefighterTask = tasks.get(0);

		this.doFireFighterTask(firefighterTask.getId());
		Thread.sleep(2000);

		// TODO: validate that the process is still running

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertTrue(tasks.isEmpty());

		// Sudenly, the fire truck runs out of water
		ProceduresMGMTService.getInstance().notifyProcedures(
				new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
						new Date()));

		Thread.sleep(5000);

		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertEquals(1, tasks.size());

		firefighterTask = tasks.get(0);

		Assert.assertEquals(
				"Water Refill: go to ( " + firefightersDepartment.getX() + ", "
						+ firefightersDepartment.getY() + " )", firefighterTask
						.getName().toString());

		// The firefighter completes the task
		this.doFireFighterTask(firefighterTask.getId());

		Thread.sleep(2000);

		// No more tasks for firefighter
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertTrue(tasks.isEmpty());

		// The Fire Truck returns to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertEquals(1, tasks.size());

		firefighterTask = tasks.get(0);

		// The firefighter completes the task
		this.doFireFighterTask(firefighterTask.getId());
		Thread.sleep(2000);

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();
		Assert.assertTrue(tasks.isEmpty());

		// Ok, the emregency ends
		ProceduresMGMTService.getInstance().notifyProcedures(
				new EmergencyEndsMessage(emergency.getId(), new Date()));


		// TODO: validate that the process has finished

	}

	@Test
	public void fireTruckOutOfWaterx2Test() throws HornetQException,
			InterruptedException, IOException, ClassNotFoundException,
			IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("call", call);
		parameters.put("emergency", emergency);
		parameters.put("vehicle", fireTruck);

		ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(),
				"DefaultFireProcedure", parameters);

		// The fire truck doesn't reach the emergency yet. No task for
		// the firefighter.
		BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        List<TaskSummary> tasks = handler.getResults();

		Assert.assertTrue(tasks.isEmpty());
                Thread.sleep(2000);
		// Now the fire truck arrives to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();


		Assert.assertEquals(1, tasks.size());

		TaskSummary firefighterTask = tasks.get(0);

		// The firefighter completes the task
		this.doFireFighterTask(firefighterTask.getId());

		Thread.sleep(2000);

		// TODO: validate that the process is still running

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();
		Assert.assertTrue(tasks.isEmpty());

		// Sudenly, the fire truck runs out of water
		ProceduresMGMTService.getInstance().notifyProcedures(
				new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
						new Date()));

		Thread.sleep(2000);

		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertEquals(1, tasks.size());

		firefighterTask = tasks.get(0);

		Assert.assertEquals(
				"Water Refill: go to ( " + firefightersDepartment.getX() + ", "
						+ firefightersDepartment.getY() + " )", firefighterTask
						.getName().toString());

		// The firefighter completes the task
		this.doFireFighterTask(firefighterTask.getId());
		Thread.sleep(2000);

		// No more tasks for firefighter
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();
		Assert.assertTrue(tasks.isEmpty());

		// The Fire Truck returns to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();
		Assert.assertEquals(1, tasks.size());

		firefighterTask = tasks.get(0);

		this.doFireFighterTask(firefighterTask.getId());
		Thread.sleep(2000);

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();
        
		Assert.assertTrue(tasks.isEmpty());

		// Again, the fire truck runs out of water
		ProceduresMGMTService.getInstance().notifyProcedures(
				new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
						new Date()));

		Thread.sleep(2000);

		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertEquals(1, tasks.size());

		firefighterTask = tasks.get(0);

		Assert.assertEquals(
				"Water Refill: go to ( " + firefightersDepartment.getX() + ", "
						+ firefightersDepartment.getY() + " )", firefighterTask
						.getName().toString());

		this.doFireFighterTask(firefighterTask.getId());
		Thread.sleep(2000);

		// No more tasks for firefighter
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();
		Assert.assertTrue(tasks.isEmpty());

		// Ok, the emregency ends
		ProceduresMGMTService.getInstance().notifyProcedures(
				new EmergencyEndsMessage(emergency.getId(), new Date()));

		// TODO: validate that the process has finished

	}


	@Test
	public void defaultFireSimpleTest() throws HornetQException,
			InterruptedException, IOException, ClassNotFoundException,
			IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("call", call);
		parameters.put("emergency", emergency);
		parameters.put("vehicle", fireTruck);

		ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(),
				"DefaultFireProcedure", parameters);
		// The fire truck doesn't reach the emergency yet. No task for
		// the firefighter.

		BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en_UK", handler);
        List<TaskSummary> tasks = handler.getResults();
        
		Assert.assertTrue(tasks.isEmpty());

		// Now the fire truck arrives to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(3000);
		

		// A new task for the firefighter should be there now
		handler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("firefighter", "en-UK", handler);
        tasks = handler.getResults();

		Assert.assertEquals(1, tasks.size());

		TaskSummary firefighterTask = tasks.get(0);

		// The firefighter completes the task
		this.doFireFighterTask(firefighterTask.getId());

		Thread.sleep(5000);

		// TODO: validate that the process has finished

	}

	private void doFireFighterTask(long taskId) {
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("emergency.priority", 1);
		ContentData contentData = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(info);
			out.close();
		} catch (IOException ioe) {
			Assert.fail();
		}
		contentData = new ContentData();
		contentData.setContent(bos.toByteArray());
		contentData.setAccessType(AccessType.Inline);

		BlockingTaskOperationResponseHandler blockingTaskOperationResponseHandler = new BlockingTaskOperationResponseHandler();
		client.start(taskId, "firefighter",
				blockingTaskOperationResponseHandler);
		blockingTaskOperationResponseHandler.waitTillDone(2000);
		blockingTaskOperationResponseHandler = new BlockingTaskOperationResponseHandler();
		client.complete(taskId, "firefighter", contentData,
				blockingTaskOperationResponseHandler);
		blockingTaskOperationResponseHandler.waitTillDone(2000);
	}
}