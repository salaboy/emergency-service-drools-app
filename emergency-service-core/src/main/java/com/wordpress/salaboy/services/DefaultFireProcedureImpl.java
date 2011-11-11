/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.acc.FirefighterDeparmtmentDistanceCalculator;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.events.*;
import com.wordpress.salaboy.services.workitemhandlers.ProcedureReportWorkItemHandler;
import com.wordpress.salaboy.workitemhandlers.DispatchVehicleWorkItemHandler;
import com.wordpress.salaboy.workitemhandlers.NotifyEndOfProcedureWorkItemHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.builder.*;
import org.drools.builder.conf.AccumulateFunctionOption;
import org.drools.conf.EventProcessingOption;
import org.drools.grid.ConnectionFactoryService;
import org.drools.grid.GridConnection;
import org.drools.grid.GridNode;
import org.drools.grid.GridServiceDescription;
import org.drools.grid.conf.GridPeerServiceConfiguration;
import org.drools.grid.conf.impl.GridPeerConfiguration;
import org.drools.grid.impl.GridImpl;
import org.drools.grid.service.directory.Address;
import org.drools.grid.service.directory.WhitePages;
import org.drools.grid.service.directory.impl.CoreServicesLookupConfiguration;
import org.drools.grid.service.directory.impl.GridServiceDescriptionImpl;
import org.drools.grid.service.directory.impl.WhitePagesRemoteConfiguration;
import org.drools.io.impl.ByteArrayResource;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;

/**
 *
 * @author esteban
 * @author salaboy
 */
public class DefaultFireProcedureImpl extends AbstractProcedureService implements DefaultFireProcedure {

    private String emergencyId;
    private String procedureId;
    private ProcessInstance processInstance;

    public DefaultFireProcedureImpl() {
        super("com.wordpress.salaboy.bpmn2.DefaultFireProcedure");
    }

    @Override
    public void vehicleReachesEmergencyNotification(VehicleHitsEmergencyEvent event) {
        internalSession.insert(event);
    }

    @Override
    public void fireTruckOutOfWaterNotification(FireTruckOutOfWaterEvent event) {
        //@TODO: add proper log
        System.out.println(">>>>>>>> Inserting FireTruckOutOfWaterEvent ");
        internalSession.insert(event);
    }

    @Override
    public void fireExtinctedNotification(FireExtinctedEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.FireExtinctedEvent", event);
    }

    @Override
    public void vehicleHitsFireDepartmentEventNotification(VehicleHitsFireDepartmentEvent vehicleHitsFireDepartmentEvent) {
        internalSession.insert(vehicleHitsFireDepartmentEvent);
    }

    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.EmergencyEndsEvent", event);
    }

    @Override
    public void configure(String emergencyId, Procedure procedure, Map<String, Object> parameters) {
        if (!parameters.containsKey("emergency")) {
            throw new IllegalStateException("Trying to start DefaultFireProcedure wihtout passing an Emergency!");
        }

        this.emergencyId = emergencyId;
        this.procedureId = procedure.getId();

        try {
            internalSession = createDefaultFireProcedureKnowledgeContext(this.emergencyId);
        } catch (IOException ex) {
            Logger.getLogger(DefaultFireProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        setWorkItemHandlers(internalSession);

        new Thread(new Runnable() {

            @Override
            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();

        parameters.put("concreteProcedureId", this.procedureName);
        parameters.put("procedure", procedure);

        internalSession.insert(parameters.get("emergency"));
        processInstance = internalSession.startProcess("com.wordpress.salaboy.bpmn2.MultiVehicleProcedure", parameters);


        procedure.setProcessInstanceId(processInstance.getId());
    }

    private void setWorkItemHandlers(StatefulKnowledgeSession session) {
        session.getWorkItemManager().registerWorkItemHandler("Report", new ProcedureReportWorkItemHandler());
        session.getWorkItemManager().registerWorkItemHandler("DispatchSelectedVehicle", new DispatchVehicleWorkItemHandler());
        session.getWorkItemManager().registerWorkItemHandler("NotifyEndOfProcedure", new NotifyEndOfProcedureWorkItemHandler());
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedHornetQWSHumanTaskHandler(session));

    }

    private StatefulKnowledgeSession createDefaultFireProcedureKnowledgeContext(String emergencyId) throws IOException {
        System.out.println(">>>> I'm creating the DefaultFireProcedure procedure for emergencyId = " + emergencyId);
        GridNode remoteN1 = null;

        KnowledgeBuilder kbuilder = null;
        KnowledgeBase kbase = null;
        if (useLocalKSession) {
            KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
            kbuilderConf.setOption(AccumulateFunctionOption.get("firefighterDeparmtmentDistanceCalculator", new FirefighterDeparmtmentDistanceCalculator()));
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
            KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kbaseConf.setOption(EventProcessingOption.STREAM);
            kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
        } else {
            Map<String, GridServiceDescription> coreServicesMap = new HashMap<String, GridServiceDescription>();
            GridServiceDescriptionImpl gsd = new GridServiceDescriptionImpl(WhitePages.class.getName());
            Address addr = gsd.addAddress("socket");
            addr.setObject(new InetSocketAddress[]{new InetSocketAddress("localhost", 8000)});
            coreServicesMap.put(WhitePages.class.getCanonicalName(), gsd);

            GridImpl grid = new GridImpl(new ConcurrentHashMap<String, Object>());

            GridPeerConfiguration conf = new GridPeerConfiguration();
            GridPeerServiceConfiguration coreSeviceConf = new CoreServicesLookupConfiguration(coreServicesMap);
            conf.addConfiguration(coreSeviceConf);

            GridPeerServiceConfiguration wprConf = new WhitePagesRemoteConfiguration();
            conf.addConfiguration(wprConf);

            conf.configure(grid);

            GridServiceDescription<GridNode> n1Gsd = grid.get(WhitePages.class).lookup("n1");
            GridConnection<GridNode> conn = grid.get(ConnectionFactoryService.class).createConnection(n1Gsd);
            remoteN1 = conn.connect();

            KnowledgeBuilderConfiguration kbuilderConf = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilderConfiguration();
            kbuilderConf.setOption(AccumulateFunctionOption.get("firefighterDeparmtmentDistanceCalculator", new FirefighterDeparmtmentDistanceCalculator()));
            kbuilder = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder(kbuilderConf);

            KnowledgeBaseConfiguration kbaseConf = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBaseConfiguration();
            kbaseConf.setOption(EventProcessingOption.STREAM);
            kbase = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(kbaseConf);
        }


        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/MultiVehicleProcedure.bpmn").getInputStream())), ResourceType.BPMN2);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/DefaultFireProcedure.bpmn").getInputStream())), ResourceType.BPMN2);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/select_water_refill_destination.drl").getInputStream())), ResourceType.DRL);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/defaultFireProcedureEventHandling.drl").getInputStream())), ResourceType.DRL);

        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors != null && errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>>>>>> Error: " + error.getMessage());

            }
            throw new IllegalStateException("Failed to parse knowledge!");
        }

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        if (useLocalKSession) {
            KnowledgeRuntimeLoggerFactory.newConsoleLogger(session);
        }
        if (!useLocalKSession) {
            remoteN1.set("DefaultFireProcedureSession" + this.emergencyId, session);
        }

        return session;

    }

    @Override
    public String getProcedureId() {
        if (this.procedureId == null && this.procedureId.equals("")) {
            throw new IllegalStateException("Procedure Service wasn't configured, you must configure it first!");
        }
        return procedureId;
    }

    @Override
    public String getEmergencyId() {
        return this.emergencyId;
    }
}
