/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.services;

import com.wordpress.salaboy.events.PulseEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactoryService;
import org.drools.builder.ResourceType;
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
 * keeps a session per vehicle
 * @author esteban
 */
public class PatientMonitorService {
    private static PatientMonitorService instance;
    private Map<Long,StatefulKnowledgeSession> sessions = new HashMap<Long, StatefulKnowledgeSession>();
    
    
    private PatientMonitorService() {
    }
    
    
    public static PatientMonitorService getInstance(){
        if(instance == null){
            instance = new PatientMonitorService();
        }
        return instance;
    }

    public void newVehicleDispatched(final Long callId,Long vehicleId) {
        try {
            final StatefulKnowledgeSession newSession = createPatientMonitorSession(callId);
            sessions.put(vehicleId, newSession);
            newSession.setGlobal("vehicleId", vehicleId);
            newSession.setGlobal("callId", callId);
            
            new Thread(new Runnable() {

                public void run() {
                    sessions.get(callId).fireUntilHalt();
                }
            }).start();
        } catch (IOException ex) {
            Logger.getLogger(ProceduresMGMTService.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void removeVehicle(Long vehicleId){
        if (!sessions.containsKey(vehicleId)){
            throw new IllegalArgumentException("Unknown vehicle "+vehicleId+". Did you dispatched it?");
        }
        sessions.get(vehicleId).dispose();
        sessions.remove(vehicleId);
    }
    
    public void newHeartBeatReceived(PulseEvent event){
        if (!sessions.containsKey(event.getVehicleId())){
            throw new IllegalArgumentException("Unknown vehicle "+event.getVehicleId()+". Did you dispatched it?");
        }
        
        sessions.get(event.getVehicleId()).getWorkingMemoryEntryPoint("patientHeartbeats").insert(event);
    }
    
    private StatefulKnowledgeSession createPatientMonitorSession(Long vehicleId) throws IOException {
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


        KnowledgeBuilder kbuilder = remoteN1.get(KnowledgeBuilderFactoryService.class).newKnowledgeBuilder();

        //kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/patient.drl").getInputStream())), ResourceType.DRL);
        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/patient.dsl").getInputStream())), ResourceType.DSL);
        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/patient.dslr").getInputStream())), ResourceType.DSLR);

        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors != null && errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>>>>>> Error: " + error.getMessage());

            }
            throw new IllegalStateException("Failed to parse knowledge!");
        }

        KnowledgeBase kbase = remoteN1.get(KnowledgeBaseFactoryService.class).newKnowledgeBase();

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        remoteN1.set("PatientMonitorSession" + vehicleId, session);

        return session;

    }

    
}
