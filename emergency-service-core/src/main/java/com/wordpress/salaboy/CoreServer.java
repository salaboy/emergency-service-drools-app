/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.model.events.PulseEvent;
import com.wordpress.salaboy.messaging.MessageConsumerWorker;
import com.wordpress.salaboy.messaging.MessageConsumerWorkerHandler;
import com.wordpress.salaboy.messaging.MessageServerSingleton;
import com.wordpress.salaboy.model.CityEntities;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.EmergencyDetailsMessage;
import com.wordpress.salaboy.model.messages.EmergencyInterchangeMessage;
import com.wordpress.salaboy.model.messages.IncomingCallMessage;
import com.wordpress.salaboy.model.messages.SelectedProcedureMessage;
import com.wordpress.salaboy.model.messages.VehicleDispatchedMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsHospitalMessage;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;
import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;
import com.wordpress.salaboy.services.HumanTaskServerService;
import com.wordpress.salaboy.services.GenericEmergencyProcedureImpl;
import com.wordpress.salaboy.services.PatientMonitorService;
import com.wordpress.salaboy.services.ProceduresMGMTService;
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

/**
 * @author salaboy
 * @author esteban
 */
public class CoreServer {

    protected Map<String, GridServiceDescription> coreServicesMap = new HashMap<String, GridServiceDescription>();
    protected static Grid grid;
    protected static GridNode remoteN1;
    
    
    private Map<Long,Boolean> vehicleHitEmergency = new HashMap<Long, Boolean> ();
    private Map<Long,Boolean> vehicleHitHospital = new HashMap<Long, Boolean> ();
    
    //CurrentWorkers
    private MessageConsumerWorker reportingWorker;
    private MessageConsumerWorker heartBeatReceivedWorker;
    private MessageConsumerWorker vehicleDispatchedWorker;
    private MessageConsumerWorker vehicleHitsHospitalWorker;
    private MessageConsumerWorker vehicleHitsEmergencyWorker;
    private MessageConsumerWorker emergencyDetailsPersistenceWorker;
    private MessageConsumerWorker selectedProcedureWorker;
    private MessageConsumerWorker phoneCallsWorker;

    public static void main(String[] args) throws Exception {
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
                } catch (Exception ex) {
                    System.out.println("Something goes wrong with the shutdown! ->"+ex.getMessage());
                    Logger.getLogger(CoreServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        
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
            System.out.println("Initializing Vehicle into the Cache - >" + vehicle.toString());
            DistributedPeristenceServerService.getInstance().storeVehicle(vehicle);
        }

        for (Hospital hospital : CityEntities.hospitals) {
            System.out.println("Initializing Hospital into the Cache - >" + hospital.toString());
            DistributedPeristenceServerService.getInstance().storeHospital(hospital);
        }

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

            //Procedure Selected Worker
            //@TODO: Delete because now the generic procedure use the Start Procedure work item to do this
            selectedProcedureWorker = new MessageConsumerWorker("selectedProcedureCoreServer", new MessageConsumerWorkerHandler<SelectedProcedureMessage>() {

                @Override
                public void handleMessage(SelectedProcedureMessage selectedProcedureMessage) {
                    ProceduresMGMTService.getInstance().newRequestedProcedure(selectedProcedureMessage.getCallId(),
                            selectedProcedureMessage.getProcedureName(),
                            selectedProcedureMessage.getParameters());
                }
            });

            //Emergency Details Persistence Selected Worker
            emergencyDetailsPersistenceWorker = new MessageConsumerWorker("emergencyDetailsCoreServer", new MessageConsumerWorkerHandler<EmergencyDetailsMessage>() {

                @Override
                public void handleMessage(EmergencyDetailsMessage emergencyDetailsMessage) {
                    DistributedPeristenceServerService.getInstance().storeEmergency(emergencyDetailsMessage.getEmergency());
                }
            });
            
           //Vehicle Hits an Emergency Selected Worker
            vehicleHitsEmergencyWorker = new MessageConsumerWorker("vehicleHitsEmergencyCoreServer", new MessageConsumerWorkerHandler<VehicleHitsEmergencyMessage>() {

                @Override
                public void handleMessage(VehicleHitsEmergencyMessage vehicleHitsEmergencyMessage) {
                    vehicleHitEmergency.put(vehicleHitsEmergencyMessage.getVehicleId(), Boolean.TRUE);
                    ProceduresMGMTService.getInstance().notifyProcedures(vehicleHitsEmergencyMessage);
                }
            });


            //Vehicle Hits an Hospital Selected Worker
            vehicleHitsHospitalWorker = new MessageConsumerWorker("vehicleHitsHospitalCoreServer", new MessageConsumerWorkerHandler<VehicleHitsHospitalMessage>() {

                @Override
                public void handleMessage(VehicleHitsHospitalMessage vehicleHitsHospitalMessage) {
                    vehicleHitHospital.put(vehicleHitsHospitalMessage.getVehicleId(), Boolean.TRUE);
                    ProceduresMGMTService.getInstance().notifyProcedures(vehicleHitsHospitalMessage);
                    
                    //Call Patient Monitor Service removeVehicle(vehicleId)
                    PatientMonitorService.getInstance().removeVehicle(vehicleHitsHospitalMessage.getVehicleId());
                }
            }); 



            //Vehicle Dispatched
            vehicleDispatchedWorker = new MessageConsumerWorker("vehicleDispatchedCoreServer", new MessageConsumerWorkerHandler<VehicleDispatchedMessage>() {

                @Override
                public void handleMessage(VehicleDispatchedMessage message) {
                    vehicleHitEmergency.put(message.getVehicleId(), Boolean.FALSE);
                    vehicleHitHospital.put(message.getVehicleId(), Boolean.FALSE);
                    PatientMonitorService.getInstance().newVehicleDispatched(message.getCallId(), message.getVehicleId());
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
                        PulseEvent event = new PulseEvent((int) message.getHeartBeatValue());
                        event.setCallId(message.getCallId());
                        event.setVehicleId(message.getVehicleId());
                        PatientMonitorService.getInstance().newHeartBeatReceived(event);
                    }
                }
            });

            reportingWorker = new MessageConsumerWorker("reportingCoreServer", new MessageConsumerWorkerHandler<EmergencyInterchangeMessage>() {

                @Override
                public void handleMessage(EmergencyInterchangeMessage message) {
                    DistributedPeristenceServerService.getInstance().addEntryToReport(message.getCallId(), message.toString());
                }
            });
 
            reportingWorker.start();
            heartBeatReceivedWorker.start();
            vehicleDispatchedWorker.start();
            vehicleHitsEmergencyWorker.start();
            vehicleHitsHospitalWorker.start();
            emergencyDetailsPersistenceWorker.start();
            selectedProcedureWorker.start();
            phoneCallsWorker.start();

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
        if(phoneCallsWorker != null){
            phoneCallsWorker.stopWorker();
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
