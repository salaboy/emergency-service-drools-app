/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import com.wordpress.salaboy.call.CallManager;
import com.wordpress.salaboy.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.events.WiiMoteEvent;
import com.wordpress.salaboy.graphicable.GraphicableFactory;
import com.wordpress.salaboy.log.Logger;
import com.wordpress.salaboy.ui.MapEventsNotifier;
import com.wordpress.salaboy.workitemhandlers.MyReportingWorkItemHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.plugtree.training.model.Ambulance;
import org.plugtree.training.model.Call;
import org.plugtree.training.model.Emergency;
import org.plugtree.training.model.events.PatientAtTheHospitalEvent;
import org.plugtree.training.model.events.PatientPickUpEvent;

/**
 *
 * @author salaboy
 */
public class EmergencyService {
    public static final Logger logger = new Logger();
    private static final EmergencyService emergency = new EmergencyService();
    private static StatefulKnowledgeSession ksession;
    private final MapEventsNotifier mapEventsNotifier;
    private Map<Long, Boolean> emergencyReachedNotified = new ConcurrentHashMap<Long, Boolean>();
    private Map<Long, Boolean> hospitalReachedNotified = new ConcurrentHashMap<Long, Boolean>();
    
    

    public EmergencyService() {
        this.mapEventsNotifier = new MapEventsNotifier(EmergencyService.logger);
        ksession = MyDroolsUtilities.createSession();
        registerHandlers();
        setGlobals();

        new Thread(new Runnable()       {

            public void run() {
                ksession.fireUntilHalt();
            }
        }).start();
    }

    public static EmergencyService getInstance() {
        return emergency;
    }

    public void signalEvent(String type, Object event) {
        ksession.signalEvent(type, event);
    }

    public QueryResults getQueryResults(String name, Object... args) {
        QueryResults queryResults = ksession.getQueryResults(name, args);
        return queryResults;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return ksession.getWorkingMemoryEntryPoint(name);
    }

    private void registerHandlers() {
        //@TODO: create a list 
        //KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new CommandBasedWSHumanTaskHandler(ksession));
        ksession.getWorkItemManager().registerWorkItemHandler("Reporting", new MyReportingWorkItemHandler());
    }

    private void setGlobals() {
        //@TODO: create a list

        ksession.setGlobal("callManager", CallManager.getInstance());

        ksession.setGlobal("ambulances", CityEntitiesUtils.ambulances);

        ksession.setGlobal("doctors", CityEntitiesUtils.doctors);

        ksession.setGlobal("hospitals", CityEntitiesUtils.hospitals);

        

    }

    public GraphicableEmergency newCall(Call call) {
        //@TODO: do a notifier, transform call into an Event inside a workingmemory entry point
        //ksession.insert(call);
        
        
        Emergency emergency = new Emergency();
        emergency.setCall(call);
        GraphicableEmergency newEmergency = GraphicableFactory.newEmergency(emergency);
        //addEmergencyAlert(xs[call.getX()] * 16, ys[call.getY()] * 16);
        //newEmergency.getEmergency().setCall(call);
        ksession.insert(newEmergency.getEmergency());
        return newEmergency;
        
    }

    public void sendPatientPickUpEvent(PatientPickUpEvent patientPickUpEvent, Long id) {
        ksession.signalEvent("com.wordpress.salaboy.PickUpPatientEvent", patientPickUpEvent );
    }
    
    public void sendPatientAtTheHospitalEvent(PatientAtTheHospitalEvent patientAtTheHospitalEvent, Long id){
        ksession.signalEvent("org.plugtree.training.model.events.PatientAtTheHospitalEvent", patientAtTheHospitalEvent);
        System.out.println("Patient at the HOspital! Event Sent!");
    }

    public Ambulance getAmbulance(Long processId) {
        WorkflowProcessInstance pI = (WorkflowProcessInstance) ksession.getProcessInstance(processId);
        Long ambulanceId = (Long)pI.getVariable("ambulance.id");
        Ambulance ambulance = CityEntitiesUtils.getAmbulanceById(ambulanceId);
        return ambulance;
    }
 
    public MapEventsNotifier getMapEventsNotifier() {
        return mapEventsNotifier;
    }

    public synchronized Map<Long, Boolean> getEmergencyReachedNotified() {
        return emergencyReachedNotified;
    }

    public Map<Long, Boolean> getHospitalReachedNotified() {
        return hospitalReachedNotified;
    }

    public void heartBeatReceivedFromAmbulance(WiiMoteEvent evt) {
        getWorkingMemoryEntryPoint("patientHeartbeats").insert(evt);
    }
    
    public void updateAmbualancePosition(Ambulance ambulance) {
        FactHandle handle = ksession.getFactHandle(ambulance);
        ksession.update(handle, ambulance);
    }

}
