/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.acc;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.Hospital;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.drools.runtime.rule.AccumulateFunction;

/**
 *
 * @author esteban
 */
public class HospitalDistanceCalculator implements AccumulateFunction, Serializable {

    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private int[] ys = new int[]{1, 7, 13, 19, 25};
    
    public static class ContextData implements Serializable{
        public Hospital selectedHospital;
        public double minDistance;
    }
    
    @Override 
    public void writeExternal(ObjectOutput out) throws IOException {
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    @Override
    public Serializable createContext() {
        return new ContextData();
    }

    @Override
    public void init(Serializable context) throws Exception {
        ContextData contextData = (ContextData)context;
        contextData.selectedHospital = null;
        contextData.minDistance = 0;
    }

    @Override
    public void accumulate(Serializable context, Object value) {
        ContextData contextData = (ContextData)context;
        
        HospitalDistanceCalculationData data = (HospitalDistanceCalculationData)value;
        Hospital currentHospital = data.getHospital();
        Emergency emergency = data.getEmergency();
        
        float difX = currentHospital.getPositionX() - xs[emergency.getLocation().getLocationX()];
        float difY = currentHospital.getPositionY() - ys[emergency.getLocation().getLocationY()];
        
        double difTotal = Math.sqrt(Math.pow(((double)difX),2d) + Math.pow(((double)difY),2d));
        System.out.println("Hospital: "+currentHospital.getName()+" - Diff Total = "+difTotal);
        System.out.println("CurrentHospitalX = "+currentHospital.getPositionX());
        System.out.println("CurrentHospitalY = "+currentHospital.getPositionY());
        System.out.println("EmergencyX = "+xs[emergency.getLocation().getLocationX()]);
        System.out.println("EmergencyY = "+ys[emergency.getLocation().getLocationY()]);
        
        if(contextData.selectedHospital == null || difTotal < contextData.minDistance){ 
            contextData.minDistance = difTotal; 
            contextData.selectedHospital = currentHospital;
        }
        
    }

    @Override
    public void reverse(Serializable context, Object value) throws Exception {
    }

    @Override
    public Object getResult(Serializable context) throws Exception {
        return ((ContextData)context).selectedHospital;
    }

    @Override
    public boolean supportsReverse() {
        return false;
    }

}
