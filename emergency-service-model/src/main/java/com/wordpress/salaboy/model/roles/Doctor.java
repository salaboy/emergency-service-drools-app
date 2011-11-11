/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.model.roles;

import com.wordpress.salaboy.model.roles.Role;
import java.io.Serializable;

/**
 *
 * @author salaboy
 *
 * This entity represents a Doctor in our scenario
 */
public class Doctor implements Role, Serializable {

    public enum DoctorSpeciality {
        BURNS, BONES, REANIMATION
    };
    private String id;
    private String name;
    private DoctorSpeciality speciality;

    public Doctor() {
    }

    public Doctor(String name, DoctorSpeciality speciality) {
        this.name = name;
        this.speciality = speciality;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }
   
    public DoctorSpeciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(DoctorSpeciality speciality) {
        this.speciality = speciality;
    }
}
