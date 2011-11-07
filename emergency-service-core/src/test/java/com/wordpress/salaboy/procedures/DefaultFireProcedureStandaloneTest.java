/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.acc.FirefighterDeparmtmentDistanceCalculator;
import java.util.Date;

import java.util.List;
import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.wordpress.salaboy.api.HumanTaskService;
import com.wordpress.salaboy.api.HumanTaskServiceFactory;
import com.wordpress.salaboy.conf.HumanTaskServiceConfiguration;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FireTruck;
import com.wordpress.salaboy.model.FirefightersDepartment;
import com.wordpress.salaboy.model.Location;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.conf.AccumulateFunctionOption;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.example.ws_ht.api.TTaskAbstract;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author esteban
 */
public class DefaultFireProcedureStandaloneTest {

    private KnowledgeBase kbase;
    private HumanTaskService humanTaskServiceClient;

    public DefaultFireProcedureStandaloneTest() {
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

        emergency = new Emergency();
        emergency.setId("Emergency1");
        fireTruck = new FireTruck();
        fireTruck.setId("FireTruck1");

        call = new Call(1, 2, new Date());

        call.setId("Call1");
        emergency.setCall(call);
        emergency.setLocation(new Location(1, 2));
        emergency.setType(Emergency.EmergencyType.FIRE);
        emergency.setNroOfPeople(1);

        firefightersDepartment = new FirefightersDepartment(
                "Firefighter Department 1", 12, 1);

        MessageServerSingleton.getInstance().start();

        HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();

        taskClientConf.addHumanTaskClientConfiguration("jBPM5-HT-Client",
                new JBPM5HornetQHumanTaskClientConfiguration(
                "127.0.0.1", 5446));

        humanTaskServiceClient = HumanTaskServiceFactory.newHumanTaskService(taskClientConf);
        humanTaskServiceClient.initializeService();

        initKBase();

    }

    @After
    public void tearDown() throws Exception {
        MessageServerSingleton.getInstance().stop();
        this.humanTaskServiceClient.cleanUpService();
    }

    @Test
    public void standaloneTest() throws Exception {
        StatefulKnowledgeSession ksession = this.getNewKSession();
        
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("call", call);
        parameters.put("emergency", emergency);
        parameters.put("vehicle", fireTruck);
        
        ksession.startProcess("com.wordpress.salaboy.bpmn2.DefaultFireProcedure",parameters);

        // The fire truck doesn't reach the emergency yet. No task for
        // the firefighter.
        humanTaskServiceClient.setAuthorizedEntityId("firefighter");
        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0,
                0);

        Assert.assertTrue(taskAbstracts.isEmpty());

        // Now the fire truck arrives to the emergency
        ksession.signalEvent(
                "com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent", 
                new VehicleHitsEmergencyEvent(emergency.getId(), 
                        fireTruck.getId(), 
                        new Date())
                );
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

        // Ok, the emregency ends
        ksession.signalEvent(
                "com.wordpress.salaboy.model.events.EmergencyEndsEvent", 
                new EmergencyEndsEvent(emergency.getId(), 
                        new Date())
                );
        // TODO: validate that the process has finished

    }

    private StatefulKnowledgeSession getNewKSession() {
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedHornetQWSHumanTaskHandler(session));
        return session;
    }

    private void initKBase() throws Exception {
        KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbuilderConf.setOption(AccumulateFunctionOption.get("firefighterDeparmtmentDistanceCalculator", new FirefighterDeparmtmentDistanceCalculator()));
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
        KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseConf.setOption(EventProcessingOption.STREAM);
        kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/MultiVehicleProcedure.bpmn").getInputStream())), ResourceType.BPMN2);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/DefaultFireProcedure.bpmn").getInputStream())), ResourceType.BPMN2);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/select_water_refill_destination.drl").getInputStream())), ResourceType.DRL);

        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors != null && errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>>>>>> Error: " + error.getMessage());

            }
            throw new IllegalStateException("Failed to parse knowledge!");
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    }
}