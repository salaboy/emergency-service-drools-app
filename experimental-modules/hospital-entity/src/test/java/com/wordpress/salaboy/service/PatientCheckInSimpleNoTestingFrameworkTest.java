package com.wordpress.salaboy.service;





import com.wordpress.salaboy.service.helpers.AutoHumanWorkItemHandler;
import com.wordpress.salaboy.service.helpers.NotificationSystemWorkItemHandler;
import java.util.HashMap;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.*;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class PatientCheckInSimpleNoTestingFrameworkTest {

    
    protected StatefulKnowledgeSession session;

    @Before
    public void setUp(){
        initializeSession();
    }
    
    @Test
    public void testPatientCheckInProcess() {
        HashMap<String, Object> input = new HashMap<String, Object>();
        
        input.put("bedrequest.date", "20111205");
        input.put("bedrequest.entity", "911");
        input.put("bedrequest.patientage", "45");
        input.put("bedrequest.patientname", "John Doe");
        input.put("bedrequest.patientgender", "M");
        input.put("bedrequest.patientstatus", "heart attack");
        
        
        AutoHumanWorkItemHandler autoHumanWorkItemHandler = new AutoHumanWorkItemHandler();
        NotificationSystemWorkItemHandler notificationHandler = new NotificationSystemWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", autoHumanWorkItemHandler);
        session.getWorkItemManager().registerWorkItemHandler("Notification System", notificationHandler);
        
        
        WorkflowProcessInstance pI = (WorkflowProcessInstance)session.startProcess("NewPatientCheckIn", input);

        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        
        assertEquals(autoHumanWorkItemHandler.getInput().get("bedrequest.patientage"), "45");
        
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("checkinresults.gate", "second-floor-gate");
        session.getWorkItemManager().completeWorkItem(autoHumanWorkItemHandler.getWorkItemId(), result);
        
        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        
        session.getWorkItemManager().completeWorkItem(notificationHandler.getWorkItemId(), null);
        
        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        result = new HashMap<String, Object>();
	
       
        result.put("checkinresults.checkedin", "true");
        result.put("checkinresults.time", "1201");
        session.getWorkItemManager().completeWorkItem(autoHumanWorkItemHandler.getWorkItemId(), result);
        
        
        assertEquals("second-floor-gate", pI.getVariable("checkinresults.gate"));
        assertEquals("1201", pI.getVariable("checkinresults.time"));
        assertEquals("true", pI.getVariable("checkinresults.notified"));
        assertEquals("true", pI.getVariable("checkinresults.checkedin"));
        
        System.out.println("-> Check In Result Gate = "+pI.getVariable("checkinresults.gate"));
        System.out.println("-> Check In Result Check In timestamp = "+pI.getVariable("checkinresults.time"));
        System.out.println("-> Check In Is Notified = "+pI.getVariable("checkinresults.notified"));
        System.out.println("-> Check In Is Succesfully Checked In = "+pI.getVariable("checkinresults.checkedin"));
       
        

        assertEquals(pI.getState(), ProcessInstance.STATE_COMPLETED);
    }

    private void initializeSession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(new ClassPathResource("hospital.drl"), ResourceType.DRL);
        kbuilder.add(new ClassPathResource("patientCheckIn.bpmn"), ResourceType.BPMN2);
        if (kbuilder.hasErrors()) {
            KnowledgeBuilderErrors errors = kbuilder.getErrors();

            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>> Error:" + error.getMessage());

            }
            throw new IllegalStateException(">>> Knowledge couldn't be parsed! ");
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        session = kbase.newStatefulKnowledgeSession();
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(session);
        
        ((StatefulKnowledgeSessionImpl)session).session.addEventListener(new org.drools.event.AgendaEventListener() {

            public void activationCreated(org.drools.event.ActivationCreatedEvent event, WorkingMemory workingMemory) {
                
            }

            public void activationCancelled(org.drools.event.ActivationCancelledEvent event, WorkingMemory workingMemory) {
                
            }

            public void beforeActivationFired(org.drools.event.BeforeActivationFiredEvent event, WorkingMemory workingMemory) {
                
            }

            public void afterActivationFired(org.drools.event.AfterActivationFiredEvent event, WorkingMemory workingMemory) {
                
            }

            public void agendaGroupPopped(org.drools.event.AgendaGroupPoppedEvent event, WorkingMemory workingMemory) {
                
            }

            public void agendaGroupPushed(org.drools.event.AgendaGroupPushedEvent event, WorkingMemory workingMemory) {
                
            }

            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
                
            }

            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
                workingMemory.fireAllRules();
            }

            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
                
            }

            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
                
            }
        });
        
        
    }
}