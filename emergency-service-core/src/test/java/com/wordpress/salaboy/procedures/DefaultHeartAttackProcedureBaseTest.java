package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;


import org.drools.grid.SocketService;

import com.wordpress.salaboy.grid.GridBaseTest;
import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.ProcedureCompletedMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsHospitalMessage;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import java.util.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public abstract class DefaultHeartAttackProcedureBaseTest extends GridBaseTest {

    private Emergency emergency = null;
    private Call call = null;
    private MessageConsumerWorker procedureEndedWorker;
    private int proceduresEndedCount;
    protected PersistenceService persistenceService;
    protected ContextTrackingService trackingService;
    
    private Ambulance ambulance1;
    private Ambulance ambulance2;

    public DefaultHeartAttackProcedureBaseTest() {
    }

    protected void setUp() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
        persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);

        trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));
        HumanTaskServerService.getInstance().initTaskServer();
        emergency = new Emergency();
        String emergencyId = trackingService.newEmergencyId();
        emergency.setId(emergencyId);
        call = new Call(1, 2, new Date());
        String callId = trackingService.newCallId();
        call.setId(callId);
        emergency.setCall(call);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);

        trackingService.attachEmergency(callId, emergencyId);

        persistenceService.storeHospital(new Hospital("My Hospital", 12, 1));
        persistenceService.storeEmergency(emergency);
        ambulance1 = new Ambulance("My Ambulance Number 1");
        persistenceService.storeVehicle(ambulance1);
        ambulance2 = new Ambulance("My Ambulance Number 2");
        persistenceService.storeVehicle(ambulance2);

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

    protected void tearDown() throws Exception {
        HumanTaskServerService.getInstance().stopTaskServer();
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

    }

    @Test
    public void defaultHeartAttackSimpleTest() throws Exception {


        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);


        ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(), "DefaultHeartAttackProcedure", parameters);
        Thread.sleep(5000);

        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        vehicles.add(ambulance1);
        
        doGarageTask(emergency, vehicles);

        Thread.sleep(5000);

        //The vehicle reaches the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulance1.getId(), emergency.getId(), new Date()));

        Thread.sleep(4000);
        
        //1 task for the doctor
        Map<String, String> doctorTasksId = getDoctorTasksId();
        Assert.assertEquals(1, doctorTasksId.size());
        
        //The doctor completes the task
        doDoctorTask(doctorTasksId.keySet().iterator().next());

        Thread.sleep(4000);

        //The process didn't finish yet
        Assert.assertEquals(0, proceduresEndedCount);

        //The vehicle reaches the hospital
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsHospitalMessage(ambulance1.getId(), new Hospital("Hospital A", 0, 0), emergency.getId(), new Date()));

        Thread.sleep(5000);

        //The emergency has ended
        Assert.assertEquals(1, proceduresEndedCount);

    }
    
    @Test
    public void defaultHeartAttackWith2VehiclesTest() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);

        //we want to dispatch 2 ambulances
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        
        vehicles.add(ambulance1);
        vehicles.add(ambulance2);

        ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(), "DefaultHeartAttackProcedure", parameters);
        Thread.sleep(5000);
        
        doGarageTask(emergency, vehicles);
        Thread.sleep(5000);
        
        //The vehicle 2 reaches the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulance2.getId(), emergency.getId(), new Date()));

        Thread.sleep(4000);
        
        //1 task for doctor now
        Map<String, String> doctorTasksId = getDoctorTasksId();
        Assert.assertEquals(1, doctorTasksId.size());
        
        
    }

    protected abstract void doGarageTask(Emergency emergency, List<Vehicle> selectedVehicles) throws Exception;

    protected abstract Map<String,String> getDoctorTasksId() throws Exception;
    
    protected abstract void doDoctorTask(String taskId) throws Exception;
}