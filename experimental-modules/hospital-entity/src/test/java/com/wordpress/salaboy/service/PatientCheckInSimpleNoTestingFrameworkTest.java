package com.wordpress.salaboy.service;





import com.wordpress.salaboy.hospital.BedRequest;
import org.drools.runtime.process.WorkflowProcessInstance;
import com.wordpress.salaboy.hospital.CheckInResults;
import com.wordpress.salaboy.hospital.Hospital;
import com.wordpress.salaboy.service.helpers.AutoHumanWorkItemHandler;
import com.wordpress.salaboy.service.helpers.NotificationSystemWorkItemHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.ResourceType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.WorkingMemory;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.event.process.*;
import org.drools.event.rule.*;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
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
        BedRequest bedRequest = new BedRequest("1", System.currentTimeMillis(), "911", 45, "John Doe", "M", "heart attack");
        input.put("bedRequest", bedRequest);
        CheckInResults checkInResults = new CheckInResults(UUID.randomUUID().toString());
        input.put("checkInResults", new CheckInResults("1"));
        
        
        AutoHumanWorkItemHandler autoHumanWorkItemHandler = new AutoHumanWorkItemHandler();
        NotificationSystemWorkItemHandler notificationHandler = new NotificationSystemWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", autoHumanWorkItemHandler);
        session.getWorkItemManager().registerWorkItemHandler("Notification System", notificationHandler);
        
        
        WorkflowProcessInstance pI = (WorkflowProcessInstance)session.startProcess("NewPatientCheckIn", input);

        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        //int fired = session.fireAllRules();
        
        //assertEquals(2, fired);
        assertEquals(autoHumanWorkItemHandler.getInput().get("patientAge"), "45");
        
        
        HashMap<String, Object> result = new HashMap<String, Object>();
        CheckInResults firstResult = new CheckInResults("2");
        firstResult.setGate("second-floor-gate");
        result.put("outcome", firstResult);
        session.getWorkItemManager().completeWorkItem(autoHumanWorkItemHandler.getWorkItemId(), result);
        
        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        
        session.getWorkItemManager().completeWorkItem(notificationHandler.getWorkItemId(), null);
        
        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        result = new HashMap<String, Object>();
	CheckInResults lastResult = (CheckInResults) pI.getVariable("checkInResults");
        lastResult.setCheckedIn(true);
        lastResult.setCheckinTimestamp(System.currentTimeMillis());
        result.put("outcome", lastResult);
        session.getWorkItemManager().completeWorkItem(autoHumanWorkItemHandler.getWorkItemId(), result);
        
        lastResult = (CheckInResults) pI.getVariable("checkInResults");
        
        System.out.println("Check In Result ID = "+lastResult.getId());
        System.out.println("Check In Result Gate = "+lastResult.getGate());
        System.out.println("Check In Result Check In timestamp = "+lastResult.getCheckinTimestamp());
        System.out.println("Check In Is Notified = "+lastResult.isNotified());
        System.out.println("Check In Is Succesfully Checked In = "+lastResult.isCheckedIn());
        
        

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