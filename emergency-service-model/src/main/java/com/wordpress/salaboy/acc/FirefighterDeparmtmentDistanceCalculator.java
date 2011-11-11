/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.acc;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.buildings.FirefightersDepartment;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.drools.runtime.rule.AccumulateFunction;

/**
 *
 * @author esteban
 */
public class FirefighterDeparmtmentDistanceCalculator implements AccumulateFunction, Serializable {

    //TODO: change this values to real firefighters deparment values
    private int[] xs = new int[]{1, 7, 13, 19, 25, 31, 37};
    private int[] ys = new int[]{1, 7, 13, 19, 25};
    
    public static class ContextData implements Serializable{
        public FirefightersDepartment selectedDepartment;
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
        contextData.selectedDepartment = null;
        contextData.minDistance = 0;
    }

    @Override
    public void accumulate(Serializable context, Object value) {
        ContextData contextData = (ContextData)context;
        
        FirefightersDepartmentDistanceCalculationData data = (FirefightersDepartmentDistanceCalculationData)value;
        FirefightersDepartment currentDepartment = data.getDepartment();
        Emergency emergency = data.getEmergency();
        
        float difX = currentDepartment.getX() - xs[emergency.getLocation().getLocationX()];
        float difY = currentDepartment.getY() - ys[emergency.getLocation().getLocationY()];
        
        double difTotal = Math.sqrt(Math.pow(((double)difX),2d) + Math.pow(((double)difY),2d));
        
        if(contextData.selectedDepartment == null || difTotal < contextData.minDistance){ 
            contextData.minDistance = difTotal; 
            contextData.selectedDepartment = currentDepartment;
        }
        
    }

    @Override
    public void reverse(Serializable context, Object value) throws Exception {
    }

    @Override
    public Object getResult(Serializable context) throws Exception {
        return ((ContextData)context).selectedDepartment;
    }

    @Override
    public boolean supportsReverse() {
        return false;
    }

}
