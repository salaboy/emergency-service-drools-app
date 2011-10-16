/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.context.tracking.ContextTrackingProvider.ContextTrackingServiceType;
import com.wordpress.salaboy.context.tracking.ContextTrackingService;
import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Procedure;
import com.wordpress.salaboy.model.ServiceChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FirefightersDepartment;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Patient;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.reporting.Report;

/**
 *
 * @author salaboy
 */
public class DistributedMapPeristenceService implements PersistenceService {

   
    private DefaultCacheManager cacheManager;
    private String nodeName;
    private ContextTrackingService contextTracking;



    public DistributedMapPeristenceService(PersistenceServiceConfiguration conf) throws IOException {

        
        GlobalConfiguration globalConf = GlobalConfiguration.getClusteredDefault();
        Configuration cfg = new Configuration();

        
        cfg.setCacheMode(Configuration.CacheMode.DIST_SYNC);
        cfg.setNumOwners(3);
        cacheManager = new DefaultCacheManager(globalConf, cfg);
        ContextTrackingServiceType type = (ContextTrackingServiceType)conf.getParameters().get("ContextTrackingImplementation");
        contextTracking = ContextTrackingProvider.getTrackingService(type);
        
        

    }

    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }
    // Stores 

    @Override
    public void storeEmergency(Emergency emergency) {
        if (emergency.getId() == null || emergency.getId().equals("")) {
            emergency.setId(contextTracking.newEmergencyId());
        }
        if (this.getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<String, Emergency>());
        }
        Map<String, Emergency> emergencies = ((Map<String, Emergency>) this.getCache().get("emergencies"));
        emergencies.put(emergency.getId(), emergency);
        this.getCache().put("emergencies", emergencies);
    }

    @Override
    public void storeVehicle(Vehicle vehicle) {
        if (vehicle.getId() == null || vehicle.getId().equals("")) {
            vehicle.setId(contextTracking.newVehicleId());
        }
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new HashMap<String, Vehicle>());
        }
        Map<String, Vehicle> vehicles = ((Map<String, Vehicle>) this.getCache().get("vehicles"));
        vehicles.put(vehicle.getId(), vehicle);
        this.getCache().put("vehicles", vehicles);

    }

    @Override
    public void storePatient(Patient patient) {
        if (patient.getId() == null || patient.getId().equals("")) {
            patient.setId(contextTracking.newPatientId());
        }
        if (this.getCache().get("patients") == null) {
            getCache().put("patients", new HashMap<String, Patient>());
        }
        Map<String, Patient> patients = ((Map<String, Patient>) this.getCache().get("patients"));
        patients.put(patient.getId(), patient);
        this.getCache().put("hospitals", patients);
    }

    @Override
    public void storeHospital(Hospital hospital) {
        if (hospital.getId() == null || hospital.getId().equals("")) {
            hospital.setId(contextTracking.newEmergencyEntityBuildingId());
        }
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new HashMap<String, Hospital>());
        }
        Map<String, Hospital> hospitals = ((Map<String, Hospital>) this.getCache().get("hospitals"));
        hospitals.put(hospital.getId(), hospital);
        this.getCache().put("hospitals", hospitals);

    }

    @Override
    public void storeCall(Call call) {
        if (call.getId() == null || call.getId().equals("")) {
            call.setId(contextTracking.newCallId());
        }
        if (this.getCache().get("calls") == null) {
            getCache().put("calls", new HashMap<Long, Emergency>());
        }
        Map<String, Call> calls = ((Map<String, Call>) this.getCache().get("calls"));
        calls.put(call.getId(), call);
        this.getCache().put("calls", calls);
    }

    @Override
    public void storeFirefightersDepartment(FirefightersDepartment firefightersDepartment) {
        if (firefightersDepartment.getId() == null || firefightersDepartment.getId().equals("")) {
            firefightersDepartment.setId(contextTracking.newEmergencyEntityBuildingId());
        }
        if (this.getCache().get("firefightersDepartments") == null) {
            getCache().put("firefightersDepartments", new HashMap<String, FirefightersDepartment>());
        }
        Map<String, FirefightersDepartment> firefightersDepartments = ((Map<String, FirefightersDepartment>) this.getCache().get("firefightersDepartments"));
        firefightersDepartments.put(firefightersDepartment.getId(), firefightersDepartment);
        this.getCache().put("firefightersDepartments", firefightersDepartments);

    }

    @Override
    public void storeProcedure(Procedure procedure) {
        if (procedure.getId() == null || procedure.getId().equals("")) {
            procedure.setId(contextTracking.newProcedureId());
        }
        if (this.getCache().get("procedures") == null) {
            getCache().put("procedures", new HashMap<String, Procedure>());
        }
        Map<String, Procedure> procedures = ((Map<String, Procedure>) this.getCache().get("procedures"));
        procedures.put(procedure.getId(), procedure);
        this.getCache().put("procedures", procedures);
    }

    @Override
    public void storeServiceChannel(ServiceChannel channel) {
        if (channel.getId() == null || channel.getId().equals("")) {
            channel.setId(contextTracking.newServiceChannelId());
        }
        if (this.getCache().get("channels") == null) {
            getCache().put("channels", new HashMap<String, ServiceChannel>());
        }
        Map<String, ServiceChannel> channels = ((Map<String, ServiceChannel>) this.getCache().get("channels"));
        channels.put(channel.getId(), channel);
        this.getCache().put("channels", channels);
    }

    //Loads
    @Override
    public Emergency loadEmergency(String id) {
        if (this.getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<String, Emergency>());
        }
        return ((Map<String, Emergency>) this.getCache().get("emergencies")).get(id);
    }

    @Override
    public Vehicle loadVehicle(String id) {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new HashMap<String, Vehicle>());
        }
        return ((Map<String, Vehicle>) this.getCache().get("vehicles")).get(id);
    }

    @Override
    public Patient loadPatient(String id) {
        if (this.getCache().get("patients") == null) {
            getCache().put("patients", new HashMap<String, Patient>());
        }
        return ((Map<String, Patient>) this.getCache().get("patients")).get(id);
    }

    @Override
    public Hospital loadHospital(String id) {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new HashMap<String, Hospital>());
        }
        return ((Map<String, Hospital>) this.getCache().get("hospitals")).get(id);
    }

    @Override
    public Report loadReport(String callId) {
        if (this.getCache().get("reports") == null) {
            getCache().put("reports", new HashMap<Long, Report>());
        }
        if (((Map<String, Report>) getCache().get("reports")).get(callId) == null) {
            ((Map<String, Report>) getCache().get("reports")).put(callId, new Report());
        }
        return ((Map<String, Report>) getCache().get("reports")).get(callId);
    }

    @Override
    public Call loadCall(String id) {
        if (this.getCache().get("calls") == null) {
            getCache().put("calls", new HashMap<String, Call>());
        }
        return ((Map<String, Call>) this.getCache().get("calls")).get(id);
    }

    @Override
    public FirefightersDepartment loadFirefighterDepartment(String id) {
        if (this.getCache().get("firefightersDepartments") == null) {
            getCache().put("firefightersDepartments", new HashMap<String, FirefightersDepartment>());
        }
        return ((Map<String, FirefightersDepartment>) this.getCache().get("firefightersDepartments")).get(id);
    }

    @Override
    public Procedure loadProcedure(String procedureId) {
        if (this.getCache().get("procedures") == null) {
            getCache().put("procedures", new HashMap<String, Procedure>());
        }
        return ((Map<String, Procedure>) this.getCache().get("procedures")).get(procedureId);
    }

    @Override
    public ServiceChannel loadServiceChannel(String channelId) {
        if (this.getCache().get("channels") == null) {
            getCache().put("channels", new HashMap<String, ServiceChannel>());
        }
        return ((Map<String, ServiceChannel>) this.getCache().get("channels")).get(channelId);
    }

    //GET ALLs
    @Override
    public Collection<Procedure> getAllProcedures() {
        if (this.getCache().get("procedures") == null) {
            getCache().put("procedures", new HashMap<String, Procedure>());
        }
        return new ArrayList<Procedure>(((Map<String, Procedure>) this.getCache().get("procedures")).values());
    }
    @Override
    public Collection<Call> getAllCalls() {
        if (this.getCache().get("calls") == null) {
            getCache().put("calls", new HashMap<String, Call>());
        }
        return new ArrayList<Call>(((Map<String, Call>) this.getCache().get("calls")).values());
    }
    
    @Override
    public Collection<Vehicle> getAllVehicles() {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new HashMap<String, Vehicle>());
        }
        return new ArrayList<Vehicle>(((Map<String, Vehicle>) this.getCache().get("vehicles")).values());
    }

    @Override
    public Collection<Hospital> getAllHospitals() {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new HashMap<String, Hospital>());
        }
        return ((Map<String, Hospital>) this.getCache().get("hospitals")).values();
    }

    @Override
    public Collection<FirefightersDepartment> getAllFirefighterDepartments() {
        if (this.getCache().get("firefightersDepartments") == null) {
            getCache().put("firefightersDepartments", new HashMap<Long, FirefightersDepartment>());
        }
        return ((Map<Long, FirefightersDepartment>) this.getCache().get("firefightersDepartments")).values();
    }

    @Override
    public Collection<Emergency> getAllEmergencies() {
        if (getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<Long, Emergency>());
        }
        return ((Map<String, Emergency>) getCache().get("emergencies")).values();
    }

    //Helpers 
    @Override
    public void addEntryToReport(String callId, String entry) {
        if (this.getCache().get("reports") == null) {
            getCache().put("reports", new HashMap<String, Report>());
        }
        if (((Map<String, Report>) getCache().get("reports")).get(callId) == null) {
            ((Map<String, Report>) getCache().get("reports")).put(callId, new Report());
        }
        Map<String, Report> reports = ((Map<String, Report>) this.getCache().get("reports"));
        reports.get(callId).addEntry(entry);
        this.getCache().put("reports", reports);

    }

    private Cache<String, Object> getCache() {
        return cacheManager.getCache();
    }

    @Override
    public void clear() {
        this.cacheManager.stop();
        this.contextTracking.clear();
    }

    
}
