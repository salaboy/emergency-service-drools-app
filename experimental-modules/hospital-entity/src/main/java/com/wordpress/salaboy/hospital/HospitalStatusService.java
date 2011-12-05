/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author salaboy
 */
public interface HospitalStatusService extends Serializable {
    public int getAvailableBeds();  
    public List<String> getSpecialities();
    public String requestBed(String id);
    
}
