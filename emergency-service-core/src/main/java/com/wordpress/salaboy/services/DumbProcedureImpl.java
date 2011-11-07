/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.acc.HospitalDistanceCalculator;
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

/**
 *
 * @author salaboy
 */
public class DumbProcedureImpl implements DumbProcedure {

    private String id;
    private String emergencyId;
    private StatefulKnowledgeSession internalSession;
    private String procedureName;
    private String procedureId;
    private boolean useLocalKSession = true;
    public DumbProcedureImpl() {
        this.procedureName = "DumbProcedure";
    }

    private StatefulKnowledgeSession createDumbProcedureSession(String emergencyId) throws IOException {
        System.out.println(">>>> I'm creating the " + "DumbProcedure" + " procedure for emergencyId = " + emergencyId);
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

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("processes/procedures/DumbProcedure.bpmn").getInputStream())), ResourceType.BPMN2);



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
            remoteN1.set("DefaultHeartAttackProcedureSession" + emergencyId, session);
        }else{
            KnowledgeRuntimeLoggerFactory.newConsoleLogger(session);
        }
        

        return session;


    }

    @Override
    public void procedureEndsNotification(EmergencyEndsEvent event) {
        System.out.println("DUMB PROCEDURE ENDED BY ENDS NOTIFICATIONS!!!!");
    }

    @Override
    public void configure(String emergencyId, Procedure procedure, Map<String, Object> parameters) {
        this.emergencyId = emergencyId;
        procedureId = procedure.getId();
        try {
            internalSession = createDumbProcedureSession(this.emergencyId);
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

    @Override
    public String getProcedureId() {
        if (this.procedureId == null && this.procedureId.equals("")) {
            throw new IllegalStateException("Procedure Service wasn't configured, you must configure it first!");
        }
        return procedureId;
    }
    
    public boolean isUseLocalKSession() {
        return useLocalKSession;
    }

    public void setUseLocalKSession(boolean useLocalKSession) {
        this.useLocalKSession = useLocalKSession;
    }

    @Override
    public String getEmergencyId() {
        return this.emergencyId;
    }
}
