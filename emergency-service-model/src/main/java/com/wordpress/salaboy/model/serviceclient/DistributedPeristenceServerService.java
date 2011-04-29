/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import com.wordpress.salaboy.model.Patient;
import com.wordpress.salaboy.model.Vehicle;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

/**
 *
 * @author salaboy
 */
public class DistributedPeristenceServerService {

    private static DistributedPeristenceServerService instance;
    private EmbeddedCacheManager cacheManager;
    private Cache<String, Object> cache;

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
        cacheManager = new DefaultCacheManager("config.xml");
        cache = cacheManager.getCache();

    }

    public EmbeddedCacheManager getCacheManager() {
        return cacheManager;
    }

    public void storeCall(Call call) {
        if (this.cache.get("calls") == null) {
            cache.put("calls", new ConcurrentHashMap<Long, Call>());
        }
        ((Map<Long, Call>) this.cache.get("calls")).put(call.getId(), call);
    }

    public void storeEmergency(Emergency emergency) {
        if (this.cache.get("emergencies") == null) {
            cache.put("emergencies", new ConcurrentHashMap<Long, Emergency>());
        }
        ((Map<Long, Emergency>) this.cache.get("emergencies")).put(emergency.getId(), emergency);
    }

    public void storeVehicle(Vehicle vehicle) {
        if (this.cache.get("vehicles") == null) {
            cache.put("vehicles", new ConcurrentHashMap<Long, Vehicle>());
        }
        ((Map<Long, Vehicle>) this.cache.get("vehicles")).put(vehicle.getId(), vehicle);

    }

    public void storePatient(Patient patient) {
        if (this.cache.get("patients") == null) {
            cache.put("patients", new ConcurrentHashMap<Long, Patient>());
        }
        ((Map<Long, Patient>) this.cache.get("patients")).put(patient.getId(), patient);
    }

    public Call loadCall(Long id) {
        if (this.cache.get("calls") == null) {
            cache.put("calls", new ConcurrentHashMap<Long, Call>());
        }
        return ((Map<Long, Call>) this.cache.get("calls")).get(id);
    }

    public Emergency loadEmergency(Long id) {
        if (this.cache.get("emergencies") == null) {
            cache.put("emergencies", new ConcurrentHashMap<Long, Emergency>());
        }
        return ((Map<Long, Emergency>) this.cache.get("emergencies")).get(id);
    }

    public Vehicle loadVehicle(Long id) {
        if (this.cache.get("vehicles") == null) {
            cache.put("vehicles", new ConcurrentHashMap<Long, Vehicle>());
        }
        return ((Map<Long, Vehicle>) this.cache.get("vehicles")).get(id);
    }

    public Patient loadPatient(Long id) {
        if (this.cache.get("patients") == null) {
            cache.put("patients", new ConcurrentHashMap<Long, Patient>());
        }
        return ((Map<Long, Patient>) this.cache.get("patients")).get(id);
    }

    public Collection<Vehicle> getAllVehicles() {
        return ((Map<Long, Vehicle>) this.cache.get("vehicles")).values();
    }

    public void storeHospital(Hospital hospital) {
        if (this.cache.get("hospitals") == null) {
            cache.put("hospitals", new ConcurrentHashMap<Long, Hospital>());
        }
        ((Map<Long, Hospital>) this.cache.get("hospitals")).put(hospital.getId(), hospital);
    }

    public Hospital loadHospital(Long id) {
        if (this.cache.get("hospitals") == null) {
            cache.put("hospitals", new ConcurrentHashMap<Long, Hospital>());
        }
        return ((Map<Long, Hospital>) this.cache.get("hospitals")).get(id);
    }

    public Collection<Hospital> getAllHospitals() {
        return ((Map<Long, Hospital>) this.cache.get("hospitals")).values();
    }
}
