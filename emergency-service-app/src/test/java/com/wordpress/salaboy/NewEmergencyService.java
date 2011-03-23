package com.wordpress.salaboy;

import com.wordpress.salaboy.call.CallManager;
import com.wordpress.salaboy.events.PulseEvent;
import com.wordpress.salaboy.log.Logger;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.events.IncomingCallEvent;
import com.wordpress.salaboy.model.events.PatientAtTheHospitalEvent;
import com.wordpress.salaboy.model.events.PatientPickUpEvent;
import com.wordpress.salaboy.workitemhandlers.MyReportingWorkItemHandler;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.jbpm.process.workitem.wsht.CommandBasedWSHumanTaskHandler;
import org.jbpm.workflow.instance.WorkflowProcessInstance;


/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/11/11
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewEmergencyService {
    public static final Logger logger = new Logger();
       private static final NewEmergencyService emergency = new NewEmergencyService();
       protected static StatefulKnowledgeSession ksession;




       private NewEmergencyService() {

           ksession = NewMyDroolsUtilities.createSession();
           registerHandlers();
           setGlobals();

           new Thread(new Runnable()       {

               public void run() {
                   ksession.fireUntilHalt();
               }
           }).start();
       }

       public static NewEmergencyService getInstance() {
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


       public void sendPatientPickUpEvent(PatientPickUpEvent patientPickUpEvent, Long id) {
           ksession.signalEvent("com.wordpress.salaboy.model.events.PickUpPatientEvent", patientPickUpEvent );
       }

       public void sendPatientAtTheHospitalEvent(PatientAtTheHospitalEvent patientAtTheHospitalEvent, Long id){
           ksession.signalEvent("com.wordpress.salaboy.model.events.PatientAtTheHospitalEvent", patientAtTheHospitalEvent);
           System.out.println("Patient at the HOspital! Event Sent!");
       }

       public Ambulance getAmbulance(Long processId) {
           WorkflowProcessInstance pI = (WorkflowProcessInstance) ksession.getProcessInstance(processId);
           Long ambulanceId = (Long)pI.getVariable("ambulance.id");
           Ambulance ambulance = CityEntitiesUtils.getAmbulanceById(ambulanceId);
           return ambulance;
       }


       public void heartBeatReceivedFromAmbulance(Long ambulanceId, PulseEvent evt) {
           //@TODO: compose the event with the Ambulance Id
           getWorkingMemoryEntryPoint("patientHeartbeats").insert(evt);
       }

       public void updateAmbualancePosition(Ambulance ambulance) {
           FactHandle handle = ksession.getFactHandle(ambulance);
           ksession.update(handle, ambulance);
       }

    public void incomingCallArriving(IncomingCallEvent incomingCallEvent) {
        getWorkingMemoryEntryPoint("incoming-calls").insert(incomingCallEvent);
    }
}
