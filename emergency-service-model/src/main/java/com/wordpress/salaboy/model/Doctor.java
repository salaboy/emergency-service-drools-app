/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class Doctor implements Serializable {
    public enum DoctorSpeciality{BURNS, BONES, REANIMATION};
    private Long id;
    private DoctorSpeciality speciality;
    public static AtomicLong incrementalId = new AtomicLong();
    
    public Doctor() {
        this.id = Doctor.incrementalId.getAndIncrement();
    }

   


    public Doctor(DoctorSpeciality speciality) {
        this();
        this.speciality = speciality;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DoctorSpeciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(DoctorSpeciality speciality) {
        this.speciality = speciality;
    }

  

    
    
}
