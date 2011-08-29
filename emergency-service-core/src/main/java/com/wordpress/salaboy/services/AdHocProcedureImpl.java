/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;


import com.wordpress.salaboy.model.ProcedureRequest;
import com.wordpress.salaboy.services.workitemhandlers.StartProcedureWorkItemHandler;
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
public class AdHocProcedureImpl implements AdHocProcedure{

    private String id;
    private String callId;
    private StatefulKnowledgeSession internalSession;
    private String procedureName;

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
    public void configure(String callId, Map<String, Object> parameters) {
        this.callId = callId;
        try {
            internalSession = createAdHocProcedureSession(this.callId);
        } catch (IOException ex) {
            Logger.getLogger(AdHocProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        setWorkItemHandlers(internalSession);

        new Thread(new Runnable() {
            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();
        internalSession.startProcess("com.wordpress.salaboy.bpmn2.AdHocProcedure", parameters);
    }
    
    
    private void setWorkItemHandlers(StatefulKnowledgeSession session) {
        session.getWorkItemManager().registerWorkItemHandler("Start Procedure", new StartProcedureWorkItemHandler());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    
}
