/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class DistributedPeristenceServerService {

    private static DistributedPeristenceServerService instance;
    private DefaultCacheManager cacheManager;
    private String nodeName;

    public static DistributedPeristenceServerService getInstance() {
        if (instance == null) {
            try {
                instance = new DistributedPeristenceServerService();
            } catch (IOException ex) {
                Logger.getLogger(DistributedPeristenceServerService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }

    private DistributedPeristenceServerService() throws IOException{

        GlobalConfiguration globalConf = GlobalConfiguration.getClusteredDefault();
        Configuration cfg = new Configuration();

        //cfg.setCacheMode(Configuration.CacheMode.REPL_SYNC);
        cfg.setCacheMode(Configuration.CacheMode.DIST_SYNC);
        cfg.setNumOwners(3);
        cacheManager = new DefaultCacheManager(globalConf, cfg);
        
    }

   
    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    public void storeEmergency(Emergency emergency) {
        if (this.getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<Long, Emergency>());
        }
        Map<String, Emergency> emergencies = ((Map<String, Emergency>) this.getCache().get("emergencies"));
        emergencies.put(emergency.getId(), emergency);
        this.getCache().put("emergencies",emergencies);
    }

    public void storeVehicle(Vehicle vehicle) {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new HashMap<String, Vehicle>());
        }
        Map<String, Vehicle> vehicles = ((Map<String, Vehicle>) this.getCache().get("vehicles"));
        vehicles.put(vehicle.getId(), vehicle);
        this.getCache().put("vehicles",vehicles);

    }

    public void storePatient(Patient patient) {
        if (this.getCache().get("patients") == null) {
            getCache().put("patients", new HashMap<String, Patient>());
        }
        Map<String, Patient> patients = ((Map<String, Patient>) this.getCache().get("patients"));
        patients.put(patient.getId(), patient);
        this.getCache().put("hospitals",patients);
    }

    public Emergency loadEmergency(String id) {
        if (this.getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<String, Emergency>());
        }
        return ((Map<String, Emergency>) this.getCache().get("emergencies")).get(id);
    }

    public Vehicle loadVehicle(String id) {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new HashMap<String, Vehicle>());
        }
        return ((Map<String, Vehicle>) this.getCache().get("vehicles")).get(id);
    }

    public Patient loadPatient(String id) {
        if (this.getCache().get("patients") == null) {
            getCache().put("patients", new HashMap<String, Patient>());
        }
        return ((Map<String, Patient>) this.getCache().get("patients")).get(id);
    }

    public Collection<Vehicle> getAllVehicles() {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new HashMap<String, Vehicle>());
        }
        return new ArrayList<Vehicle>(((Map<String, Vehicle>) this.getCache().get("vehicles")).values());
    }

    public void storeHospital(Hospital hospital) {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new HashMap<String, Hospital>());
        }
        Map<String, Hospital> hospitals = ((Map<String, Hospital>) this.getCache().get("hospitals"));
        hospitals.put(hospital.getId(), hospital);
        this.getCache().put("hospitals",hospitals);
        
    }

    public Hospital loadHospital(String id) {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new HashMap<String, Hospital>());
        }
        return ((Map<String, Hospital>) this.getCache().get("hospitals")).get(id);
    }

    public Collection<Hospital> getAllHospitals() {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new HashMap<String, Hospital>());
        }
        return ((Map<String, Hospital>) this.getCache().get("hospitals")).values();
    }
    
    public void storeFirefightersDepartment(FirefightersDepartment firefightersDepartment) {
        if (this.getCache().get("firefightersDepartment") == null) {
            getCache().put("firefightersDepartment", new HashMap<Long, FirefightersDepartment>());
        }
        Map<Long, FirefightersDepartment> firefightersDepartments = ((Map<Long, FirefightersDepartment>) this.getCache().get("firefightersDepartment"));
        firefightersDepartments.put(firefightersDepartment.getId(), firefightersDepartment);
        this.getCache().put("FirefightersDepartment",firefightersDepartments);
        
    }

    public FirefightersDepartment loadFirefightersDepartment(Long id) {
        if (this.getCache().get("firefightersDepartment") == null) {
            getCache().put("firefightersDepartment", new HashMap<Long, Hospital>());
        }
        return ((Map<Long, FirefightersDepartment>) this.getCache().get("firefightersDepartment")).get(id);
    }
    
    public Collection<FirefightersDepartment> getAllFirefightersDepartments() {
        if (this.getCache().get("firefightersDepartment") == null) {
            getCache().put("firefightersDepartment", new HashMap<Long, FirefightersDepartment>());
        }
        return ((Map<Long, FirefightersDepartment>) this.getCache().get("firefightersDepartment")).values();
    }

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

    public Report getReportByCallId(String callId) {
        if (this.getCache().get("reports") == null) {
            getCache().put("reports", new HashMap<Long, Report>());
        }
        if (((Map<String, Report>) getCache().get("reports")).get(callId) == null) {
            ((Map<String, Report>) getCache().get("reports")).put(callId, new Report());
        }
        return ((Map<String, Report>) getCache().get("reports")).get(callId);
    }

    public Collection<Emergency> getAllEmergencies() {
        if (getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<Long, Emergency>());
        }
        return ((Map<String, Emergency>) getCache().get("emergencies")).values();
    }
    
    public Emergency getEmergencyById(String id) {
    	if (getCache().get("emergencies") == null) {
            getCache().put("emergencies", new HashMap<Long, Emergency>());
        }
    	return ((Map<String, Emergency>) getCache().get("emergencies")).get(id);
    }

    private Cache<String, Object> getCache() {
        return cacheManager.getCache();
    }
}
