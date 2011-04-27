/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.events.keyboard;

import com.wordpress.salaboy.services.old.GridEmergencyService;
import com.wordpress.salaboy.events.PatientVitalSignNotifierEvent;
import com.wordpress.salaboy.events.PulseEvent;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.events.MapEventsNotifier;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author esteban
 */
public class KeyboardPulseEventGenerator {
    
    private static KeyboardPulseEventGenerator INSTANCE;

    private KeyboardPulseEventGenerator() {
    }
    
    public static synchronized KeyboardPulseEventGenerator getInstance(){
        if (INSTANCE == null){
            INSTANCE = new KeyboardPulseEventGenerator();
        }
        
        return INSTANCE;
    }
    
    private Map<Character,Ambulance> upKeysList = new HashMap<Character, Ambulance>();
    private Map<Character,Ambulance> downKeysList = new HashMap<Character, Ambulance>();
    
    public void resetConfigurations(){
        this.upKeysList = new HashMap<Character, Ambulance>();
        this.downKeysList = new HashMap<Character, Ambulance>();
    }
    
    public void addConfiguration(char upKey, char downKey, Ambulance ambulance){
        this.upKeysList.put(upKey, ambulance);
        this.downKeysList.put(downKey, ambulance);
    }

    public boolean isKeyMapped(char key){
        return this.upKeysList.containsKey(key) || this.downKeysList.containsKey(key);
    }
    
    public void generateEvent(char key){
        if (!this.isKeyMapped(key)){
            return;
        }
        int pulseValue = new Double(String.valueOf(Math.random()*1000)).intValue();
        Long ambulanceId = 0L;
        if (this.upKeysList.containsKey(key)){
            ambulanceId = this.upKeysList.get(key).getId();
        }else{
            ambulanceId = this.downKeysList.get(key).getId();
            pulseValue = -1*pulseValue;
        }
        
        GridEmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.HEART_BEAT_RECEIVED, new PatientVitalSignNotifierEvent(new PulseEvent(pulseValue + 235), ambulanceId));
    }
    
}
