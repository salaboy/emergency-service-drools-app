package com.wordpress.salaboy.service;




import com.wordpress.salaboy.hospital.BedRequest;
import com.wordpress.salaboy.hospital.CheckInResults;
import com.wordpress.salaboy.hospital.Hospital;
import com.wordpress.salaboy.service.helpers.AutoHumanWorkItemHandler;
import com.wordpress.salaboy.service.helpers.NotificationSystemWorkItemHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.*;
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
        Map<String, Object> params = new HashMap<String, Object>();
        BedRequest bedRequest = new BedRequest(UUID.randomUUID().toString(), System.currentTimeMillis(), "911", 28, "Salaboy!", "male", "really bad!");
        params.put("bedRequest", bedRequest);
        CheckInResults checkInResults = new CheckInResults(UUID.randomUUID().toString());
        params.put("checkInResults", checkInResults);
        
        
        AutoHumanWorkItemHandler autoHumanWorkItemHandler = new AutoHumanWorkItemHandler();
        NotificationSystemWorkItemHandler notificationHandler = new NotificationSystemWorkItemHandler();
        session.getWorkItemManager().registerWorkItemHandler("Human Task", autoHumanWorkItemHandler);
        session.getWorkItemManager().registerWorkItemHandler("Notification System", notificationHandler);
        
        
        ProcessInstance pI = session.startProcess("NewPatientCheckIn", params);

        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        int fired = session.fireAllRules();
        
        assertEquals(1, fired);
        assertEquals(autoHumanWorkItemHandler.getInput().get("patientAge"), "28");
        
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("gate", "GATE ONE");
        session.getWorkItemManager().completeWorkItem(autoHumanWorkItemHandler.getWorkItemId(), results);
        
        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        
        session.getWorkItemManager().completeWorkItem(notificationHandler.getWorkItemId(), null);
        
        assertEquals(pI.getState(), ProcessInstance.STATE_ACTIVE);
        
        session.getWorkItemManager().completeWorkItem(autoHumanWorkItemHandler.getWorkItemId(), null);
        
        

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
        
        Hospital hospital = new Hospital();
        hospital.setAvailableBeds(30);
        session.insert(hospital);
        
    }
}