/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.workitemhandlers.MyReportingWorkItemHandler;
import com.wordpress.salaboy.call.CallManager;
import com.wordpress.salaboy.log.Logger;
import com.wordpress.salaboy.log.ProcessEventLogger;
import com.wordpress.salaboy.ui.Block;
import com.wordpress.salaboy.ui.MapEventsNotifier;
import com.wordpress.salaboy.util.MedicalKitUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.process.workitem.wsht.BlockingGetTaskResponseHandler;
import org.drools.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.drools.task.Task;
import org.drools.task.service.TaskClient;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.plugtree.training.model.Ambulance;
import org.plugtree.training.model.Emergency.EmergencyType;
import org.plugtree.training.model.Hospital;
import org.plugtree.training.model.Medic;
import org.plugtree.training.model.Medic.MedicSpeciality;
import org.plugtree.training.model.MedicalKit;

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