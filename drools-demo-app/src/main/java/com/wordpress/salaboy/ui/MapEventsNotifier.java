/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.log.Logger;
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
        EMERGENCY_REACHED,
        AMBULANCE_POSITION,
        HOSPITAL_SELECTED,
        HEART_BEAT_RECEIVED,
        NO_VITAL_SIGNS,
        HEART_ATTACK
    }
    
    private final List<MapEventsListener> mapEventListeners = new ArrayList<MapEventsListener>();
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);
    private final Logger logger;

    public MapEventsNotifier(Logger logger){
        this.logger = logger;
    }
    
    public void notifyMapEventsListeners(final EVENT_TYPE type, final Object data) {
        for (final MapEventsListener mapEventsListener : mapEventListeners) {
            executor.execute(new Runnable()  {
                @Override
                public void run() {
                    logger.addMessage(type+": "+data);
                    switch (type){
                        case EMERGENCY_REACHED:
                            mapEventsListener.emergencyReached((Block) data);
                            break;
                        case HOSPITAL_REACHED:
                            mapEventsListener.hospitalReached((Block) data);
                            break;
                        case AMBULANCE_POSITION:
                            mapEventsListener.positionReceived((Block) data);
                            break;
                        case HOSPITAL_SELECTED:
                            mapEventsListener.hospitalSelected((Long) data);
                            break;
                        case HEART_BEAT_RECEIVED:
                            mapEventsListener.heartBeatReceived((Double) data);
                            break;
                        case HEART_ATTACK:
                            mapEventsListener.monitorAlertReceived((String) data);
                            break;
                        case NO_VITAL_SIGNS:
                            mapEventsListener.monitorAlertReceived((String) data);
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
