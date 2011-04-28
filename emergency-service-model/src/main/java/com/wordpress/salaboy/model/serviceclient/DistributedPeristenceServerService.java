/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.serviceclient;

import com.wordpress.salaboy.model.Call;
import com.wordpress.salaboy.model.Emergency;
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
    private EmbeddedCacheManager manager;
    private Cache<String, Object> cache;
    private Map<Long, Call> calls;
    private Map<Long, Emergency> emergencies;
    private Map<Long, Vehicle> vehicles;
    private Map<Long, Patient> patients;
    
    public static DistributedPeristenceServerService getInstance(){
        if(instance == null){
            try {
                instance = new DistributedPeristenceServerService();
            } catch (IOException ex) {
                Logger.getLogger(DistributedPeristenceServerService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instance;
    }
    
    private DistributedPeristenceServerService() throws IOException {
        manager = new DefaultCacheManager("config.xml");
        cache = manager.getCache("myPersistence");
        if(cache.size() == 0){
            this.calls = new ConcurrentHashMap<Long, Call>();
            this.emergencies = new ConcurrentHashMap<Long, Emergency>();
            this.vehicles = new ConcurrentHashMap<Long, Vehicle>();
            this.patients = new ConcurrentHashMap<Long, Patient>();
            cache.put("calls", this.calls);
            cache.put("emergencies", this.emergencies);
            cache.put("vehicles", this.vehicles);
            cache.put("patients", this.patients);
        }
        cache.start();
    }
    
    public void storeCall(Call call){
        ((Map<Long,Call>)this.cache.get("calls")).put(call.getId(), call);
    }
    
    public void storeEmergency(Emergency emergency){
        ((Map<Long,Emergency>)this.cache.get("emergencies")).put(emergency.getId(), emergency);
    }
    
    public void storeVehicle(Vehicle vehicle){
        ((Map<Long,Vehicle>)this.cache.get("vehicles")).put(vehicle.getId(), vehicle);
                
    }
    public void storePatient(Patient patient){
       ((Map<Long,Patient>)this.cache.get("patients")).put(patient.getId(), patient);
    }
    
    public Call loadCall(Long id){
        return ((Map<Long,Call>)this.cache.get("calls")).get(id);
    }
    
    public Emergency loadEmergency(Long id){
        return ((Map<Long,Emergency>)this.cache.get("emergencies")).get(id);
    }
    
    public Vehicle loadVehicle(Long id){
        return ((Map<Long,Vehicle>)this.cache.get("vehicles")).get(id);
    }
    
    public Patient loadPatient(Long id){
        return ((Map<Long,Patient>)this.cache.get("patients")).get(id);
    }
   
    public Collection<Vehicle> getAllVehicles(){
        return ((Map<Long,Vehicle>)this.cache.get("vehicles")).values();
    }
    
}
