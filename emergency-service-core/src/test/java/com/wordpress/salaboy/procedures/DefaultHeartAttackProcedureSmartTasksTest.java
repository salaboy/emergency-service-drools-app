/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.procedures;


import com.wordpress.salaboy.api.HumanTaskService;
import com.wordpress.salaboy.api.HumanTaskServiceFactory;
import com.wordpress.salaboy.conf.HumanTaskServiceConfiguration;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingServiceImpl;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;
import java.util.*;
import junit.framework.Assert;
import org.example.ws_ht.api.TAttachment;
import org.example.ws_ht.api.TAttachmentInfo;
import org.example.ws_ht.api.TTask;
import org.example.ws_ht.api.TTaskAbstract;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;

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
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
        persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);

        trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        this.humanTaskServiceClient.cleanUpService();
       
    }

    @Override
    protected void doGarageTask(Emergency emergency, List<Vehicle> selectedVehicles) throws Exception {
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
        String ambulanceId = trackingService.newVehicleId();
        ambulance.setId(ambulanceId);
        vehicles.add(ambulance);
        //ContextTrackingServiceImpl.getInstance().attachVehicle(, ambulanceId);
        info.put("emergency.vehicles", selectedVehicles);

        humanTaskServiceClient.complete(task.getId(), info);
    }

    @Override
    protected void doDoctorTask(String taskId) throws Exception {
        humanTaskServiceClient.start(taskId);
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);

        humanTaskServiceClient.complete(taskId, info);
    }

    @Override
    protected Map<String, String> getDoctorTasksId() throws Exception {
        humanTaskServiceClient.setAuthorizedEntityId("doctor");
        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "doctor", "", null, "", "", "", 0, 0);
        
        Map<String, String> ids = new HashMap<String, String>();
        for (TTaskAbstract taskAbstract : taskAbstracts) {
            ids.put(taskAbstract.getId()+"", taskAbstract.getName().toString());
        }
        
        return ids;
    }

}