/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.events.EmergencyVehicleEvent;
import com.wordpress.salaboy.model.events.FireTruckDecreaseWaterLevelEvent;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.IOUtils;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
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
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * @author esteban
 */
public class FireTruckMonitorService implements VehicleMonitorService{

    private StatefulKnowledgeSession session;
    private Thread sessionThread;

    public FireTruckMonitorService() {
        
    }

    @Override
    public void newVehicleDispatched(final String emergencyId, final String vehicleId) {
        try {
            Vehicle vehicle = PersistenceServiceProvider.getPersistenceService().loadVehicle(vehicleId);
            if (vehicle == null){
                throw new IllegalArgumentException("Unknown Vehicle "+vehicleId);
            }
            
            session = createFireTruckMonitorSession(vehicleId);
            session.setGlobal("emergencyId", emergencyId);
            
            session.insert(vehicle);
            
            sessionThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    session.fireUntilHalt();
                }
            });
            
            sessionThread.start();
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    @Override
    public void processEvent(EmergencyVehicleEvent event){
        if (event instanceof FireTruckDecreaseWaterLevelEvent){
            this.processFireTruckDecreaseWaterLevelEvent((FireTruckDecreaseWaterLevelEvent)event);
        }
    }

    @Override
    public void vehicleRemoved() {
        session.dispose();
        sessionThread.stop();
    }
    
    public void processFireTruckDecreaseWaterLevelEvent(FireTruckDecreaseWaterLevelEvent event){
        session.insert(event);
    }

    private StatefulKnowledgeSession createFireTruckMonitorSession(String vehicleId) throws IOException {
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

        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/fireTruck.drl").getInputStream())), ResourceType.DRL);

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

        remoteN1.set("FireTruckMonitorSession" + vehicleId, session);

        return session;

    }
}
