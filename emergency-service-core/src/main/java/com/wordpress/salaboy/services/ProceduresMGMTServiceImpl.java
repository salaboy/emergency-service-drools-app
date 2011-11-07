/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.services;

import com.wordpress.salaboy.model.events.*;
import com.wordpress.salaboy.model.messages.EmergencyInterchangeMessage;
import com.wordpress.salaboy.model.services.ProcedureRequest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
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
/**
 *
 * @author salaboy
 */
public class ProceduresMGMTServiceImpl implements ProcedureMGMTService {

    
    private StatefulKnowledgeSession internalSession;
    private String procedureName;
    
    private boolean useLocalKSession = true;
   // private boolean logInFile;
    
    
    private static ProceduresMGMTServiceImpl instance;
    //private Map<String, List<ProcedureService>> proceduresByEmergency;
    
    private ProceduresMGMTServiceImpl() {
       // proceduresByEmergency = new HashMap<String, List<ProcedureService>>();
        configure(null);
    }

    public static ProceduresMGMTServiceImpl getInstance() {
        if (instance == null) {
            instance = new ProceduresMGMTServiceImpl();
        }
        return instance;
    }
    
    public static void clear(){
        instance = null;
    }

    @Override
    public void newRequestedProcedure(final String emergencyId, String procedureName, Map<String, Object> parameters) throws IOException {
       
        internalSession.insert(new ProcedureRequest(emergencyId, procedureName, parameters));
//        if (!proceduresByEmergency.containsKey(emergencyId)){
//            proceduresByEmergency.put(emergencyId, new ArrayList<ProcedureService>());
//        }
//        
//        List<ProcedureService> procedures = proceduresByEmergency.get(emergencyId);
//        procedures.add(ProcedureServiceFactory.createProcedureService(emergencyId, procedureName, parameters));

    }
 
    /**
     * Notifies all procedures of an emergency about a particular message.
     * The emergency is taken from {@link EmergencyInterchangeMessage#getCallId()}
     * Here is where message -> event -> service mapping is created
     * @param callId
     * @param event
     * @return 
     */
    @Override
    public void notifyProcedures(EmergencyEvent event){
        
        internalSession.insert(event);
        
//        String emergencyId = event.getEmergencyId();
//        
//        if (!this.proceduresByEmergency.containsKey(emergencyId)){
//            throw new IllegalStateException("Unknown emergency "+emergencyId);
//        }
//        
//        System.out.printf("Notify procedures about %s\n",event);
//        System.out.printf("Procedures registered to emergency '%s': %s \n", emergencyId, this.proceduresByEmergency.get(emergencyId).size());
//        
//        //notify each of the processes involved in the call
//        for (ProcedureService procedureService : this.proceduresByEmergency.get(emergencyId)) {
//            
//            //Emergency Ends event has the same behaviour for all procedures
//            if (event instanceof EmergencyEndsEvent){
//                procedureService.procedureEndsNotification((EmergencyEndsEvent)event);
//                continue;
//            }
//            
//            //TODO: change all these logic to something that doesn't hurt my eyes :)
//            if (procedureService instanceof DefaultHeartAttackProcedure){
//                DefaultHeartAttackProcedure heartAttackProcedure = (DefaultHeartAttackProcedure)procedureService;
//                if (event instanceof VehicleHitsEmergencyEvent){
//                    heartAttackProcedure.patientPickUpNotification((VehicleHitsEmergencyEvent)event);
//                }else if( event instanceof VehicleHitsHospitalEvent){
//                    heartAttackProcedure.patientAtHospitalNotification((VehicleHitsHospitalEvent)event);
//                }
//            }else if (procedureService instanceof DefaultFireProcedure){
//                DefaultFireProcedure fireProcedure = (DefaultFireProcedure)procedureService;
//                if (event instanceof VehicleHitsEmergencyEvent){
//                    fireProcedure.vehicleReachesEmergencyNotification((VehicleHitsEmergencyEvent)event);
//                }else if (event instanceof FireTruckOutOfWaterEvent){
//                    fireProcedure.fireTruckOutOfWaterNotification((FireTruckOutOfWaterEvent)event);
//                }else if (event instanceof FireExtinctedEvent){
//                    fireProcedure.fireExtinctedNotification((FireExtinctedEvent)event);
//                }else if (event instanceof VehicleHitsFireDepartmentEvent){
//                    fireProcedure.vehicleHitsFireDepartmentEventNotification((VehicleHitsFireDepartmentEvent)event);
//                }
//            }
//        }
        
        
        
    }
    
    
     private StatefulKnowledgeSession createProcedureMGMTSession() throws IOException {
        System.out.println(">>>> I'm creating the Procedure MGMT Service Session (local: "+useLocalKSession+")");
        GridNode remoteN1 = null;
        
        KnowledgeBuilder kbuilder = null;
        KnowledgeBase kbase = null;
        
        if (useLocalKSession) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kbaseConf.setOption(EventProcessingOption.STREAM);
            kbase = KnowledgeBaseFactory.newKnowledgeBase(kbaseConf);
        }else{
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
        
        
        
        kbuilder.add(new ByteArrayResource(IOUtils.toByteArray(new ClassPathResource("rules/procedure_MGMT_router.drl").getInputStream())), ResourceType.DRL);
        


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
            remoteN1.set("ProcedureMGMTSession", session);
        }else{
            KnowledgeRuntimeLoggerFactory.newConsoleLogger(session);
        }
        
        return session;

    }

    private void setWorkItemHandlers(StatefulKnowledgeSession session) {
        //nothing needed yet
    }


    
    public void configure( Map<String, Object> parameters) {
        
        try {
            internalSession = createProcedureMGMTSession();
        } catch (IOException ex) {
            Logger.getLogger(DefaultHeartAttackProcedureImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        setWorkItemHandlers(internalSession);

        new Thread(new Runnable() {

            public void run() {
                internalSession.fireUntilHalt();
            }
        }).start();

    }

    public boolean isUseLocalKSession() {
        return useLocalKSession;
    }

    public void setUseLocalKSession(boolean useLocalKSession) {
        this.useLocalKSession = useLocalKSession;
    }

   
   
    
}
