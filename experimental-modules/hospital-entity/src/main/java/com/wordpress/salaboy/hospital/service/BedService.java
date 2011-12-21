/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital.service;

import com.wordpress.salaboy.hospital.Hospital;
import com.wordpress.salaboy.hospital.model.units.AbstractUnit;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author salaboy
 * Based on a Hospital the Bed Service Distribute randomly the 
 * amount of available beds in different hospital Units
 */
public class BedService {
    private Hospital hospital;
    private Map<String, AbstractUnit> units = new HashMap<String, AbstractUnit>();
    
    
    public BedService(Hospital hospital) {
        this.hospital = hospital;
        
    }

    public AbstractUnit put(String unit, AbstractUnit v) {
        return units.put(unit, v);
    }

   
    
    
    /**
    * Return 0 if no bed available
    */
    
    public int requestBed(String unit){
        return this.units.get(unit).requestBed();
        
    }
    
    
    
}
