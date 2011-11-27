//package com.wordpress.salaboy.service;
//
//import static org.jbpm.test.matcher.CurrentActivitiesCountMatcher.currentActivitiesCount;
//import static org.jbpm.test.matcher.IsInActivityMatcher.isInActivity;
//import static org.jbpm.test.matcher.ProcessStateMatcher.isInState;
//import static org.junit.Assert.assertThat;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.drools.KnowledgeBase;
//import org.drools.runtime.StatefulKnowledgeSession;
//import org.drools.runtime.process.ProcessInstance;
//import org.jbpm.bpmn2.handler.SendTaskHandler;
//import org.jbpm.bpmn2.handler.ServiceTaskHandler;
//import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
//import org.jbpm.test.JbpmJUnitRunner;
//import org.jbpm.test.LifeCyclePhase;
//import org.jbpm.test.Logger;
//import org.jbpm.test.TestTaskClient;
//import org.jbpm.test.annotation.HumanTaskSupport;
//import org.jbpm.test.annotation.KnowledgeSession;
//import org.jbpm.test.annotation.LifeCycle;
//import org.jbpm.test.annotation.WorkItemHandler;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(JbpmJUnitRunner.class)
//@org.jbpm.test.annotation.KnowledgeBase(source={"patientTest.bpmn2"})
//@KnowledgeSession(handlers={@WorkItemHandler(taskName="Human Task", handler=WSHumanTaskHandler.class), 
//		@WorkItemHandler(taskName="Service Task", handler=ServiceTaskHandler.class),
//		@WorkItemHandler(taskName="Send Task", handler=SendTaskHandler.class)}, logger=Logger.CONSOLE)
//@HumanTaskSupport(persistenceUnit="org.jbpm.task", users={"Administrator", "hospital-resource-planner", "doctor"})
//public class PatientTestProcessTest {
//
//	protected KnowledgeBase kBase;
//	protected StatefulKnowledgeSession session;
//	protected TestTaskClient taskClient;
//	
//	@Test
//	@LifeCycle(phases={LifeCyclePhase.START, LifeCyclePhase.COMPLETE})
//	public void testPatientTestProcessComplete() {
//		
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("timer", "10s");
//		params.put("patient", "maciej");
//		ProcessInstance pi = session.startProcess("patientTest", params);
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat(1, currentActivitiesCount(pi));
//		assertThat("Request Appointment Information",isInActivity(pi));
//		
//		taskClient.performLifeCycle("hospital-resource-planner", null, "en-UK", null);
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat(2, currentActivitiesCount(pi));
//		assertThat("Message",isInActivity(pi));
//		assertThat("Timer",isInActivity(pi));
//		
//		session.signalEvent("Message-ReplyMessage", "SomeValue", pi.getId());
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat(1, currentActivitiesCount(pi));
//		assertThat("Analyze results and write patient report",isInActivity(pi));
//		
//		taskClient.performLifeCycle("doctor", null, "en-UK", null);
//		
//		assertThat(ProcessInstance.STATE_COMPLETED, isInState(pi));
//	}
//	
//	@Test
//	@LifeCycle(phases={LifeCyclePhase.START, LifeCyclePhase.COMPLETE})
//	public void testPatientTestProcessWithTimerComplete() {
//		
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("timer", "2s");
//		params.put("patient", "maciej");
//		ProcessInstance pi = session.startProcess("patientTest", params);
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat(1, currentActivitiesCount(pi));
//		assertThat("Request Appointment Information",isInActivity(pi));
//		
//		taskClient.performLifeCycle("hospital-resource-planner", null, "en-UK", null);
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat(2, currentActivitiesCount(pi));
//		assertThat("Message",isInActivity(pi));
//		assertThat("Timer",isInActivity(pi));
//		
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//		}
//		
//		assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//		assertThat(1, currentActivitiesCount(pi));
//		assertThat("Analyze results and write patient report",isInActivity(pi));
//		
//		taskClient.performLifeCycle("doctor", null, "en-UK", null);
//		
//		assertThat(ProcessInstance.STATE_COMPLETED, isInState(pi));
//	}
//}