/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.log.ProcessEventLogger;
import java.util.logging.Level;
import org.drools.io.impl.ClassPathResource;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.event.DebugProcessEventListener;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;

import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;

/**
 *
 * @author salaboy
 * We need to separate the task stuff in another service class
 */
public class MyDroolsUtilities {

    
    public static final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
    
    public static StatefulKnowledgeSession createSession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add(new ClassPathResource("processes/EmergencyService3.bpmn"), ResourceType.BPMN2);
        kbuilder.add(new ClassPathResource("rules/select_ambulance.drl"), ResourceType.DRL);
       // kbuilder.add(new ClassPathResource("rules/select_ambulance.dsl"), ResourceType.DSL);
        //kbuilder.add(new ClassPathResource("rules/select_ambulance.dslr"), ResourceType.DSLR);
        kbuilder.add(new ClassPathResource("rules/select_hospital.drl"), ResourceType.DRL);
        kbuilder.add(new ClassPathResource("rules/patient.drl"), ResourceType.DRL);
    //    kbuilder.add(new ClassPathResource("rules/patient.dsl"), ResourceType.DSL);
    //    kbuilder.add(new ClassPathResource("rules/patient.dslr"), ResourceType.DSLR);
        kbuilder.add(new ClassPathResource("rules/callsHandling.drl"), ResourceType.DRL);
       // kbuilder.add(new ClassPathResource("rules/callsHandling.dsl"), ResourceType.DSL);
       // kbuilder.add(new ClassPathResource("rules/callsHandling.dslr"), ResourceType.DSLR);

        if (kbuilder.hasErrors()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                System.out.println(error);
            }
            throw new IllegalStateException("Error building kbase!");
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.addEventListener(new ProcessEventLogger(EmergencyService.logger));
        ksession.addEventListener(new DebugAgendaEventListener());
        ksession.addEventListener(new DebugProcessEventListener());
        ksession.addEventListener(new DebugWorkingMemoryEventListener());
        
        return ksession;
    }

    
    
    

    

    

    public static void initTaskServer() {


        Runtime.getRuntime().addShutdownHook(new Thread()  {

            public void run() {
                System.out.println("\n");
                taskServerDaemon.stopServer();
                System.out.println("server stoped...");
            }
        });

        taskServerDaemon.startServer();
        System.out.println("server started... (ctrl-c to stop it)");
    }

    public static TaskClient initTaskClient() {
        TaskClient client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        boolean connected = client.connect("127.0.0.1", 9123);

        int retry = 0;
        while (!connected) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(MyDroolsUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = client.connect("127.0.0.1", 9123);
            if (!connected) {
                retry++;
            }
        }
        System.out.println("Client Connected after " + retry + " retries");
        return client;
    }

   

     
}