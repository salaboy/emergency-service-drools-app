/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.roles;

import java.util.UUID;

/**
 *
 * @author salaboy
 *
 * This entity represents a Doctor in our scenario
 */
public class Doctor extends AbstractRole {

    public enum DoctorSpeciality {
        BURNS, BONES, REANIMATION
    };
    
    private DoctorSpeciality speciality;

    public Doctor(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public Doctor(String name, DoctorSpeciality speciality) {
        this(name);
        this.speciality = speciality;
    }

    public DoctorSpeciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(DoctorSpeciality speciality) {
        this.speciality = speciality;
    }
}
