/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.workitemhandlers.MyReportingWorkItemHandler;
import com.wordpress.salaboy.call.CallManager;
import com.wordpress.salaboy.ui.Block;
import com.wordpress.salaboy.ui.MapEventsNotifier;
import com.wordpress.salaboy.util.MedicalKitUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class MyDroolsService {

    public static final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();
    public static final Map<EmergencyType, List<Ambulance>> ambulances = new HashMap<EmergencyType, List<Ambulance>>() {

        {
            put(EmergencyType.FIRE, new ArrayList<Ambulance>() {

                {
                    add(initializeFireAmbulance());
                }
            });
            put(EmergencyType.HEART_ATTACK, new ArrayList<Ambulance>() {

                {
                    add(initializeHeartAttackAmbulance());
                }
            });
            put(EmergencyType.CAR_CRASH, new ArrayList<Ambulance>() {

                {
                    add(initializeCarCrashAmbulance());
                }
            });
        }
    };
    
    public static Ambulance getAmbulanceById(Long id){
        for (Map.Entry<EmergencyType, List<Ambulance>> entry : ambulances.entrySet()) {
            for (Ambulance ambulance : entry.getValue()) {
                if (ambulance.getId().equals(id)){
                    return ambulance;
                }
            }
        }
        return null;
    }
    
    public static Hospital getHospitalById(Long id){
        for (Map.Entry<String, Hospital> entry : hospitals.entrySet()) {
            if (entry.getValue().getId().equals(id)){
                return entry.getValue();
            }
        }
        return null;
    }
    
    public static final Map<MedicSpeciality, List<Medic>> doctors = new HashMap<MedicSpeciality, List<Medic>>() {

        {
            put(MedicSpeciality.BURNS, new ArrayList<Medic>() {

                {
                    add(new Medic(MedicSpeciality.BURNS));
                }
            });
            put(MedicSpeciality.BONES, new ArrayList<Medic>() {

                {
                    add(new Medic(MedicSpeciality.BONES));
                }
            });
            put(MedicSpeciality.REANIMATION, new ArrayList<Medic>() {

                {
                    add(new Medic(MedicSpeciality.REANIMATION));
                }
            });
        }
    };
    
    public static final Map<String, Hospital> hospitals = new HashMap<String, Hospital>(){{
        
        put("Hosital 01", new Hospital("Hospital 01", 11, 13));
        put("Hosital 02", new Hospital("Hospital 02", 35, 13));
        put("Hosital 03", new Hospital("Hospital 03", 17, 25));
       
    
    }};

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

        return ksession;
    }

    public static void setGlobals(StatefulKnowledgeSession ksession) {
        ksession.setGlobal("callManager", CallManager.getInstance());

        ksession.setGlobal("ambulances", ambulances);

        ksession.setGlobal("doctors", doctors);
        
        ksession.setGlobal("hospitals", hospitals);
        
        

    }
    
    public static void setNotifier(StatefulKnowledgeSession ksession, MapEventsNotifier notifier){
        ksession.setGlobal("notifier", notifier);
    }

    private static Ambulance initializeFireAmbulance() {
        MedicalKit fireKit = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.BURNS);
        Ambulance fireAmbulance = new Ambulance("Fire Ambulance");
        fireAmbulance.addKit(fireKit);
        return fireAmbulance;
    }

    private static Ambulance initializeHeartAttackAmbulance() {
        MedicalKit heartAttackKit = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.REANIMATION);
        Ambulance heartAttackAmbulance = new Ambulance("Strokes Ambulance");
        heartAttackAmbulance.addKit(heartAttackKit);
        return heartAttackAmbulance;
    }

    private static Ambulance initializeCarCrashAmbulance() {
        MedicalKit carCrashKit1 = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.BONES);
        MedicalKit carCrashKit2 = MedicalKitUtil.createNewMEdicalKit(MedicSpeciality.BURNS);
        Ambulance carCrashAmbulance = new Ambulance("Fire & Bones Ambulance");
        carCrashAmbulance.addKit(carCrashKit1);
        carCrashAmbulance.addKit(carCrashKit2);
        return carCrashAmbulance;
    }

    public static void registerHandlers(StatefulKnowledgeSession ksession) {
        //new WorkingMemoryDbLogger(ksession);
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(ksession));
        ksession.getWorkItemManager().registerWorkItemHandler("Reporting", new MyReportingWorkItemHandler());
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
                Logger.getLogger(MyDroolsService.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = client.connect("127.0.0.1", 9123);
            if (!connected) {
                retry++;
            }
        }
        System.out.println("Client Connected after " + retry + " retries");
        return client;
    }

    public static Task getTask(TaskClient client, Long workItemId) {
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTaskByWorkItemId(workItemId, getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        return task;
    }

     public static Hospital getHospitalByCoordinates(Block hospitalBlock) {
        Collection<Hospital> myhospitals = MyDroolsService.hospitals.values();
        float x = Math.round(hospitalBlock.poly.getX()/16);
        float y = Math.round(hospitalBlock.poly.getY()/16);
        
        for(Hospital thishospital : myhospitals){
            if(thishospital.getPositionX() == x && thishospital.getPositionY() == y){
                return thishospital;
            }
        }
        return null;
    }
}
