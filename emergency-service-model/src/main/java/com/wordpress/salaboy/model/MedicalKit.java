/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.model;

import java.io.Serializable;
import com.wordpress.salaboy.model.Doctor.DoctorSpeciality;

/**
 *
 * @author salaboy
 */
public class MedicalKit implements Serializable{
    private Long id;
    private String name;
    private DoctorSpeciality type;

    public MedicalKit(String name, DoctorSpeciality type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DoctorSpeciality getType() {
        return type;
    }

    public void setType(DoctorSpeciality type) {
        this.type = type;
    }
    
}
