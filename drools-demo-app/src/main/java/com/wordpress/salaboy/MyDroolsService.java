/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.workitemhandlers.MyReportingWorkItemHandler;
import com.wordpress.salaboy.call.CallManager;
import java.util.logging.Level;
import java.util.logging.Logger;
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

/**
 *
 * @author salaboy
 * We need to separate the task stuff in another service class
 */

public class MyDroolsService {
    public static final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
    
    
    public static StatefulKnowledgeSession createSession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(new ClassPathResource("processes/EmergencyService3.bpmn"), ResourceType.BPMN2);
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
        ksession.getWorkItemManager().registerWorkItemHandler("Reporting", new MyReportingWorkItemHandler());
    }
    public static void initTaskServer(){
         
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
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
        while(!connected){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyDroolsService.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = client.connect("127.0.0.1", 9123);
            if(!connected){
                retry++;
            }
        }
        System.out.println("Client Connected after "+retry+" retries");
        return client;
    }
    
    public static Task getTask(TaskClient client, Long workItemId) {
            BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
            client.getTaskByWorkItemId(workItemId, getTaskResponseHandler);
            Task task = getTaskResponseHandler.getTask();
            return task;
    }
}
