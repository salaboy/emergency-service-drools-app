/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author esteban
 */
public class MapEventsNotifier {

    public static enum EVENT_TYPE{
        HOSPITAL_REACHED,
        EMERGENCY_REACHED
    }
    
    private final List<MapEventsListener> mapEventListeners = new ArrayList<MapEventsListener>();
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    public void notifyMapEventsListeners(final EVENT_TYPE type, final Block source) {
        for (final MapEventsListener mapEventsListener : mapEventListeners) {
            executor.execute(new Runnable()  {
                @Override
                public void run() {
                    switch (type){
                        case EMERGENCY_REACHED:
                            mapEventsListener.emergencyReached(source);
                            break;
                        case HOSPITAL_REACHED:
                            mapEventsListener.hospitalReached(source);
                            break;
                    }
                }
            });
        }
    }
    
    public void addMapEventsListener(MapEventsListener listener){
        this.mapEventListeners.add(listener);
    }
    
}
