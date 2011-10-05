
package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.drools.grid.SocketService;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.wordpress.salaboy.grid.GridBaseTest;
import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.messages.ProcedureCompletedMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsHospitalMessage;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import org.junit.Assert;
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

    public DefaultHeartAttackProcedureBaseTest() {
    }

    protected void setUp() throws Exception {
        HumanTaskServerService.getInstance().initTaskServer();
        emergency = new Emergency();
        String emergencyId = ContextTrackingServiceImpl.getInstance().newEmergencyId();
        emergency.setId(emergencyId);
        call = new Call(1, 2, new Date());
        String callId = ContextTrackingServiceImpl.getInstance().newCallId();
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

        String ambulanceId = doGarageTask(emergency);

        Thread.sleep(5000);

        //The vehicle reaches the emergency
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulanceId, emergency.getId(), new Date()));

        Thread.sleep(4000);

        doDoctorTask();

        Thread.sleep(4000);

        //The process didn't finish yet
        Assert.assertEquals(0, proceduresEndedCount);

        //The vehicle reaches the hospital
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsHospitalMessage(ambulanceId, new Hospital("Hospital A", 0, 0), emergency.getId(), new Date()));

        Thread.sleep(5000);

        //The emergency has ended
        Assert.assertEquals(1, proceduresEndedCount);
        
    }

    protected abstract String doGarageTask(Emergency emergency) throws Exception;

    protected abstract void doDoctorTask() throws Exception;
}