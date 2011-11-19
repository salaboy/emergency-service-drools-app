//package com.wordpress.salaboy.service;
//
//
//
//import org.drools.KnowledgeBase;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.drools.runtime.process.ProcessInstance;
//import org.drools.runtime.process.WorkflowProcessInstance;
//import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
//import org.jbpm.test.JbpmJUnitRunner;
//import org.jbpm.test.LifeCyclePhase;
//import org.jbpm.test.Logger;
//import org.jbpm.test.TestTaskClient;
//import org.jbpm.test.annotation.HumanTaskSupport;
//import org.jbpm.test.annotation.KnowledgeSession;
//import org.jbpm.test.annotation.LifeCycle;
//import org.jbpm.test.annotation.WorkItemHandler;
//import static org.jbpm.test.matcher.CurrentActivitiesCountMatcher.currentActivitiesCount;
//import static org.jbpm.test.matcher.IsInActivityMatcher.isInActivity;
//import static org.jbpm.test.matcher.ProcessStateMatcher.isInState;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertThat;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//
//@RunWith(JbpmJUnitRunner.class)
//@org.jbpm.test.annotation.KnowledgeBase(source={"patientCheckIn.bpmn","hospital.drl"})
//@KnowledgeSession(handlers={@WorkItemHandler(taskName="Human Task", handler=WSHumanTaskHandler.class)}, logger=Logger.CONSOLE)
//@HumanTaskSupport(persistenceUnit="org.jbpm.task", users={"Administrator", "hospital-resource-planner", "nurse"})
//public class PatientCheckInTest {
//
//	protected KnowledgeBase kBase;
//	protected StatefulKnowledgeSession session;
//	protected TestTaskClient taskClient;
//	
//	@Test
//	@LifeCycle(phases={LifeCyclePhase.START, LifeCyclePhase.COMPLETE})
//	public void testPatientCheckInProcess() {
//		WorkflowProcessInstance pi = (WorkflowProcessInstance)session.startProcess("NewPatientCheckIn");
//		
//		assertThat(1, currentActivitiesCount(pi));
//		assertThat("Assign Bed",isInActivity(pi));
//		
//		int fired = session.fireAllRules();
//                assertEquals(1, fired);
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat("Prepare Staff to receive patient",isInActivity(pi));
//		taskClient.performLifeCycle("hospital-resource-planner", null, "en-UK", null);
//		
//		
//                assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat("Check In Patient",isInActivity(pi));
//		taskClient.performLifeCycle("nurse", null, "en-UK", null);
//		
//                assertThat(ProcessInstance.STATE_COMPLETED, isInState(pi));
//		
//                
//	}
//}