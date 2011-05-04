package com.wordpress.salaboy;

/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/11/11
 * Time: 3:09 PM
 * To change this template use File | Settings | File Templates.
 */
import org.junit.Test;
public class ProcessDataHandlingTest {
	
	@Test
	public void noTestHere(){

	}
//    TaskClient client;
//    private static PoolingDataSource ds1;
//    private HumanTaskService humanTaskServiceClient;
//
//    
//    @Before
//    public void setUp() {
//        MyDroolsUtilities.initTaskServer();
//        client = MyDroolsUtilities.initTaskClient();
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        client.disconnect();
//        MyDroolsUtilities.stopTaskServer();
//    }
//    
//    @Test
//    public void dummyTest(){
//        
//    }
//    @Test
//    public void basicProcessEntityHandlingTest() throws InterruptedException {
//
//        Call call = new Call(2, 4, new Date());
//        EmergencyService.getInstance().newEmergency(call);
//
//        Thread.sleep(5000);
//
//
//        BlockingTaskSummaryResponseHandler handler = new BlockingTaskSummaryResponseHandler();
//
//        client.getTasksAssignedAsPotentialOwner("operator", "en-UK", (TaskClientHandler.TaskSummaryResponseHandler) handler);
//        List<TaskSummary> taskSums = handler.getResults();
//        TaskSummary taskSum = taskSums.get(0);
//
//        Assert.assertNotNull(taskSum);
//
//    }
//
//    
//    @Test
//    public void newBasicProcessEntityHandlingTest(){
//
//
//
//        NewEmergencyService.getInstance().incomingCallArriving(new IncomingCallEvent(new Call(2, 4, new Date())));
//
//        //Operator Tasks
//        try {
//            List<TTaskAbstract> operatorTasks = humanTaskServiceClient.getMyTaskAbstracts("", "operator", "", null, "", "", "", 0, 0);
//        } catch (IllegalArgumentFault illegalArgumentFault) {
//            illegalArgumentFault.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IllegalStateFault illegalStateFault) {
//            illegalStateFault.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//       // Emergency emergency = NewEmergencyService.getInstance().newEmergency();
//
//
//    }
//
//    private void initTaskClient() {
//        HumanTaskServiceConfiguration taskClientConf = new HumanTaskServiceConfiguration();
//        taskClientConf.addHumanTaskClientConfiguration("jBPM5-HT-Client",new JBPM5MinaHumanTaskClientConfiguration("127.0.0.1", 9123));
//        humanTaskServiceClient = HumanTaskServiceFactory.newHumanTaskService(taskClientConf);
//        humanTaskServiceClient.initializeService();
//    }

}
