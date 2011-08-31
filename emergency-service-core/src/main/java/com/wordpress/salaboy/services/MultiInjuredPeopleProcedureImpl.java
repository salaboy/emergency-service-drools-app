/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.acc.HospitalDistanceCalculator;
import com.wordpress.salaboy.model.ProcedureRequest;
import com.wordpress.salaboy.model.events.VehicleHitsHospitalEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.services.workitemhandlers.LocalReportWorkItemHandler;
import com.wordpress.salaboy.workitemhandlers.DispatchSelectedVehiclesWorkItemHandler;
import com.wordpress.salaboy.workitemhandlers.DispatchSingleSelectedVehicleWorkItemHandler;

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
import org.drools.KnowledgeBaseFactoryService;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.builder.ResourceType;
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
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;

/**
 * Procedure to start a procedure with many injured prople.
 * @author calcacuervo
 *
 */
public class MultiInjuredPeopleProcedureImpl implements DefaultHeartAttackProcedure {

    private String callId;
        private StatefulKnowledgeSession internalSession;
    private String procedureName;
    
    public MultiInjuredPeopleProcedureImpl() {
        this.procedureName = "DefaultHeartAttackProcedure";
    }

    private StatefulKnowledgeSession createDefaultHeartAttackProcedureSession(String callId) throws IOException {

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
        GridNode remoteN1 = conn.connect();

        KnowledgeBuilderConfiguration kbuilderConf = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilderConfiguration();
        kbuilderConf.setOption(AccumulateFunctionOption.get("hospitalDistanceCalculator", new HospitalDistanceCalculator()));
        KnowledgeBuilder kbuilder = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder(kbuilderConf);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/MultiInjuredEmergencyProcedure.bpmn").getInputStream())), ResourceType.BPMN2);
        
        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/proceduresRequestHandler.drl").getInputStream())), ResourceType.DRL);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/select_hospital.drl").getInputStream())), ResourceType.DRL);


        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors != null && errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>>>>>> Error: " + error.getMessage());

            }
            throw new IllegalStateException("Failed to parse knowledge!");
        }

        KnowledgeBaseConfiguration kbaseConf = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBaseConfiguration();
        kbaseConf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(kbaseConf);

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        session.getWorkItemManager().registerWorkItemHandler("DispatchSelectedVehicles", new DispatchSingleSelectedVehicleWorkItemHandler());
        
        //This handlers are for report
        session.getWorkItemManager().registerWorkItemHandler("Report", new LocalReportWorkItemHandler());
        session.getWorkItemManager().registerWorkItemHandler("GlobalReport", new SystemOutWorkItemHandler());



        remoteN1.set("DefaultHeartAttackProcedureSession" + this.callId, session);

        return session;

    }

    private void setWorkItemHandlers(StatefulKnowledgeSession session) {
        //session.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(session));
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedHornetQWSHumanTaskHandler(session));
    }

    @Override
    public void patientAtHospitalNotification(VehicleHitsHospitalEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.PatientAtHospitalEvent", event);
    }

    @Override
    public void patientPickUpNotification(VehicleHitsEmergencyEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.PatientPickUpEvent", event);
    }

    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event){

    }
    
    @Override
    public void configure(String callId, Map<String, Object> parameters) {
        this.callId = callId;
        try {
            internalSession = createDefaultHeartAttackProcedureSession(this.callId);
        } catch (IOException ex) {
            Logger.getLogger(MultiInjuredPeopleProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        setWorkItemHandlers(internalSession);

        new Thread(new Runnable() {
            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();
        
         internalSession.getWorkingMemoryEntryPoint("procedure request").insert(new ProcedureRequest(this.procedureName, parameters));
    }
    
    
}
