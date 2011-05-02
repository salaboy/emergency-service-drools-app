/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.acc;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class HospitalDistanceCalculationData implements Serializable {

    private Hospital hospital;
    private Emergency emergency;

    public HospitalDistanceCalculationData(Hospital hospital, Emergency emergency) {
        this.hospital = hospital;
        this.emergency = emergency;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public Hospital getHospital() {
        return hospital;
    }
    
}
