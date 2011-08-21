/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.acc;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.FirefightersDepartment;
import java.io.Serializable;

/**
 *
 * @author esteban
 */
public class FirefightersDepartmentDistanceCalculationData implements Serializable {

    private FirefightersDepartment department;
    private Emergency emergency;

    public FirefightersDepartmentDistanceCalculationData(FirefightersDepartment department, Emergency emergency) {
        this.department = department;
        this.emergency = emergency;
    }

    public Emergency getEmergency() {
        return emergency;
    }

    public FirefightersDepartment getDepartment() {
        return department;
    }

}
