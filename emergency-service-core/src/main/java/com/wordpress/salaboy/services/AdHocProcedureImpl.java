/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.services.workitemhandlers.AsyncStartProcedureWorkItemHandler;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
public class AdHocProcedureImpl implements AdHocProcedure {

    private String emergencyId;
    private StatefulKnowledgeSession internalSession;
    private String procedureName;
    private String procedureId;

    public AdHocProcedureImpl() {
        this.procedureName = "AdHocProcedure";
    }

    private StatefulKnowledgeSession createAdHocProcedureSession(String callId) throws IOException {


//        Map<String, GridServiceDescription> coreServicesMap = new HashMap<String, GridServiceDescription>();
//        GridServiceDescriptionImpl gsd = new GridServiceDescriptionImpl(WhitePages.class.getName());
//        Address addr = gsd.addAddress("socket");
//        addr.setObject(new InetSocketAddress[]{new InetSocketAddress("localhost", 8000)});
//        coreServicesMap.put(WhitePages.class.getCanonicalName(), gsd);
//
//        GridImpl grid = new GridImpl(new ConcurrentHashMap<String, Object>());
//
//        GridPeerConfiguration conf = new GridPeerConfiguration();
//        GridPeerServiceConfiguration coreSeviceConf = new CoreServicesLookupConfiguration(coreServicesMap);
//        conf.addConfiguration(coreSeviceConf);
//
//        GridPeerServiceConfiguration wprConf = new WhitePagesRemoteConfiguration();
//        conf.addConfiguration(wprConf);
//
//        conf.configure(grid);
//
//        GridServiceDescription<GridNode> n1Gsd = grid.get(WhitePages.class).lookup("n1");
//        GridConnection<GridNode> conn = grid.get(ConnectionFactoryService.class).createConnection(n1Gsd);
//        GridNode remoteN1 = conn.connect();
//
//        KnowledgeBuilderConfiguration kbuilderConf = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilderConfiguration();
//        KnowledgeBuilder kbuilder = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder(kbuilderConf);
//
//        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/AdHocProcedure.bpmn").getInputStream())), ResourceType.BPMN2);
//
//
//        KnowledgeBuilderErrors errors = kbuilder.getErrors();
//        if (errors != null && errors.size() > 0) {
//            for (KnowledgeBuilderError error : errors) {
//                System.out.println(">>>>>>> Error: " + error.getMessage());
//
//            }
//            throw new IllegalStateException("Failed to parse knowledge!");
//        }
//
//        KnowledgeBaseConfiguration kbaseConf = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBaseConfiguration();
//        kbaseConf.setOption(EventProcessingOption.STREAM);
//        KnowledgeBase kbase = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(kbaseConf);
//
//        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
//
//        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
//
//        remoteN1.set("AdHocProcedureSession" + this.callId, session);
//
//        return session;

        System.out.println("Starting Local Session because Remote takes to long!!!");
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ClassPathResource("processes/procedures/AdHocProcedure.bpmn"), ResourceType.BPMN2);

        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors != null && errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>>>>>> Error: " + error.getMessage());

            }
            throw new IllegalStateException("Failed to parse knowledge!");
        }
        KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseConf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        return ksession;

    }

    @Override
    public void configure(String emergencyId, Procedure procedure, Map<String, Object> parameters) {
        this.procedureId = procedure.getId();
        this.emergencyId = emergencyId;
        try {
            internalSession = createAdHocProcedureSession(this.emergencyId);
        } catch (IOException ex) {
            Logger.getLogger(AdHocProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        setWorkItemHandlers(internalSession);

        new Thread(new Runnable() {

            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();
        ProcessInstance pi = internalSession.startProcess("com.wordpress.salaboy.bpmn2.AdHocProcedure", parameters);

        procedure.setProcessInstanceId(pi.getId());
    }

    private void setWorkItemHandlers(StatefulKnowledgeSession session) {
        session.getWorkItemManager().registerWorkItemHandler("Start Procedure", new AsyncStartProcedureWorkItemHandler());
    }

    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
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
