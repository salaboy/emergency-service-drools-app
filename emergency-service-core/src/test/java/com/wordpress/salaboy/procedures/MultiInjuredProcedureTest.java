/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;




import org.junit.Test;

import com.wordpress.salaboy.grid.GridBaseTest;

/**
 *
 * @author salaboy
 */
public class MultiInjuredProcedureTest extends GridBaseTest {
        @Test
        public void testMe(){}
//    private MessageConsumer consumer;
//    private HumanTaskService humanTaskServiceClient;
//    
//
//    public MultiInjuredProcedureTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//        HumanTaskServerService.getInstance().initTaskServer();
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//
//        HumanTaskServerService.getInstance().stopTaskServer();
//    }
//
//    Emergency emergency = null;
//    Call call = null;
//    @Before
//    public void setUp() throws Exception {
//        emergency = new Emergency();
//        String emergencyId = ContextTrackingServiceImpl.getInstance().newEmergency();
//        emergency.setId(emergencyId);
//        call = new Call(1,2,new Date());
//        String callId = ContextTrackingServiceImpl.getInstance().newCall();
//        call.setId(callId);
//        emergency.setCall(call);
//        emergency.setLocation(new Location(1,2));
//        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
//        emergency.setNroOfPeople(1);
//        DistributedPeristenceServerService.getInstance().storeHospital(new Hospital("My Hospital", 12, 1));
//        DistributedPeristenceServerService.getInstance().storeEmergency(emergency);
//        DistributedPeristenceServerService.getInstance().storeVehicle(new Ambulance("My Ambulance Test"));
//        DistributedPeristenceServerService.getInstance().storeVehicle(new Ambulance("My Ambulance Test 2"));
//        MessageServerSingleton.getInstance().start();
//
//
//
//        this.coreServicesMap = new HashMap();
//        createRemoteNode();
//
//        HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();
//
//
//        taskClientConf.addHumanTaskClientConfiguration("jBPM5-HT-Client", new JBPM5HornetQHumanTaskClientConfiguration("127.0.0.1", 5446));
//
//        humanTaskServiceClient = HumanTaskServiceFactory.newHumanTaskService(taskClientConf);
//        humanTaskServiceClient.initializeService();
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        
//        MessageServerSingleton.getInstance().stop();
//        if (remoteN1 != null) {
//            remoteN1.dispose();
//        }
//        if (grid1 != null) {
//            grid1.get(SocketService.class).close();
//        }
//    }
//
//    @Test
//    public void defaultHeartAttackSimpleTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException, IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {
//
//
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        parameters.put("call", call);
//        parameters.put("emergency", emergency);
//
//
//
//        ProceduresMGMTService.getInstance().newRequestedProcedure(((Call) parameters.get("call")).getId(), "MultiInjuredPeopleProcedure", parameters);
//
//        Thread.sleep(5000);
//
//        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "garage_emergency_service", "", null, "", "", "", 0, 0);
//        assertNotNull(taskAbstracts);
//        assertEquals(1, taskAbstracts.size());
//        TTaskAbstract taskAbstract = taskAbstracts.get(0); // getting the first task
//        assertEquals(" Select Vehicle For "+call.getId()+" ", taskAbstract.getName().getLocalPart());
//        
//
//
//        // I need to get the Content Data and check the values of the Emergency and Call Ids.
//        // Using that I need to select one vehicle ID from the list of all the vehicles. 
//
//        TTask task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
//        assertNotNull(task);
//        
//        humanTaskServiceClient.setAuthorizedEntityId("garage_emergency_service");
//        humanTaskServiceClient.start(task.getId());
//
//        List<TAttachmentInfo> attachmentsInfo = humanTaskServiceClient.getAttachmentInfos(task.getId());
//        TAttachmentInfo firstAttachmentInfo = attachmentsInfo.get(0);
//        TAttachment attachment = humanTaskServiceClient.getAttachments(task.getId(), firstAttachmentInfo.getName()).get(0);
//
//        String value = (String)((Map)attachment.getValue()).get("Content");
//        
//        assertNotNull(value, "1,1"); 
//
//        
//        Map<String, Object> info = new HashMap<String, Object>();
//        List<Vehicle> vehicles = new ArrayList<Vehicle>();
//        Ambulance ambulance = new Ambulance("My Ambulance", new Date());
//        String ambulanceId = ContextTrackingServiceImpl.getInstance().newVehicle();
//        ambulance.setId(ambulanceId);
//        Ambulance ambulance2 = new Ambulance("My Ambulance2", new Date());
//        String ambulanceId2 = ContextTrackingServiceImpl.getInstance().newVehicle();
//        ambulance2.setId(ambulanceId2);
//        vehicles.add(ambulance);
//        vehicles.add(ambulance2);
//        info.put("emergency.vehicles", vehicles);
//        
//        
//        humanTaskServiceClient.complete(task.getId(), info);
//
//        Thread.sleep(4000);
//        
//        //The vehicle reaches the emergency
//        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulanceId, call.getId(), new Date()));
//        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(ambulanceId2, call.getId(), new Date()));
//
//        Thread.sleep(4000);
//        
//        humanTaskServiceClient.setAuthorizedEntityId("doctor");
//        taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "doctor", "", null, "", "", "", 0, 0);
//        assertNotNull(taskAbstracts);
//        assertEquals(2, taskAbstracts.size());
//        taskAbstract = taskAbstracts.get(0); 
//        
//        task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
//        assertNotNull(task);
//        
//        humanTaskServiceClient.start(task.getId());
//        
//        info = new HashMap<String, Object>();
//        attachmentsInfo = humanTaskServiceClient.getAttachmentInfos(task.getId());
//        firstAttachmentInfo = attachmentsInfo.get(0);
//        attachment = humanTaskServiceClient.getAttachments(task.getId(), firstAttachmentInfo.getName()).get(0);
//
//        Map taskinfo = (Map)attachment.getValue();
//
//        info.put("comment", "Comment 1");
//        info.put("priority", 2);
//        humanTaskServiceClient.complete(task.getId(), info);
//
//        Thread.sleep(4000);
//
//        //The vehicle2 reaches the hospital
//        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsHospitalMessage(ambulanceId2, new Hospital("Hospital A", 0, 0), call.getId(), new Date()));
//
//        taskAbstract = taskAbstracts.get(1);
//        task = humanTaskServiceClient.getTaskInfo(taskAbstract.getId());
//        assertNotNull(task);
//        
//        humanTaskServiceClient.start(task.getId());
//        
//        info = new HashMap<String, Object>();
//        taskinfo = (Map)attachment.getValue();
//
//        info.put("comment", "Comment 2");
//        info.put("priority", 2);
//        humanTaskServiceClient.complete(task.getId(), info);
//
//        Thread.sleep(4000);
//
//        //The vehicle1 reaches the hospital
//        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsHospitalMessage(ambulanceId, new Hospital("Hospital A", 0, 0), call.getId(), new Date()));
//        Thread.sleep(4000);
//
//        Assert.assertEquals(1, emergency.getUpdatesForVehicle(ambulanceId).size());
//        Assert.assertEquals(1, emergency.getUpdatesForVehicle(ambulanceId2).size());
//
//    }
}