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
public class Medic implements Serializable {
    public enum MedicSpeciality{BURNS, BONES, REANIMATION};
    private Long id;
    private MedicSpeciality speciality;
    public static AtomicLong incrementalId = new AtomicLong();
    
    public Medic() {
        this.id = Medic.incrementalId.getAndIncrement();
    }

   


    public Medic(MedicSpeciality speciality) {
        this();
        this.speciality = speciality;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicSpeciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(MedicSpeciality speciality) {
        this.speciality = speciality;
    }

  

    
    
}