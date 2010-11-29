/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.plugtree.training.model;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class Medic implements Serializable {
    public enum MedicSpeciality{BURNS, BONES, REANIMATION};
    private Long id;
    
    private MedicSpeciality speciality;

    public Medic() {
    }

   


    public Medic(MedicSpeciality speciality) {
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
