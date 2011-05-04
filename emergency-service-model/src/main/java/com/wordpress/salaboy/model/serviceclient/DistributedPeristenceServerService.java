/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Patient;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.reporting.Report;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 *
 * @author salaboy
 */
public class DistributedPeristenceServerService {

    private static DistributedPeristenceServerService instance;
    private DefaultCacheManager cacheManager;

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

    private DistributedPeristenceServerService() throws IOException {
        //1.
        //cacheManager = new DefaultCacheManager("config.xml");
        
        //2.
        //cacheManager = new DefaultCacheManager(GlobalConfiguration.getClusteredDefault() );
        
        //3.
        GlobalConfiguration globalConf = GlobalConfiguration.getClusteredDefault();
        Configuration cfg = new Configuration();
        cfg.setCacheMode(Configuration.CacheMode.DIST_SYNC);
        cacheManager = new DefaultCacheManager(globalConf, cfg);

    }

    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    

    public void storeEmergency(Emergency emergency) {
        if (this.getCache().get("emergencies") == null) {
            getCache().put("emergencies", new ConcurrentHashMap<Long, Emergency>());
        }
        ((Map<Long, Emergency>) this.getCache().get("emergencies")).put(emergency.getId(), emergency);
    }

    public void storeVehicle(Vehicle vehicle) {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new ConcurrentHashMap<Long, Vehicle>());
        }
        ((Map<Long, Vehicle>) this.getCache().get("vehicles")).put(vehicle.getId(), vehicle);

    }

    public void storePatient(Patient patient) {
        if (this.getCache().get("patients") == null) {
            getCache().put("patients", new ConcurrentHashMap<Long, Patient>());
        }
        ((Map<Long, Patient>) this.getCache().get("patients")).put(patient.getId(), patient);
    }

  

    public Emergency loadEmergency(Long id) {
        if (this.getCache().get("emergencies") == null) {
            getCache().put("emergencies", new ConcurrentHashMap<Long, Emergency>());
        }
        return ((Map<Long, Emergency>) this.getCache().get("emergencies")).get(id);
    }

    public Vehicle loadVehicle(Long id) {
        if (this.getCache().get("vehicles") == null) {
            getCache().put("vehicles", new ConcurrentHashMap<Long, Vehicle>());
        }
        return ((Map<Long, Vehicle>) this.getCache().get("vehicles")).get(id);
    }

    public Patient loadPatient(Long id) {
        if (this.getCache().get("patients") == null) {
            getCache().put("patients", new ConcurrentHashMap<Long, Patient>());
        }
        return ((Map<Long, Patient>) this.getCache().get("patients")).get(id);
    }

    public Collection<Vehicle> getAllVehicles() {
        return ((Map<Long, Vehicle>) this.getCache().get("vehicles")).values();
    }

    public void storeHospital(Hospital hospital) {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new ConcurrentHashMap<Long, Hospital>());
        }
        ((Map<Long, Hospital>) this.getCache().get("hospitals")).put(hospital.getId(), hospital);
    }

    public Hospital loadHospital(Long id) {
        if (this.getCache().get("hospitals") == null) {
            getCache().put("hospitals", new ConcurrentHashMap<Long, Hospital>());
        }
        return ((Map<Long, Hospital>) this.getCache().get("hospitals")).get(id);
    }

    public Collection<Hospital> getAllHospitals() {
        return ((Map<Long, Hospital>) this.getCache().get("hospitals")).values();
    }
    
    public void addEntryToReport(Long callId, String entry){
        if (this.getCache().get("reports") == null) {
            getCache().put("reports", new ConcurrentHashMap<Long, Report>());
        }
        if(((Map<Long, Report>)getCache().get("reports")).get(callId) == null){
            ((Map<Long, Report>)getCache().get("reports")).put(callId, new Report());
        }
        ((Map<Long, Report>)getCache().get("reports")).get(callId).addEntry(entry);
        
    }
    
    public Report getReportByCallId(Long callId){
        if (this.getCache().get("reports") == null) {
            getCache().put("reports", new ConcurrentHashMap<Long, Report>());
        }
        if(((Map<Long, Report>)getCache().get("reports")).get(callId) == null){
            ((Map<Long, Report>)getCache().get("reports")).put(callId, new Report());
        }
        return ((Map<Long, Report>)getCache().get("reports")).get(callId);
    }
    
    public Collection<Emergency> getAllEmergencies(){
        if(getCache().get("emergencies") == null){
            getCache().put("emergencies", new ConcurrentHashMap<Long, Emergency>());
        }
        return ((Map<Long, Emergency>)getCache().get("emergencies")).values();
    }
    private Cache<String, Object> getCache(){
        return cacheManager.getCache();
    }
}
