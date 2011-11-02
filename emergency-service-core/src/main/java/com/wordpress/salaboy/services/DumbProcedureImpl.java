/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
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

/**
 *
 * @author salaboy
 */
public class DumbProcedureImpl implements DumbProcedure {
    private String id;
    private String callId;
    private StatefulKnowledgeSession internalSession;
    private String procedureName;

    public DumbProcedureImpl() {
        this.procedureName = "DumbProcedure";
    }

    private StatefulKnowledgeSession createDumbProcedureSession(String callId) throws IOException {
        System.out.println(">>>> I'm creating the "+"DumbProcedure"+" procedure for emergencyId = "+callId);
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
        KnowledgeBuilder kbuilder = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder(kbuilderConf);
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Building Resources Remotely");
        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/DumbProcedure.bpmn").getInputStream())), ResourceType.BPMN2);

      

        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Finish Building Resources Remotely");
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors != null && errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>>>>>> Error: " + error.getMessage());

            }
            throw new IllegalStateException("Failed to parse knowledge!");
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Configure Remote KBASE");
        KnowledgeBaseConfiguration kbaseConf = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBaseConfiguration();
        kbaseConf.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBase(kbaseConf);
        
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Finishing Configure Remote KBASE");
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Registering Session");
        remoteN1.set("DumbProcedureSession" + this.callId, session);

        return session;

//        System.out.println("Starting Local Session because Remote takes to long!!!");
//        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
//        kbuilder.add(new ClassPathResource("processes/procedures/DumbProcedure.bpmn"), ResourceType.BPMN2);
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
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
//
//        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
//        return ksession;

    }

    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event) {
    }
   
    @Override 
    public void configure(String callId, Procedure procedure, Map<String, Object> parameters) {
        this.callId = callId;
        try {
            internalSession = createDumbProcedureSession(this.callId);
        } catch (IOException ex) {
            Logger.getLogger(DumbProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Thread(new Runnable() {

            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^Starting DUMB PROCEDURE");
        internalSession.startProcess("com.wordpress.salaboy.bpmn2.DumbProcedure", parameters);
    }

}
