/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.procedures;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.grid.SocketService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.grid.GridBaseTest;
import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FireTruck;
import com.wordpress.salaboy.model.FirefightersDepartment;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.FireExtinctedMessage;
import com.wordpress.salaboy.model.messages.FireTruckOutOfWaterMessage;
import com.wordpress.salaboy.model.messages.ProcedureCompletedMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public abstract class DefaultFireProcedureBaseTest extends GridBaseTest {

    protected PersistenceService persistenceService;
    protected ContextTrackingService trackingService;
    private Emergency emergency = null;
    private FireTruck fireTruck = null;
    private Call call = null;
    private FirefightersDepartment firefightersDepartment = null;
    private MessageConsumerWorker procedureEndedWorker;
    private int proceduresEndedCount;

    public DefaultFireProcedureBaseTest() {
    }

    @Before
    public void setUp() throws Exception {
        HumanTaskServerService.getInstance().initTaskServer();

        initializePersistenceAndTracking();

        emergency = new Emergency();


        fireTruck = new FireTruck("FireTruck 1");
        persistenceService.storeVehicle(fireTruck);

        call = new Call(1, 2, new Date());
        persistenceService.storeCall(call);

        emergency.setCall(call);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.FIRE);
        emergency.setNroOfPeople(1);
        persistenceService.storeEmergency(emergency);
        firefightersDepartment = new FirefightersDepartment("Firefighter Department 1", 12, 1);

        persistenceService.storeFirefightersDepartment(firefightersDepartment);


        MessageServerSingleton.getInstance().start();

        this.coreServicesMap = new HashMap();
        createRemoteNode();

        //Procedure Ended Worker
        procedureEndedWorker = new MessageConsumerWorker("ProcedureEndedCoreServer", new MessageConsumerWorkerHandler<ProcedureCompletedMessage>() {

            @Override
            public void handleMessage(ProcedureCompletedMessage procedureEndsMessage) {
                proceduresEndedCount++;
            }
        });

        procedureEndedWorker.start();
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
        if (procedureEndedWorker != null) {
            procedureEndedWorker.stopWorker();
        }
        HumanTaskServerService.getInstance().stopTaskServer();
        PersistenceServiceProvider.clear();
        ContextTrackingProvider.clear();
        ProceduresMGMTService.clear();
    }

    @Test
    public void defaultFireSimpleTest() throws Exception {
        //start the process
        this.startProcess(call, emergency);

        //Because of the emergency, a new Task is ready for garage: pick the corresponding vehicle/s
        List<Vehicle> trucks = new ArrayList<Vehicle>();
        trucks.add(fireTruck);
        this.testGarageTask(emergency, trucks);

        // The fire truck doesn't reach the emergency yet. No task for
        // the firefighter.
        Map<String, String> firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        // Now the fire truck arrives to the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(
                new VehicleHitsEmergencyMessage(fireTruck.getId(),
                emergency.getId(), new Date()));

        Thread.sleep(2000);

        // A new task for the firefighter should be there now
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertEquals(1, firefighterTasks.size());

        //The firefighter completes the task
        String firefighterTaskId = firefighterTasks.keySet().iterator().next();
        this.completeTask("firefighter", firefighterTaskId);

        // Becasuse the fire truck still got enough water, no "Water Refill"
        // task exists
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        //The process didn't finish yet
        Assert.assertEquals(0, proceduresEndedCount);

        // Ok, no more fire!
        ProceduresMGMTService.getInstance().notifyProcedures(
                new FireExtinctedMessage(emergency.getId(), new Date()));

        Thread.sleep(5000);

        //The emergency has ended
        Assert.assertEquals(1, proceduresEndedCount);

    }

    @Test
    public void fireTruckOutOfWaterx2Test() throws Exception {

        //start the process
        this.startProcess(call, emergency);

        //Because of the emergency, a new Task is ready for garage: pick the corresponding vehicle/s
        List<Vehicle> trucks = new ArrayList<Vehicle>();
        trucks.add(fireTruck);
        this.testGarageTask(emergency, trucks);

        // The fire truck doesn't reach the emergency yet. No task for
        // the firefighter.
        Map<String, String> firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        // Now the fire truck arrives to the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(
                new VehicleHitsEmergencyMessage(fireTruck.getId(),
                emergency.getId(), new Date()));

        Thread.sleep(2000);

        // A new task for the firefighter should be there now
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertEquals(1, firefighterTasks.size());

        //The firefighter completes the task
        String firefighterTaskId = firefighterTasks.keySet().iterator().next();
        this.completeTask("firefighter", firefighterTaskId);

        // Becasuse the fire truck still got enough water, no "Water Refill"
        // task exists
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        // Sudenly, the fire truck runs out of water
        ProceduresMGMTService.getInstance().notifyProcedures(
                new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
                new Date()));

        Thread.sleep(5000);

        //Now, the firefighter has a new task
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertEquals(1, firefighterTasks.size());

        firefighterTaskId = firefighterTasks.keySet().iterator().next();
        String firefighterTaskName = firefighterTasks.values().iterator().next();

        Assert.assertEquals(
                "Water Refill: go to ( " + firefightersDepartment.getX() + ", "
                + firefightersDepartment.getY() + " )", firefighterTaskName);

        // The firefighter completes the task
        this.completeTask("firefighter", firefighterTaskId);

        // No more tasks for firefighter
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        // The Fire Truck returns to the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(
                new VehicleHitsEmergencyMessage(fireTruck.getId(),
                emergency.getId(), new Date()));

        Thread.sleep(5000);

        // A new task for the firefighter should be there now
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertEquals(1, firefighterTasks.size());

        firefighterTaskId = firefighterTasks.keySet().iterator().next();

        // The firefighter completes the task
        this.completeTask("firefighter", firefighterTaskId);

        // Becasuse the fire truck still got enough water, no "Water Refill"
        // task exists
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        // Again, the fire truck runs out of water
        ProceduresMGMTService.getInstance().notifyProcedures(
                new FireTruckOutOfWaterMessage(emergency.getId(), fireTruck.getId(),
                new Date()));

        Thread.sleep(5000);

        firefighterTasks = this.getFirefighterTasks();
        Assert.assertEquals(1, firefighterTasks.size());

        firefighterTaskId = firefighterTasks.keySet().iterator().next();
        firefighterTaskName = firefighterTasks.values().iterator().next();

        Assert.assertEquals(
                "Water Refill: go to ( " + firefightersDepartment.getX() + ", "
                + firefightersDepartment.getY() + " )", firefighterTaskName);

        // The firefighter completes the task
        this.completeTask("firefighter", firefighterTaskId);

        // No more tasks for firefighter
        firefighterTasks = this.getFirefighterTasks();
        Assert.assertTrue(firefighterTasks.isEmpty());

        //The process didn't finish yet
        Assert.assertEquals(0, proceduresEndedCount);

        // Ok, no more fire!
        ProceduresMGMTService.getInstance().notifyProcedures(
                new FireExtinctedMessage(emergency.getId(), new Date()));

        Thread.sleep(5000);

        //The emergency has ended
        Assert.assertEquals(1, proceduresEndedCount);

    }

    private void startProcess(Call call, Emergency emergency) throws InterruptedException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);
        try {
            ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(),
                    "DefaultFireProcedure", parameters);
        } catch (IOException ex) {
            Logger.getLogger(DefaultFireProcedureBaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Thread.sleep(2000);
    }

    protected abstract void testGarageTask(Emergency emergency, List<Vehicle> selectedVehicles) throws Exception;

    protected abstract Map<String, String> getFirefighterTasks() throws Exception;

    protected abstract void completeTask(String user, String taskId) throws Exception;

    protected abstract void initializePersistenceAndTracking();
}