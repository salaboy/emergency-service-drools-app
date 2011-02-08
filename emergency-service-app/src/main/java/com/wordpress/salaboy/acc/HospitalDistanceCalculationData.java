/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.acc;

import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.Hospital;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class HospitalDistanceCalculationData implements Serializable {

    private Hospital hospital;
    private Ambulance ambulance;

    public HospitalDistanceCalculationData(Hospital hospital, Ambulance ambulance) {
        this.hospital = hospital;
        this.ambulance = ambulance;
    }

    public Ambulance getAmbulance() {
        return ambulance;
    }

    public Hospital getHospital() {
        return hospital;
    }
    
}
