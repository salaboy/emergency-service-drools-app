/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.acc.FirefighterDeparmtmentDistanceCalculator;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.model.events.FireTruckOutOfWaterEvent;
import com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent;
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
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
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
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;

/**
 *
 * @author esteban
 */
public class DefaultFireProcedureImpl implements DefaultFireProcedure {

    private String callId;
    private StatefulKnowledgeSession internalSession;
    private String procedureName;
    private boolean useLocalKSession;

    public DefaultFireProcedureImpl() {
        this.procedureName = "DefaultFireProcedure";
    }

    private StatefulKnowledgeSession createDefaultFireProcedureSession(String callId) throws IOException {
        
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

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        if (!useLocalKSession){
            remoteN1.set("DefaultFireProcedureSession" + this.callId, session);
        }

        return session;

    }

    private void setWorkItemHandlers(StatefulKnowledgeSession session) {
        session.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedHornetQWSHumanTaskHandler(session));
    }

    @Override
    public void vehicleReachesEmergencyNotification(VehicleHitsEmergencyEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.VehicleHitsEmergencyEvent", event);
    }

    @Override
    public void fireTruckOutOfWaterNotification(FireTruckOutOfWaterEvent event) {
        //we need the event as a fact in order to make inference.
        internalSession.insert(event);
        
        //the process is signaled
        internalSession.signalEvent("com.wordpress.salaboy.model.events.FireTruckOutOfWaterEvent", event);
    }

    @Override
    public void configure(String callId, Map<String, Object> parameters) {
	if (!parameters.containsKey("emergency")){
            throw new IllegalStateException("Trying to start DefaultFireProcedure wihtout passing an Emergency!");
        }

        this.callId = callId;
        try {
            internalSession = createDefaultFireProcedureSession(this.callId);
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
        
        ProcessInstance processInstance = internalSession.startProcess("com.wordpress.salaboy.bpmn2.DefaultFireProcedure", parameters);
        internalSession.insert(processInstance);
        internalSession.insert(parameters.get("emergency"));
    }
    
    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.EmergencyEndsEvent", event);
    }

    public boolean isUseLocalKSession() {
        return useLocalKSession;
    }

    public void setUseLocalKSession(boolean useLocalKSession) {
        this.useLocalKSession = useLocalKSession;
    }
}
