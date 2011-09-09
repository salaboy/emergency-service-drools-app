/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

//import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.drools.grid.SocketService;
import org.example.ws_ht.api.TAttachment;
import org.example.ws_ht.api.TAttachmentInfo;
import org.example.ws_ht.api.TTask;
import org.example.ws_ht.api.TTaskAbstract;
import org.example.ws_ht.api.wsdl.IllegalAccessFault;
import org.example.ws_ht.api.wsdl.IllegalArgumentFault;
import org.example.ws_ht.api.wsdl.IllegalStateFault;
import org.hornetq.api.core.HornetQException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordpress.salaboy.api.HumanTaskService;
import com.wordpress.salaboy.api.HumanTaskServiceFactory;
import com.wordpress.salaboy.conf.HumanTaskServiceConfiguration;
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
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;
import com.wordpress.salaboy.tracking.ContextTrackingServiceImpl;

/**
 *
 * @author salaboy
 */
public class DefaultHeartAttackProcedureTest extends GridBaseTest {
    private HumanTaskService humanTaskServiceClient;
    

    public DefaultHeartAttackProcedureTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        HumanTaskServerService.getInstance().initTaskServer();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {

        HumanTaskServerService.getInstance().stopTaskServer();
    }

    Emergency emergency = null;
    Call call = null;
    @Before
    public void setUp() throws Exception {
        emergency = new Emergency();
        String emergencyId = ContextTrackingServiceImpl.getInstance().newEmergency();
        emergency.setId(emergencyId);
        call = new Call(1,2,new Date());
        String callId = ContextTrackingServiceImpl.getInstance().newCall();
        call.setId(callId);
        emergency.setCall(call);
        emergency.setLocation(new Location(1,2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);
        
        ContextTrackingServiceImpl.getInstance().attachEmergency(callId, emergencyId);
        
        DistributedPeristenceServerService.getInstance().storeHospital(new Hospital("My Hospital", 12, 1));
        DistributedPeristenceServerService.getInstance().storeEmergency(emergency);
        DistributedPeristenceServerService.getInstance().storeVehicle(new Ambulance("My Ambulance Test"));
        MessageServerSingleton.getInstance().start();



        this.coreServicesMap = new HashMap();
        createRemoteNode();

        HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();


        taskClientConf.addHumanTaskClientConfiguration("jBPM5-HT-Client", new JBPM5HornetQHumanTaskClientConfiguration("127.0.0.1", 5446));

        humanTaskServiceClient = HumanTaskServiceFactory.newHumanTaskService(taskClientConf);
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
    }

    @Test
    public void defaultHeartAttackSimpleTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException, IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {


        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency); 



        //ProceduresMGMTService.getInstance().newRequestedProcedure(((Call) parameters.get("call")).getId(), "DefaultHeartAttackProcedure", parameters);
        ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(), "DefaultHeartAttackProcedure", parameters);
        Thread.sleep(5000);

        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "garage_emergency_service", "", null, "", "", "", 0, 0);
        assertNotNull(taskAbstracts);
        Assert.assertEquals(1, taskAbstracts.size());
        TTaskAbstract taskAbstract = taskAbstracts.get(0); // getting the first task
        Assert.assertEquals(" Select Vehicle For "+call.getId()+" ", taskAbstract.getName().getLocalPart());
        


        // I need to get the Content Data and check the values of the Emergency and Call Ids.
        // Using that I need to select one vehicle ID from the list of all the vehicles. 

        TTask task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
        assertNotNull(task);
        
        humanTaskServiceClient.setAuthorizedEntityId("garage_emergency_service");
        humanTaskServiceClient.start(task.getId());

        List<TAttachmentInfo> attachmentsInfo = humanTaskServiceClient.getAttachmentInfos(task.getId());
        TAttachmentInfo firstAttachmentInfo = attachmentsInfo.get(0);
        TAttachment attachment = humanTaskServiceClient.getAttachments(task.getId(), firstAttachmentInfo.getName()).get(0);

        String value = (String)((Map)attachment.getValue()).get("Content");
        
        assertNotNull(value, "1,1"); 

        
        Map<String, Object> info = new HashMap<String, Object>();
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        Ambulance ambulance = new Ambulance("My Ambulance", new Date());
        String ambulanceId = ContextTrackingServiceImpl.getInstance().newVehicle();
        ambulance.setId(ambulanceId);
        vehicles.add(ambulance);
        //ContextTrackingServiceImpl.getInstance().attachVehicle(, ambulanceId);
        info.put("emergency.vehicles", vehicles);
        
        
        humanTaskServiceClient.complete(task.getId(), info);

        Thread.sleep(4000);
        
        //The vehicle reaches the emergency
        //ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulanceId, call.getId(), new Date()));
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulanceId, emergency.getId(), new Date()));

        Thread.sleep(4000);
        
        humanTaskServiceClient.setAuthorizedEntityId("doctor");
        taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "doctor", "", null, "", "", "", 0, 0);
        assertNotNull(taskAbstracts);
        Assert.assertEquals(1, taskAbstracts.size());
        taskAbstract = taskAbstracts.get(0); 
        
        task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
        assertNotNull(task);
        
        humanTaskServiceClient.start(task.getId());
        
        info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);
        
        humanTaskServiceClient.complete(task.getId(), info);


        Thread.sleep(4000);

        //The vehicle reaches the hospital
        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsHospitalMessage(ambulanceId, new Hospital("Hospital A", 0, 0), emergency.getId(), new Date()));
        
        Thread.sleep(4000);

    }
}