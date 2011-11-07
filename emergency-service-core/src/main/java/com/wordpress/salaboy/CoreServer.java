/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.SystemEventListenerFactory;
import org.drools.grid.ConnectionFactoryService;
import org.drools.grid.Grid;
import org.drools.grid.GridConnection;
import org.drools.grid.GridNode;
import org.drools.grid.GridServiceDescription;
import org.drools.grid.SocketService;
import org.drools.grid.conf.GridPeerServiceConfiguration;
import org.drools.grid.conf.impl.GridPeerConfiguration;
import org.drools.grid.impl.GridImpl;
import org.drools.grid.impl.MultiplexSocketServerImpl;
import org.drools.grid.io.impl.MultiplexSocketServiceCongifuration;
import org.drools.grid.remote.mina.MinaAcceptorFactoryService;
import org.drools.grid.service.directory.WhitePages;
import org.drools.grid.service.directory.impl.CoreServicesLookupConfiguration;
import org.drools.grid.service.directory.impl.WhitePagesLocalConfiguration;
import org.drools.grid.timer.impl.CoreServicesSchedulerConfiguration;
import org.neo4j.kernel.AbstractGraphDatabase;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.EmbeddedServerConfigurator;
import org.neo4j.test.ImpermanentGraphDatabase;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.*;
import com.wordpress.salaboy.model.events.*;
import com.wordpress.salaboy.model.messages.*;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;
import com.wordpress.salaboy.model.persistence.PersistenceService;
import com.wordpress.salaboy.model.persistence.PersistenceServiceProvider;
import com.wordpress.salaboy.services.*;
import com.wordpress.salaboy.services.util.MessageToEventConverter;

/**
 * @author salaboy
 * @author esteban
 */
public class CoreServer {
    private static PersistenceService persistenceService;
    private static ContextTrackingService trackingService;

    protected Map<String, GridServiceDescription> coreServicesMap = new HashMap<String, GridServiceDescription>();
    protected static Grid grid;
    protected static GridNode remoteN1;
    
    
    private Map<String,Boolean> vehicleHitEmergency = new HashMap<String, Boolean> ();
    private Map<String,Boolean> vehicleHitHospital = new HashMap<String, Boolean> ();
    
    //CurrentWorkers
    private MessageConsumerWorker reportingWorker;
    private MessageConsumerWorker heartBeatReceivedWorker;
    private MessageConsumerWorker vehicleDispatchedWorker;
    private MessageConsumerWorker vehicleHitsHospitalWorker;
    private MessageConsumerWorker vehicleHitsFireDepartmentWorker;
    private MessageConsumerWorker vehicleHitsEmergencyWorker;
    private MessageConsumerWorker emergencyDetailsPersistenceWorker;
    private MessageConsumerWorker selectedProcedureWorker;
    private MessageConsumerWorker phoneCallsWorker;
    private MessageConsumerWorker asynchProcedureStartWorker;
    private MessageConsumerWorker procedureEndedWorker;
    private MessageConsumerWorker allProceduresEndedWorker;
    private MessageConsumerWorker fireTruckDecreaseWaterLevelWorker;
    private MessageConsumerWorker fireTruckOutOfWaterWorker;
    private MessageConsumerWorker fireTruckWaterRefillMonitorWorker;
    private static boolean startWrappingServer = true;
	private static final String SERVER_API_PATH_PROP = ContextTrackingProvider.SERVER_BASE_URL
			+ "/db/data/";
    private static AbstractGraphDatabase myDb;
    private static WrappingNeoServerBootstrapper srv;
    
    public static void main(String[] args) throws Exception {
		if (startWrappingServer) {
			myDb = new ImpermanentGraphDatabase();
			EmbeddedServerConfigurator config = new EmbeddedServerConfigurator(
					myDb);
			config.configuration().setProperty(
					Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);
			config.configuration().setProperty(
					Configurator.REST_API_PATH_PROPERTY_KEY,
					SERVER_API_PATH_PROP);
			srv = new WrappingNeoServerBootstrapper(myDb, config);
			srv.start();
		}
        final CoreServer coreServer = new CoreServer();
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    System.out.println("Stopping Core Server ... ");
                    MessageServerSingleton.getInstance().stop();
                    remoteN1.dispose();
                    grid.get(SocketService.class).close();
                    HumanTaskServerService.getInstance().stopTaskServer();
                    System.out.println("Core Server Stopped! ");
                    coreServer.stopWorkers();
					if (srv != null) {
						srv.stop();
					}
                } catch (Exception ex) {
                    System.out.println("Something goes wrong with the shutdown! ->"+ex.getMessage());
                    Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
//        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
//        persistenceService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);
//
//        trackingService = ContextTrackingProvider.getTrackingService((ContextTrackingProvider.ContextTrackingServiceType) conf.getParameters().get("ContextTrackingImplementation"));
        persistenceService = PersistenceServiceProvider.getPersistenceService();
        trackingService = ContextTrackingProvider.getTrackingService();
        coreServer.startServer();
        
    }
    
    public void startServer() throws Exception {

        MessageServerSingleton.getInstance().start();


        //Starting Grid View
        createRemoteNode();
        //Starting Human Task Server
        HumanTaskServerService.getInstance().initTaskServer();
        
        //Init Persistence Service and add all the city entities
        for (Vehicle vehicle : CityEntities.vehicles) {
            System.out.println("Initializing Vehicle into the Cache - >" + vehicle);
            persistenceService.storeVehicle(vehicle);
        }
        
        for (Hospital hospital : CityEntities.hospitals) {
            System.out.println("Initializing Hospital into the Cache - >" + hospital);
            persistenceService.storeHospital(hospital);
        }
        
        FirefightersDepartment firefightersDepartment = (FirefightersDepartment)CityEntities.buildings.get("Firefighters Department");
        System.out.println("Initializing Hospital into the Cache - >" + firefightersDepartment);
        persistenceService.storeFirefightersDepartment(firefightersDepartment);

        //Init First Response Service, just to have one instance ready for new phone calls
        GenericEmergencyProcedureImpl.getInstance();
        
        //Start Workers
        startQueuesWorkers();


    }

    private void startQueuesWorkers() {
        try {

            //Phone Calls Worker
            phoneCallsWorker = new MessageConsumerWorker("IncomingCallCoreServer", new MessageConsumerWorkerHandler<IncomingCallMessage>() {

                @Override
                public void handleMessage(IncomingCallMessage incomingCallMessage) {
                    GenericEmergencyProcedureImpl.getInstance().newPhoneCall(incomingCallMessage.getCall());
                }
            });
            
            //Procedure Ended Worker
            procedureEndedWorker = new MessageConsumerWorker("ProcedureEndedCoreServer", new MessageConsumerWorkerHandler<ProcedureCompletedMessage>() {

                @Override
                public void handleMessage(ProcedureCompletedMessage procedureEndsMessage) {
                    GenericEmergencyProcedureImpl.getInstance().procedureCompletedNotification(procedureEndsMessage.getEmergencyId(), procedureEndsMessage.getProcedureId());
                }
            });
            
            //All Procedures Ended Worker
            allProceduresEndedWorker = new MessageConsumerWorker("AllProceduresEndedCoreServer", new MessageConsumerWorkerHandler<AllProceduresEndedMessage>() {

                @Override
                public void handleMessage(AllProceduresEndedMessage allProceduresEndedMessage) {
                    GenericEmergencyProcedureImpl.getInstance().allProceduresEnededNotification(new AllProceduresEndedEvent(allProceduresEndedMessage.getEmergencyId(), allProceduresEndedMessage.getEndedProcedures()));
                }
            });

            //Procedure Selected Worker
            //@TODO: Delete because now the generic procedure use the Start Procedure work item to do this
            selectedProcedureWorker = new MessageConsumerWorker("selectedProcedureCoreServer", new MessageConsumerWorkerHandler<SelectedProcedureMessage>() {

                @Override
                public void handleMessage(SelectedProcedureMessage selectedProcedureMessage) {
                    try {
                        ProceduresMGMTServiceImpl.getInstance().newRequestedProcedure(selectedProcedureMessage.getEmergencyId(),
                                selectedProcedureMessage.getProcedureName(),
                                selectedProcedureMessage.getParameters());
                    } catch (IOException ex) {
                        Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            //Emergency Details Persistence Selected Worker
            emergencyDetailsPersistenceWorker = new MessageConsumerWorker("emergencyDetailsCoreServer", new MessageConsumerWorkerHandler<EmergencyDetailsMessage>() {

                @Override
                public void handleMessage(EmergencyDetailsMessage emergencyDetailsMessage) {
                    //update the emergency
                    persistenceService.storeEmergency(emergencyDetailsMessage.getEmergency());
                    //attachs it to the call
                    trackingService.attachEmergency(emergencyDetailsMessage.getEmergency().getCall().getId(), emergencyDetailsMessage.getEmergency().getId());
                }
            });
            
           //Vehicle Hits an Emergency Selected Worker
            vehicleHitsEmergencyWorker = new MessageConsumerWorker("vehicleHitsEmergencyCoreServer", new MessageConsumerWorkerHandler<VehicleHitsEmergencyMessage>() {

                @Override
                public void handleMessage(VehicleHitsEmergencyMessage vehicleHitsEmergencyMessage) {
                    EmergencyEvent event = MessageToEventConverter.convertMessageToEvent(vehicleHitsEmergencyMessage);
                    vehicleHitEmergency.put(vehicleHitsEmergencyMessage.getVehicleId(), Boolean.TRUE);
                    ProceduresMGMTServiceImpl.getInstance().notifyProcedures(event);
                }
            });


            //Vehicle Hits an Hospital Selected Worker
            vehicleHitsHospitalWorker = new MessageConsumerWorker("vehicleHitsHospitalCoreServer", new MessageConsumerWorkerHandler<VehicleHitsHospitalMessage>() {

                @Override
                public void handleMessage(VehicleHitsHospitalMessage vehicleHitsHospitalMessage) {
                    EmergencyEvent event = MessageToEventConverter.convertMessageToEvent(vehicleHitsHospitalMessage);
                    vehicleHitHospital.put(vehicleHitsHospitalMessage.getVehicleId(), Boolean.TRUE);
                    ProceduresMGMTServiceImpl.getInstance().notifyProcedures(event);
                    
                    //Notify VehicleMGMTService
                    VehiclesMGMTService.getInstance().vehicleRemoved(vehicleHitsHospitalMessage.getVehicleId());
                }
            }); 
            
            //Vehicle Hits a Fire Department Selected Worker
            vehicleHitsFireDepartmentWorker = new MessageConsumerWorker("vehicleHitsFireDepartmentWorkerCoreServer", new MessageConsumerWorkerHandler<VehicleHitsFireDepartmentMessage>() {
                @Override
                public void handleMessage(VehicleHitsFireDepartmentMessage vehicleHitsFireDepartmentMessage) {
                    EmergencyEvent event = MessageToEventConverter.convertMessageToEvent(vehicleHitsFireDepartmentMessage);
                    ProceduresMGMTServiceImpl.getInstance().notifyProcedures(event);
                    VehiclesMGMTService.getInstance().processEvent((EmergencyVehicleEvent)event);
                }
            }); 



            //Vehicle Dispatched
            vehicleDispatchedWorker = new MessageConsumerWorker("vehicleDispatchedCoreServer", new MessageConsumerWorkerHandler<VehicleDispatchedMessage>() {

                @Override
                public void handleMessage(VehicleDispatchedMessage message) {
                    vehicleHitEmergency.put(message.getVehicleId(), Boolean.FALSE);
                    vehicleHitHospital.put(message.getVehicleId(), Boolean.FALSE);
                    VehiclesMGMTService.getInstance().newVehicleDispatched(message.getEmergencyId(), message.getVehicleId());
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
            });

            //Heart Beat Received
            heartBeatReceivedWorker = new MessageConsumerWorker("heartBeatCoreServer", new MessageConsumerWorkerHandler<HeartBeatMessage>() {

                @Override
                public void handleMessage(HeartBeatMessage message) {
                    //Only notify if the vehicle already hit the emergency 
                    //but it doesnt hit the hospital
                    Boolean hitEmergency = vehicleHitEmergency.get(message.getVehicleId());
                    Boolean hitHospital = vehicleHitHospital.get(message.getVehicleId());
                    
                    if (hitEmergency == null || hitHospital == null){
                        return;
                    }
                    
                    if (hitEmergency && !hitHospital){
                        EmergencyEvent event = MessageToEventConverter.convertMessageToEvent(message);
                        VehiclesMGMTService.getInstance().processEvent((PulseEvent)event);
                    }
                }
            });
            
            //FireTruck Water Level Decreased Received
            fireTruckDecreaseWaterLevelWorker = new MessageConsumerWorker("fireTruckDecreaseWaterLevelCoreServer", new MessageConsumerWorkerHandler<FireTruckDecreaseWaterLevelMessage>() {

                @Override
                public void handleMessage(FireTruckDecreaseWaterLevelMessage message) {
                    VehiclesMGMTService.getInstance().processEvent(new FireTruckDecreaseWaterLevelEvent(message.getEmergencyId(), message.getVehicleId(), message.getTime()));
                }
            });
            //FireTruck Out Of Water Received
            fireTruckOutOfWaterWorker = new MessageConsumerWorker("fireTruckOutOfWaterCoreServer", new MessageConsumerWorkerHandler<FireTruckOutOfWaterMessage>() {

                @Override
                public void handleMessage(FireTruckOutOfWaterMessage message) {
                    EmergencyEvent event = MessageToEventConverter.convertMessageToEvent(message);
                    VehiclesMGMTService.getInstance().processEvent((EmergencyVehicleEvent)event);
                    ProceduresMGMTServiceImpl.getInstance().notifyProcedures(event);
                }
            });

            reportingWorker = new MessageConsumerWorker("reportingCoreServer", new MessageConsumerWorkerHandler<EmergencyInterchangeMessage>() {

                @Override
                public void handleMessage(EmergencyInterchangeMessage message) {
                    persistenceService.addEntryToReport(message.getEmergencyId(), message.toString());
                }
            });
            
             asynchProcedureStartWorker = new MessageConsumerWorker("asyncProcedureStartCoreServer", new MessageConsumerWorkerHandler<AsyncProcedureStartMessage>() {

                @Override
                public void handleMessage(AsyncProcedureStartMessage message) {
                      System.out.println(">>>>>>>>>>>Creating a new Procedure = "+message.getProcedureName());
                    try {
                        ProceduresMGMTServiceImpl.getInstance().newRequestedProcedure(message.getEmergencyId(), message.getProcedureName(), message.getParameters());
                    } catch (IOException ex) {
                        Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
             
             
               fireTruckWaterRefillMonitorWorker = new MessageConsumerWorker("asynchFireMonitorCoreServer", new MessageConsumerWorkerHandler<FireTruckWaterRefilledMessage>() {

                @Override
                public void handleMessage(FireTruckWaterRefilledMessage message) {
                       
                     VehiclesMGMTService.getInstance().processEvent((EmergencyVehicleEvent)MessageToEventConverter.convertMessageToEvent(message));
                   
                }
            });
            fireTruckWaterRefillMonitorWorker.start();   
            reportingWorker.start();
            heartBeatReceivedWorker.start();
            vehicleDispatchedWorker.start();
            vehicleHitsEmergencyWorker.start();
            vehicleHitsHospitalWorker.start();
            emergencyDetailsPersistenceWorker.start();
            selectedProcedureWorker.start();
            phoneCallsWorker.start();
            asynchProcedureStartWorker.start();
            procedureEndedWorker.start();
            allProceduresEndedWorker.start();
            fireTruckDecreaseWaterLevelWorker.start();
            fireTruckOutOfWaterWorker.start();
            vehicleHitsFireDepartmentWorker.start();
            
            phoneCallsWorker.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void stopWorkers() {
        if(reportingWorker != null ){
            reportingWorker.stopWorker();
        }
        if(heartBeatReceivedWorker != null){
            heartBeatReceivedWorker.stopWorker();
        }
        if(vehicleDispatchedWorker!= null){
            vehicleDispatchedWorker.stopWorker();
        }
        if(vehicleHitsEmergencyWorker != null){
            vehicleHitsEmergencyWorker.stopWorker();
        }
        if(vehicleHitsHospitalWorker != null){
            vehicleHitsHospitalWorker.stopWorker();
        }
        if(emergencyDetailsPersistenceWorker != null){
            emergencyDetailsPersistenceWorker.stopWorker();
        }
        if(selectedProcedureWorker != null){
            selectedProcedureWorker.stopWorker();
        }
        if(asynchProcedureStartWorker != null){
            asynchProcedureStartWorker.stopWorker();
        }
        if(phoneCallsWorker != null){
            phoneCallsWorker.stopWorker();
        }
        if(procedureEndedWorker != null){
            procedureEndedWorker.stopWorker();
        }
        if(allProceduresEndedWorker != null){
            allProceduresEndedWorker.stopWorker();
        }
        if(fireTruckDecreaseWaterLevelWorker != null){
            fireTruckDecreaseWaterLevelWorker.stopWorker();
        }
        if(fireTruckOutOfWaterWorker != null){
            fireTruckOutOfWaterWorker.stopWorker();
        }
        if(vehicleHitsFireDepartmentWorker != null){
            vehicleHitsFireDepartmentWorker.stopWorker();
        }
        
    }

    protected void createRemoteNode() {
        grid = new GridImpl(new HashMap<String, Object>());
        configureGrid1(grid,
                8000,
                null);


        GridNode n1 = grid.createGridNode("n1");
        grid.get(SocketService.class).addService("n1", 8000, n1);

        GridServiceDescription<GridNode> n1Gsd = grid.get(WhitePages.class).lookup("n1");
        GridConnection<GridNode> conn = grid.get(ConnectionFactoryService.class).createConnection(n1Gsd);
        remoteN1 = conn.connect();

    }

    private void configureGrid1(Grid grid,
            int port,
            WhitePages wp) {

        //Local Grid Configuration, for our client
        GridPeerConfiguration conf = new GridPeerConfiguration();

        //Configuring the Core Services White Pages
        GridPeerServiceConfiguration coreSeviceWPConf = new CoreServicesLookupConfiguration(coreServicesMap);
        conf.addConfiguration(coreSeviceWPConf);

        //Configuring the Core Services Scheduler
        GridPeerServiceConfiguration coreSeviceSchedulerConf = new CoreServicesSchedulerConfiguration();
        conf.addConfiguration(coreSeviceSchedulerConf);

        //Configuring the WhitePages 
        WhitePagesLocalConfiguration wplConf = new WhitePagesLocalConfiguration();
        wplConf.setWhitePages(wp);
        conf.addConfiguration(wplConf);

        if (port >= 0) {
            //Configuring the SocketService
            MultiplexSocketServiceCongifuration socketConf = new MultiplexSocketServiceCongifuration(new MultiplexSocketServerImpl("127.0.0.1",
                    new MinaAcceptorFactoryService(),
                    SystemEventListenerFactory.getSystemEventListener(),
                    grid));
            socketConf.addService(WhitePages.class.getName(), wplConf.getWhitePages(), port);

            conf.addConfiguration(socketConf);
        }
        conf.configure(grid);

    }
}
