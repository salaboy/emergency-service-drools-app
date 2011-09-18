/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.grid.SocketService;
import org.example.ws_ht.api.TTaskAbstract;
import org.example.ws_ht.api.wsdl.IllegalAccessFault;
import org.example.ws_ht.api.wsdl.IllegalArgumentFault;
import org.example.ws_ht.api.wsdl.IllegalStateFault;
import org.hornetq.api.core.HornetQException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
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
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.EmergencyEndsMessage;
import com.wordpress.salaboy.model.messages.FireTruckOutOfWaterMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;
import com.wordpress.salaboy.tracking.ContextTrackingServiceImpl;
import java.util.ArrayList;
import org.junit.Ignore;

/**
 * 
 * @author esteban
 */
public class DefaultFireProcedureTest extends GridBaseTest {

	private HumanTaskService humanTaskServiceClient;

	public DefaultFireProcedureTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		HumanTaskServerService.getInstance().initTaskServer();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {

		HumanTaskServerService.getInstance().stopTaskServer();
	}

	private Emergency emergency = null;
	private FireTruck fireTruck = null;
	private Call call = null;
	private FirefightersDepartment firefightersDepartment = null;

	@Before
	public void setUp() throws Exception {
		
		String emergencyId = ContextTrackingServiceImpl.getInstance()
				.newEmergency();
                emergency = new Emergency(emergencyId);

                String fireTruckId = ContextTrackingServiceImpl.getInstance()
				.newVehicle();
		fireTruck = new FireTruck("FireTruck 1");
		fireTruck.setId(fireTruckId);

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

		HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();

		taskClientConf
				.addHumanTaskClientConfiguration("jBPM5-HT-Client",
						new JBPM5HornetQHumanTaskClientConfiguration(
								"127.0.0.1", 5446));

		humanTaskServiceClient = HumanTaskServiceFactory
				.newHumanTaskService(taskClientConf);
		humanTaskServiceClient.initializeService();

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
		this.humanTaskServiceClient.cleanUpService();
	}

	@Test
	public void defaultFireSimpleTest() throws HornetQException,
			InterruptedException, IOException, ClassNotFoundException,
			IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("call", call);
		parameters.put("emergency", emergency);
                
		ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(),
				"DefaultFireProcedure", parameters);

                Thread.sleep(5000);
                
                //Because of the emergency, a new Task is ready for garage: pick the corresponding vehicle/s
                List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "garage_emergency_service", "", null, "", "", "", 0, 0);
                Assert.assertNotNull(taskAbstracts);
                Assert.assertEquals(1, taskAbstracts.size());
                TTaskAbstract taskAbstract = taskAbstracts.get(0); // getting the first task
                Assert.assertEquals(" Select Vehicle For "+emergency.getId()+" ", taskAbstract.getName().getLocalPart());
                
                //Garage team starts working on the task
                humanTaskServiceClient.setAuthorizedEntityId("garage_emergency_service");
                humanTaskServiceClient.start(taskAbstract.getId());
                
                
                //A Firetruck is selected
                Map<String, Object> info = new HashMap<String, Object>();
                List<Vehicle> vehicles = new ArrayList<Vehicle>();
                vehicles.add(fireTruck);
                info.put("emergency.vehicles", vehicles);
                
                //Garage team completes the task
                humanTaskServiceClient.complete(taskAbstract.getId(), info);
                
		// The fire truck doesn't reach the emergency yet. No task for
		// the firefighter.
		humanTaskServiceClient.setAuthorizedEntityId("firefighter");
		taskAbstracts = humanTaskServiceClient
				.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0,
						0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Now the fire truck arrives to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		TTaskAbstract firefighterTask = taskAbstracts.get(0);

		// The firefighter completes the task
		info = new HashMap<String, Object>();
		info.put("emergency.priority", 1);
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), info);

		Thread.sleep(2000);

		// TODO: validate that the process is still running

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Ok, the emregency ends
		ProceduresMGMTService.getInstance().notifyProcedures(
				new EmergencyEndsMessage(emergency.getId(), new Date()));


		// TODO: validate that the process has finished

	}

	@Ignore
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
		humanTaskServiceClient.setAuthorizedEntityId("firefighter");
		List<TTaskAbstract> taskAbstracts = humanTaskServiceClient
				.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0,
						0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Now the fire truck arrives to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		TTaskAbstract firefighterTask = taskAbstracts.get(0);

		// The firefighter completes the task
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("emergency.priority", 1);
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), info);

		Thread.sleep(2000);

		// TODO: validate that the process is still running

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Sudenly, the fire truck runs out of water
		ProceduresMGMTService.getInstance().notifyProcedures(
				new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
						new Date()));

		Thread.sleep(2000);

		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		firefighterTask = taskAbstracts.get(0);

		Assert.assertEquals(
				"Water Refill: go to ( " + firefightersDepartment.getX() + ", "
						+ firefightersDepartment.getY() + " )", firefighterTask
						.getName().toString());

		// The firefighter completes the task
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), null);

		Thread.sleep(2000);

		// No more tasks for firefighter
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// The Fire Truck returns to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		firefighterTask = taskAbstracts.get(0);

		// The firefighter completes the task
		info = new HashMap<String, Object>();
		info.put("emergency.priority", 1);
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), info);

		Thread.sleep(2000);

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Ok, the emregency ends
		ProceduresMGMTService.getInstance().notifyProcedures(
				new EmergencyEndsMessage(emergency.getId(), new Date()));


		// TODO: validate that the process has finished

	}

	@Ignore
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
		humanTaskServiceClient.setAuthorizedEntityId("firefighter");
		List<TTaskAbstract> taskAbstracts = humanTaskServiceClient
				.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0,
						0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Now the fire truck arrives to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		TTaskAbstract firefighterTask = taskAbstracts.get(0);

		// The firefighter completes the task
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("emergency.priority", 1);
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), info);

		Thread.sleep(2000);

		// TODO: validate that the process is still running

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Sudenly, the fire truck runs out of water
		ProceduresMGMTService.getInstance().notifyProcedures(
				new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
						new Date()));

		Thread.sleep(5000);

		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		firefighterTask = taskAbstracts.get(0);

		Assert.assertEquals(
				"Water Refill: go to ( " + firefightersDepartment.getX() + ", "
						+ firefightersDepartment.getY() + " )", firefighterTask
						.getName().toString());

		// The firefighter completes the task
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), null);

		Thread.sleep(2000);

		// No more tasks for firefighter
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// The Fire Truck returns to the emergency
		ProceduresMGMTService.getInstance().notifyProcedures(
				new VehicleHitsEmergencyMessage(fireTruck.getId(),
						emergency.getId(), new Date()));

		Thread.sleep(2000);

		// A new task for the firefighter should be there now
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		firefighterTask = taskAbstracts.get(0);

		// The firefighter completes the task
		info = new HashMap<String, Object>();
		info.put("emergency.priority", 1);
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), info);

		Thread.sleep(2000);

		// Becasuse the fire truck still got enough water, no "Water Refill"
		// task exists
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Again, the fire truck runs out of water
		ProceduresMGMTService.getInstance().notifyProcedures(
				new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
						new Date()));

		Thread.sleep(2000);

		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertEquals(1, taskAbstracts.size());

		firefighterTask = taskAbstracts.get(0);

		Assert.assertEquals(
				"Water Refill: go to ( " + firefightersDepartment.getX() + ", "
						+ firefightersDepartment.getY() + " )", firefighterTask
						.getName().toString());

		// The firefighter completes the task
		humanTaskServiceClient.start(firefighterTask.getId());
		humanTaskServiceClient.complete(firefighterTask.getId(), null);

		Thread.sleep(2000);

		// No more tasks for firefighter
		taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("",
				"firefighter", "", null, "", "", "", 0, 0);

		Assert.assertTrue(taskAbstracts.isEmpty());

		// Ok, the emregency ends
		ProceduresMGMTService.getInstance().notifyProcedures(
				new EmergencyEndsMessage(emergency.getId(), new Date()));

		// TODO: validate that the process has finished

	}

}