/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.call.CallManager;
import org.drools.io.impl.ClassPathResource;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.process.workitem.wsht.CommandBasedWSHumanTaskHandler;

/**
 *
 * @author salaboy
 */
public class MyDroolsService {

    public static StatefulKnowledgeSession createSession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(new ClassPathResource("processes/EmergencyService.bpmn"), ResourceType.BPMN2);
        kbuilder.add(new ClassPathResource("rules/select_ambulance.drl"), ResourceType.DRL);
        kbuilder.add(new ClassPathResource("rules/select_hospital.drl"), ResourceType.DRL);
        kbuilder.add(new ClassPathResource("rules/patient.drl"), ResourceType.DRL);
        kbuilder.add(new ClassPathResource("rules/callsHandling.drl"), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                System.out.println(error);
            }
            throw new IllegalStateException("Error building kbase!");
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        //patientHeartbeatsEntryPoint = ksession.getWorkingMemoryEntryPoint("patientHeartbeats");
        return ksession;
    } 
    public static void setGlobals(StatefulKnowledgeSession ksession){
        ksession.setGlobal("callManager", CallManager.getInstance());
    
    }   

    public static void registerHandlers(StatefulKnowledgeSession ksession) {
        //new WorkingMemoryDbLogger(ksession);
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(ksession));
    }
}
