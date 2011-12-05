//package com.wordpress.salaboy.service;
//
//
//
//import java.util.HashMap;
//
//import org.drools.KnowledgeBase;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.drools.runtime.process.ProcessInstance;
//import org.drools.runtime.process.WorkflowProcessInstance;
//import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
//import org.jbpm.test.JbpmJUnitRunner;
//import org.jbpm.test.LifeCyclePhase;
//import org.jbpm.test.Logger;
//import org.jbpm.test.TaskServerType;
//import org.jbpm.test.TestTaskClient;
//import org.jbpm.test.annotation.HumanTaskSupport;
//import org.jbpm.test.annotation.KnowledgeSession;
//import org.jbpm.test.annotation.LifeCycle;
//import org.jbpm.test.annotation.WorkItemHandler;
//import static org.jbpm.test.matcher.CurrentActivitiesCountMatcher.currentActivitiesCount;
//import static org.jbpm.test.matcher.IsInActivityMatcher.isInActivity;
//import static org.jbpm.test.matcher.ProcessStateMatcher.isInState;
//import static org.jbpm.test.matcher.VariableValueMatcher.variableValue;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import com.wordpress.salaboy.hospital.BedRequest;
//import com.wordpress.salaboy.hospital.CheckInResults;
//import com.wordpress.salaboy.hospital.Hospital;
//import com.wordpress.salaboy.service.helpers.NotificationSystemWorkItemHandler;
//
//
//@RunWith(JbpmJUnitRunner.class)
//@org.jbpm.test.annotation.KnowledgeBase(source={"patientCheckIn.bpmn","hospital.drl"})
//@KnowledgeSession(handlers={@WorkItemHandler(taskName="Human Task", handler=WSHumanTaskHandler.class), 
//		@WorkItemHandler(taskName="Notification System", handler=NotificationSystemWorkItemHandler.class)}, logger=Logger.CONSOLE)
//@HumanTaskSupport(persistenceUnit="org.jbpm.task", users={"Administrator", "hospital-resource-planner", "nurse"}, type=TaskServerType.MINA_ASYNC)
//public class PatientCheckInSimpleTest {
//
//	protected KnowledgeBase kBase;
//	protected StatefulKnowledgeSession session;
//	protected TestTaskClient taskClient;
//	
//	@Before
//	public void setup() {
//		Hospital hospital = new Hospital();
//		hospital.setAvailableBeds(10);
//		
//		session.insert(hospital);
//	}
//	
//	@Test
//	@LifeCycle(phases={LifeCyclePhase.START, LifeCyclePhase.COMPLETE})
//	public void testPatientCheckInProcess() {
//		
//		HashMap<String, Object> input = new HashMap<String, Object>();
//		BedRequest bedRequest = new BedRequest("1", System.currentTimeMillis(), "911", 45, "John Doe", "M", "heart attack");
//		input.put("bedRequest", bedRequest);
//		input.put("checkInResults", new CheckInResults("1"));
//		WorkflowProcessInstance pi = (WorkflowProcessInstance)session.startProcess("NewPatientCheckIn", input);
//		
//		assertThat(1, currentActivitiesCount(pi));
//		assertThat("Assign Bed",isInActivity(pi));
//		
//		int fired = session.fireAllRules();
//        assertEquals(1, fired);
//        assertThat("1", variableValue(pi, "#{checkInResults.id}"));
//        
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat("Coordinate Staff",isInActivity(pi));
//		HashMap<String, Object> result = new HashMap<String, Object>();
//		CheckInResults firstResult = new CheckInResults("2");
//		firstResult.setGate("second-flor-gate");
//		result.put("outcome", firstResult);
//		taskClient.performLifeCycle("hospital-resource-planner", null, "en-UK", result);
//		
//		assertThat("second-flor-gate", variableValue(pi, "#{checkInResults.gate}"));
//		assertThat("3", variableValue(pi, "#{checkInResults.id}"));
//		assertThat("true", variableValue(pi, "#{checkInResults.notified}"));
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat("Check In Patient",isInActivity(pi));
//		result = new HashMap<String, Object>();
//		CheckInResults lastResult = (CheckInResults) pi.getVariable("checkInResults");
//		lastResult.setCheckedIn(true);
//		lastResult.setCheckinTimestamp(System.currentTimeMillis());
//		result.put("outcome", lastResult);
//		taskClient.performLifeCycle("nurse", null, "en-UK", result);
//		
//		assertThat("second-flor-gate", variableValue(pi, "#{checkInResults.gate}"));
//		assertThat("3", variableValue(pi, "#{checkInResults.id}"));
//		assertThat("true", variableValue(pi, "#{checkInResults.notified}"));
//		assertThat("true", variableValue(pi, "#{checkInResults.checkedIn}"));
//		
//        assertThat(ProcessInstance.STATE_COMPLETED, isInState(pi));
//		
//                
//	}
//}