/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.events.NotifierEvent;
import com.wordpress.salaboy.events.WorldEventNotifier;
import com.wordpress.salaboy.log.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author salaboy
 * @author esteban
 */
public class MapEventsNotifier {

    

    public static enum EventType{
        HOSPITAL_REACHED,
        EMERGENCY_REACHED,
        AMBULANCE_POSITION,
        HOSPITAL_SELECTED,
        HEART_BEAT_RECEIVED,
        NO_VITAL_SIGNS,
        HEART_ATTACK
    }
    
    
    private final Map<EventType, List<WorldEventNotifier>> mapEventNotifiers = new ConcurrentHashMap<EventType, List<WorldEventNotifier>>();
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    private final Logger logger;

    public MapEventsNotifier(Logger logger){
        this.logger = logger;
    }
    
    public void notifyMapEventsListeners(final EventType type, final NotifierEvent data) {
        if (mapEventNotifiers.get(type) == null){
            return;
        }
        
        for (final WorldEventNotifier mapEventNotifier : mapEventNotifiers.get(type)) {
            executor.execute(new Runnable()  {
                @Override
                public void run() {
                     Thread.currentThread().setName(type+" executor thread");
                     mapEventNotifier.notify(data);
                }
            });
        }
    }
    
   
    
    public void addWorldEventNotifier(EventType type, WorldEventNotifier notifier) {
        List<WorldEventNotifier> notifiers = this.mapEventNotifiers.get(type);
        if(notifiers == null){
            this.mapEventNotifiers.put(type, new ArrayList<WorldEventNotifier>());
            notifiers = this.mapEventNotifiers.get(type);
        }
        notifiers.add(notifier);
    }
    
    public void removeWorldEventNotifier(EventType type){
        this.mapEventNotifiers.remove(type);
    }
    
}
