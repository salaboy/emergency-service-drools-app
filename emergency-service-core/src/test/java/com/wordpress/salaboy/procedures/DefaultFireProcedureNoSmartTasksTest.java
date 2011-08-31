/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.procedures;

import com.wordpress.salaboy.grid.GridBaseTest;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class DefaultFireProcedureNoSmartTasksTest extends GridBaseTest {

    @Test
    public void testMe() {
    }
//    private  Emergency emergency = null;
//    private FireTruck fireTruck = null;
//    private MessageConsumer consumer;
//    private TaskClient client;
//    
//    private Call call = null;
//   
//    
//
//    public DefaultFireProcedureNoSmartTasksTest() {
//    }
//
//  
//     @BeforeClass
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
//    
//   @Before
//    public void setUp() throws Exception {
//        emergency = new Emergency();
//        String emergencyId = ContextTrackingServiceImpl.getInstance().newEmergency();
//        emergency.setId(emergencyId);
//        call = new Call(1, 2, new Date());
//        String callId = ContextTrackingServiceImpl.getInstance().newCall();
//        call.setId(callId);
//        emergency.setCall(call);
//        emergency.setLocation(new Location(1, 2));
//        emergency.setType(Emergency.EmergencyType.HEART_ATTACK);
//        emergency.setNroOfPeople(1);
//
//        ContextTrackingServiceImpl.getInstance().attachEmergency(callId, emergencyId);
//
//        DistributedPeristenceServerService.getInstance().storeHospital(new Hospital("My Hospital", 12, 1));
//        DistributedPeristenceServerService.getInstance().storeEmergency(emergency);
//        DistributedPeristenceServerService.getInstance().storeVehicle(new Ambulance("My Ambulance Test"));
//
//        MessageServerSingleton.getInstance().start();
//
//
//        this.coreServicesMap = new HashMap();
//        createRemoteNode();
//
//        client = HumanTaskServerService.getInstance().initTaskClient();
//        this.coreServicesMap = new HashMap();
//
//
//
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//
//
//        MessageServerSingleton.getInstance().stop();
//        if (remoteN1 != null) {
//            remoteN1.dispose();
//        }
//        if (grid1 != null) {
//            grid1.get(SocketService.class).close();
//        }
//
//    }
//
//
//    @Test
//    public void defaultHeartAttackSimpleTest() throws HornetQException, InterruptedException, IOException, ClassNotFoundException, IllegalArgumentFault, IllegalStateFault, IllegalAccessFault {
//
//
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        parameters.put("call", call);
//        parameters.put("emergency", emergency);
//        parameters.put("vehicle", fireTruck);
//
//        ProceduresMGMTService.getInstance().newRequestedProcedure(emergency.getId(), "DefaultFireProcedure", parameters);
//
//        //The fire truck doesn't reach the emergency yet. No task for 
//        //the firefighter.
//        humanTaskServiceClient.setAuthorizedEntityId("firefighter");
//        List<TTaskAbstract> taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0, 0);
//        
//        Assert.assertTrue(taskAbstracts.isEmpty());
//        
//        //Now the fire truck arrives to the emergency
//        ProceduresMGMTService.getInstance().notifyProcedures(new VehicleHitsEmergencyMessage(fireTruck.getId(), emergency.getId(), new Date()));
//        
//        Thread.sleep(2000);
//        
//        //A new task for the firefighter should be there now
//        taskAbstracts = humanTaskServiceClient.getMyTaskAbstracts("", "firefighter", "", null, "", "", "", 0, 0);
//        
//        Assert.assertEquals(1,taskAbstracts.size());
//        
//        TTaskAbstract firefighterTask = taskAbstracts.get(0);
//        
//        //The firefighter completes the task
//        Map<String, Object> info = new HashMap<String, Object>();
//        info.put("emergency.priority", 1);
//        humanTaskServiceClient.start(firefighterTask.getId());
//        humanTaskServiceClient.complete(firefighterTask.getId(), info);
//        
//        Thread.sleep(5000);
//        
//        //TODO: validate that the process has finished
//        
//        
//
//    }
}