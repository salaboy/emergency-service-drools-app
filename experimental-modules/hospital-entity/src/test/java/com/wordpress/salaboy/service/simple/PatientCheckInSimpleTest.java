//package com.wordpress.salaboy.service.simple;
//
//import static org.jbpm.test.matcher.CurrentActivitiesCountMatcher.currentActivitiesCount;
//import static org.jbpm.test.matcher.IsInActivityMatcher.isInActivity;
//import static org.jbpm.test.matcher.ProcessStateMatcher.isInState;
//import static org.jbpm.test.matcher.VariableValueMatcher.variableValue;
//import static org.junit.Assert.assertThat;
//
//import java.util.HashMap;
//
//import org.drools.KnowledgeBase;
//import org.drools.WorkingMemory;
//import org.drools.event.RuleFlowGroupActivatedEvent;
//import org.drools.event.RuleFlowGroupDeactivatedEvent;
//import org.drools.impl.StatefulKnowledgeSessionImpl;
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
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import com.wordpress.salaboy.service.helpers.NotificationSystemWorkItemHandler;
//
//@RunWith(JbpmJUnitRunner.class)
//@org.jbpm.test.annotation.KnowledgeBase(source = {"simple/patientCheckIn.bpmn", "simple/hospital.drl"})
//@KnowledgeSession(handlers = {
//    @WorkItemHandler(taskName = "Human Task", handler = WSHumanTaskHandler.class),
//    @WorkItemHandler(taskName = "Notification System", handler = NotificationSystemWorkItemHandler.class)}, logger = Logger.CONSOLE)
////@HumanTaskSupport(persistenceUnit = "org.jbpm.task", users = {"Administrator", "hospital", "nurse"}, type = TaskServerType.MINA_ASYNC)
//@HumanTaskSupport(persistenceUnit = "org.jbpm.task", users = {"Administrator", "hospital", "nurse"}, type = TaskServerType.LOCAL)
//public class PatientCheckInSimpleTest {
//
//    protected KnowledgeBase kBase;
//    protected StatefulKnowledgeSession session;
//    protected TestTaskClient taskClient;
//
//    @Before
//    public void setup() {
//
//
//        ((StatefulKnowledgeSessionImpl) session).session.addEventListener(new org.drools.event.AgendaEventListener() {
//
//            public void activationCreated(org.drools.event.ActivationCreatedEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void activationCancelled(org.drools.event.ActivationCancelledEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void beforeActivationFired(org.drools.event.BeforeActivationFiredEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void afterActivationFired(org.drools.event.AfterActivationFiredEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void agendaGroupPopped(org.drools.event.AgendaGroupPoppedEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void agendaGroupPushed(org.drools.event.AgendaGroupPushedEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
//                workingMemory.fireAllRules();
//            }
//
//            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
//            }
//
//            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
//            }
//        });
//    }
//
//    @Test
//    @LifeCycle(phases = {LifeCyclePhase.START, LifeCyclePhase.COMPLETE})
//    public void testPatientCheckInProcess() {
//
//        HashMap<String, Object> input = new HashMap<String, Object>();
//        input.put("bedrequest_date", "20111205");
//        input.put("bedrequest_entity", "911");
//        input.put("bedrequest_patientage", "45");
//        input.put("bedrequest_patientname", "John Doe");
//        input.put("bedrequest_patientgender", "M");
//        input.put("bedrequest_patientstatus", "heart attack");
//        WorkflowProcessInstance pi = (WorkflowProcessInstance) session.createProcessInstance("NewPatientCheckIn", input);
//        
//        session.insert(pi);
//        
//        session.startProcessInstance(pi.getId());
//        
//        assertThat(1, currentActivitiesCount(pi));
//        assertThat("45", variableValue(pi, "#{bedrequest_patientage}"));
//        assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//
//
//        assertThat("Coordinate_Staff", isInActivity(pi));
//
//        HashMap<String, Object> result = new HashMap<String, Object>();
//        result.put("checkinresults_gate", "second-floor-gate");
//        taskClient.performLifeCycle("hospital", null, "en-UK", result);
//
//        assertThat("second-floor-gate", variableValue(pi, "#{checkinresults_gate}"));
//        assertThat("true", variableValue(pi, "#{checkinresults_notified}"));
//
//        assertThat(ProcessInstance.STATE_ACTIVE, isInState(pi));
//        assertThat("Check_In_Patient", isInActivity(pi));
//        result = new HashMap<String, Object>();
//        result.put("checkinresults_checkedin", "true");
//        result.put("checkinresults_time", "1201");
//        taskClient.performLifeCycle("nurse", null, "en-UK", result);
//
//        assertThat("second-floor-gate", variableValue(pi, "#{checkinresults_gate}"));
//        assertThat("1201", variableValue(pi, "#{checkinresults_time}"));
//        assertThat("true", variableValue(pi, "#{checkinresults_notified}"));
//        assertThat("true", variableValue(pi, "#{checkinresults_checkedin}"));
//
//        assertThat(ProcessInstance.STATE_COMPLETED, isInState(pi));
//
//
//    }
//}