/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.ProcedureCompleted;
import com.wordpress.salaboy.model.events.AllProceduresEndedEvent;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.services.workitemhandlers.AsyncStartProcedureWorkItemHandler;
import com.wordpress.salaboy.workitemhandlers.MyReportingWorkItemHandler;
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
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;

/**
 *
 * @author salaboy
 */
public class GenericEmergencyProcedureImpl extends AbstractProcedureService implements GenericEmergencyProcedure {

    private static GenericEmergencyProcedureImpl instance;

    private GenericEmergencyProcedureImpl() {
        super("GenericEmergencyProcedure");
        configure();

    }

    public static GenericEmergencyProcedureImpl getInstance() {
        if (instance == null) {
            instance = new GenericEmergencyProcedureImpl();
        }
        return instance;
    }

    private WorkingMemoryEntryPoint getPhoneCallsEntryPoint() {
        return internalSession.getWorkingMemoryEntryPoint("phoneCalls stream");
    }

    @Override
    public void newPhoneCall(Call call) {
        //Track new call
        getPhoneCallsEntryPoint().insert(call);
    }

    @Override
    public void allProceduresEnededNotification(AllProceduresEndedEvent event) {
        internalSession.signalEvent("com.wordpress.salaboy.model.events.AllProceduresEndedEvent", event);
    }

    @Override
    public void procedureCompletedNotification(String emergencyId, String procedureId) {
        internalSession.insert(new ProcedureCompleted(emergencyId, procedureId));
    }

    @Override
    public void configure(String emergencyId, Procedure procedure, Map<String, Object> parameters) {
        configure();
    }

    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event) {
        //@TODO: do we need to implement this method? I don't have the 
        //procedure Id here to insert the ProcedureCompleted Fact
    }

    public void configure() {
        System.out.println(">>> Initializing Generic Emergency Procedure Service ...");
        try {
            internalSession = createGenericEmergencyServiceSession();
        } catch (IOException ex) {
            Logger.getLogger(GenericEmergencyProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        setWorkItemHandlers();
        new Thread(new Runnable() {

            @Override
            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();
        System.out.println(">>> Generic Emergency Procedure Service Running ...");
    }

    private void setWorkItemHandlers() {
        internalSession.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedHornetQWSHumanTaskHandler(internalSession));
        internalSession.getWorkItemManager().registerWorkItemHandler("Start Procedure", new AsyncStartProcedureWorkItemHandler());
        internalSession.getWorkItemManager().registerWorkItemHandler("Reporting", new MyReportingWorkItemHandler());

    }

    private StatefulKnowledgeSession createGenericEmergencyServiceSession() throws IOException {
        //@TODO: Add proper log
        System.out.println(">>>> I'm creating the " + "GenericEmergencyProcedure");
        GridNode remoteN1 = null;
        KnowledgeBuilder kbuilder = null;
        KnowledgeBase kbase = null;

        if (useLocalKSession) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
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

            kbuilder = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder();

            KnowledgeBaseConfiguration kbaseConf = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBaseConfiguration();
            kbaseConf.setOption(EventProcessingOption.STREAM);
            kbase = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(kbaseConf);
        }

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/GenericEmergencyProcedure.bpmn").getInputStream())), ResourceType.BPMN2);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/phoneCallsManagement.drl").getInputStream())), ResourceType.DRL);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/procedureSuggestions.drl").getInputStream())), ResourceType.DRL);

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/emergencyLifeCycle.drl").getInputStream())), ResourceType.DRL);

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
            remoteN1.set("GenericEmergencyProcedureSession", session);
        }

        return session;

    }

    @Override
    public String getProcedureId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getEmergencyId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
