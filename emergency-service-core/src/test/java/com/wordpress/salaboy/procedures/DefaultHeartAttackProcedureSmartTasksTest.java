/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.procedures;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.example.ws_ht.api.TAttachment;
import org.example.ws_ht.api.TAttachmentInfo;
import org.example.ws_ht.api.TTask;
import org.example.ws_ht.api.TTaskAbstract;
import org.junit.After;
import org.junit.Before;

import com.wordpress.salaboy.api.HumanTaskService;
import com.wordpress.salaboy.api.HumanTaskServiceFactory;
import com.wordpress.salaboy.conf.HumanTaskServiceConfiguration;
import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;

/**
 *
 * @author salaboy
 */
public class DefaultHeartAttackProcedureSmartTasksTest extends DefaultHeartAttackProcedureBaseTest {

    private HumanTaskService humanTaskServiceClient;

    public DefaultHeartAttackProcedureSmartTasksTest() {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();
        taskClientConf.addHumanTaskClientConfiguration("jBPM5-HT-Client", new JBPM5HornetQHumanTaskClientConfiguration("127.0.0.1", 5446));
        humanTaskServiceClient = HumanTaskServiceFactory.newHumanTaskService(taskClientConf);
        humanTaskServiceClient.initializeService();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        this.humanTaskServiceClient.cleanUpService();
    }

    @Override
    protected String doGarageTask(Emergency emergency) throws Exception {
        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "garage_emergency_service", "", null, "", "", "", 0, 0);
        assertNotNull(taskAbstracts);
        Assert.assertEquals(1, taskAbstracts.size());
        TTaskAbstract taskAbstract = taskAbstracts.get(0); // getting the first task
        Assert.assertEquals(" Select Vehicle For " + emergency.getId() + " ", taskAbstract.getName().getLocalPart());



        // I need to get the Content Data and check the values of the Emergency and Call Ids.
        // Using that I need to select one vehicle ID from the list of all the vehicles. 

        TTask task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
        assertNotNull(task);

        humanTaskServiceClient.setAuthorizedEntityId("garage_emergency_service");
        humanTaskServiceClient.start(task.getId());

        List<TAttachmentInfo> attachmentsInfo = humanTaskServiceClient.getAttachmentInfos(task.getId());
        TAttachmentInfo firstAttachmentInfo = attachmentsInfo.get(0);
        TAttachment attachment = humanTaskServiceClient.getAttachments(task.getId(), firstAttachmentInfo.getName()).get(0);

        String value = (String) ((Map) attachment.getValue()).get("Content");

        assertNotNull(value, "1,1");


        Map<String, Object> info = new HashMap<String, Object>();
        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        Ambulance ambulance = new Ambulance("My Ambulance", new Date());
        String ambulanceId = ContextTrackingServiceImpl.getInstance().newVehicleId();
        ambulance.setId(ambulanceId);
        vehicles.add(ambulance);
        //ContextTrackingServiceImpl.getInstance().attachVehicle(, ambulanceId);
        info.put("emergency.vehicles", vehicles);


        humanTaskServiceClient.complete(task.getId(), info);

        return ambulanceId;
    }

    @Override
    protected void doDoctorTask() throws Exception {
        humanTaskServiceClient.setAuthorizedEntityId("doctor");
        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "doctor", "", null, "", "", "", 0, 0);
        assertNotNull(taskAbstracts);
        Assert.assertEquals(1, taskAbstracts.size());
        TTaskAbstract taskAbstract = taskAbstracts.get(0);
        TTask task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
        assertNotNull(task);

        humanTaskServiceClient.start(task.getId());
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);

        humanTaskServiceClient.complete(task.getId(), info);
    }
}