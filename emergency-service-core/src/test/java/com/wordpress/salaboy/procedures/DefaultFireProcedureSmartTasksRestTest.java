/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.procedures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.ws_ht.api.TTaskAbstract;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.wordpress.salaboy.api.HumanTaskService;
import com.wordpress.salaboy.api.HumanTaskServiceFactory;
import com.wordpress.salaboy.conf.HumanTaskServiceConfiguration;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import com.wordpress.salaboy.smarttasks.jbpm5wrapper.conf.JBPM5HornetQHumanTaskClientConfiguration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

/**
 *
 * @author salaboy
 * @author demianc
 */
public class DefaultFireProcedureSmartTasksRestTest extends DefaultFireProcedureBaseTest {

    private HumanTaskService humanTaskServiceClient;
    private ImpermanentGraphDatabase myDb;
    private WrappingNeoServerBootstrapper srv;

    public DefaultFireProcedureSmartTasksRestTest() {
    }

    @Before
    @Override
    public void setUp() throws Exception {
        myDb = new ImpermanentGraphDatabase();

        EmbeddedServerConfigurator config = new EmbeddedServerConfigurator(myDb);
        config.configuration().setProperty(
                Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);
        config.configuration().setProperty(
                Configurator.REST_API_PATH_PROPERTY_KEY,
                "http://localhost:7575/db/data/");
        srv = new WrappingNeoServerBootstrapper(myDb, config);
        srv.start();
        
        super.setUp();

        HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();

        taskClientConf.addHumanTaskClientConfiguration("jBPM5-HT-Client",
                new JBPM5HornetQHumanTaskClientConfiguration(
                "127.0.0.1", 5446));

      

        humanTaskServiceClient = HumanTaskServiceFactory.newHumanTaskService(taskClientConf);
        humanTaskServiceClient.initializeService();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        try {
            super.tearDown();
            myDb.shutdown();
            srv.stop();
        } finally {
            this.humanTaskServiceClient.cleanUpService();
        }
    }

    @Override
    protected void testGarageTask(Emergency emergency, List<Vehicle> selectedVehicles) throws Exception {
        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "garage_emergency_service", "", null, "", "", "", 0, 0);
        Assert.assertNotNull(taskAbstracts);
        Assert.assertEquals(1, taskAbstracts.size());
        TTaskAbstract taskAbstract = taskAbstracts.get(0); // getting the first task
        Assert.assertEquals(" Select Vehicle For " + emergency.getId() + " ", taskAbstract.getName().getLocalPart());

        //Garage team starts working on the task
        humanTaskServiceClient.setAuthorizedEntityId("garage_emergency_service");
        humanTaskServiceClient.start(taskAbstract.getId());


        //A Firetruck is selected
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.vehicles", selectedVehicles);

        //Garage team completes the task
        humanTaskServiceClient.complete(taskAbstract.getId(), info);

        Thread.sleep(2000);
    }

    @Override
    protected Map<String, String> getFirefighterTasks() throws Exception {
        humanTaskServiceClient.setAuthorizedEntityId("firefighter");
        List<TTaskAbstract> abstracts = humanTaskServiceClient.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0,
                0);
        Map<String, String> ids = new HashMap<String, String>();
        if (abstracts != null) {
            for (TTaskAbstract tTaskAbstract : abstracts) {
                ids.put(tTaskAbstract.getId(), tTaskAbstract.getName().toString());
            }
        }

        return ids;
    }

    @Override
    protected void completeTask(String user, String taskId) throws Exception {
        humanTaskServiceClient.setAuthorizedEntityId(user);

        Map<String, Object> info = new HashMap<String, Object>();
        info.put("emergency.priority", 1);
        humanTaskServiceClient.start(taskId);
        humanTaskServiceClient.complete(taskId, info);

        Thread.sleep(2000);
    }

    @Override
    protected void initializePersistenceAndTracking() {
        PersistenceServiceProvider.configFile = "remote-config-beans.xml";
        ContextTrackingProvider.configFile = "remote-config-beans.xml";
        persistenceService = PersistenceServiceProvider.getPersistenceService();
        trackingService = ContextTrackingProvider.getTrackingService();
    }
}