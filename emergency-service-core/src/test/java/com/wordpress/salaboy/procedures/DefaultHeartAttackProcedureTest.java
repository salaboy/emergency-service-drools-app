/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.wordpress.salaboy.model.events.PatientAtHospitalEvent;
import com.wordpress.salaboy.model.events.PatientPickUpEvent;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import com.wordpress.salaboy.services.DefaultHeartAttackProcedure;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;

/**
 *
 * @author salaboy
 */
public class DefaultHeartAttackProcedureTest extends GridBaseTest {

    private MessageConsumer consumer;
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

    @Before
    public void setUp() throws Exception {
        Emergency emergency = new Emergency(1L);
        emergency.setCall(new Call(1,2,new Date()));
        emergency.setLocation(new Location(1,2));
        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
        emergency.setNroOfPeople(1);
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
        parameters.put("call.id", 1L);
        parameters.put("emergency.id", 1L);



        ProceduresMGMTService.getInstance().newRequestedProcedure((Long) parameters.get("call.id"), "DefaultHeartAttackProcedure", parameters);

        Thread.sleep(5000);

        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "garage_emergency_service", "", null, "", "", "", 0, 0);
        assertNotNull(taskAbstracts);
        assertEquals(1, taskAbstracts.size());
        TTaskAbstract taskAbstract = taskAbstracts.get(0); // getting the first task
        assertEquals(" Select Vehicle For 1 ", taskAbstract.getName().getLocalPart());
        


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
        vehicles.add(new Ambulance("My Ambulance", new Date()));
        info.put("emergency.vehicles", vehicles);
        
        
        humanTaskServiceClient.complete(task.getId(), info);

        Thread.sleep(4000);
        
        ((DefaultHeartAttackProcedure) ProceduresMGMTService.getInstance().getProcedureService(1L)).patientPickUpNotification(new PatientPickUpEvent(1L, 1L, new Date()));

        Thread.sleep(4000);
        
        humanTaskServiceClient.setAuthorizedEntityId("doctor");
        taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "doctor", "", null, "", "", "", 0, 0);
        assertNotNull(taskAbstracts);
        assertEquals(1, taskAbstracts.size());
        taskAbstract = taskAbstracts.get(0); 
        
        task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
        assertNotNull(task);
        
        humanTaskServiceClient.start(task.getId());
        
        info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);
        
        humanTaskServiceClient.complete(task.getId(), info);


        Thread.sleep(4000);

        ((DefaultHeartAttackProcedure) ProceduresMGMTService.getInstance().getProcedureService(1L))
                        .patientAtHospitalNotification(new PatientAtHospitalEvent(1L, 1L, 1L, new Date()));

        Thread.sleep(4000);
        
        


    }
}