/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.MyDroolsService;
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
    
    private String lastReportedPosition = "";
    
    public void notifyMapEventsListeners(final EVENT_TYPE type, final Object data) {
        for (final MapEventsListener mapEventsListener : mapEventListeners) {
            executor.execute(new Runnable()  {
                @Override
                public void run() {
                    
                    switch (type){
                        case EMERGENCY_REACHED:
                            mapEventsListener.emergencyReached((Block) data);
                            logger.addMessage("The emergency location was reached by the Ambulance");
                            break;
                        case HOSPITAL_REACHED:
                            mapEventsListener.hospitalReached((Block) data);
                            logger.addMessage(" The Hospital: "+MyDroolsService.getHospitalByCoordinates((Block)data).getName()+" was selected");
                            break;
                        case AMBULANCE_POSITION:
                            mapEventsListener.positionReceived((Block) data);
                            int x = (int)Math.round(((Block) data).poly.getX()/16);
                            int y = (int)Math.round(((Block) data).poly.getY()/16);
                            
                            String position = MyDroolsService.translatePosition(x, y);
                            if (!lastReportedPosition.equals(position)){
                                lastReportedPosition = position;
                                logger.addMessage("The Ambulance was at "+MyDroolsService.translatePosition(x, y));
                            }
                            
                            break;
                        case HOSPITAL_SELECTED:
                            mapEventsListener.hospitalSelected((Long) data);
                            logger.addMessage(" The Hospital: "+MyDroolsService.getHospitalById((Long)data)+" was selected for this emergency");
                            break;
                        case HEART_BEAT_RECEIVED:
                            mapEventsListener.heartBeatReceived((Double) data);
                            break;
                        case HEART_ATTACK:
                            mapEventsListener.monitorAlertReceived((String) data);
                            logger.addMessage("Alarm: the Patient is having a Heart Attack");
                            break;
                        case NO_VITAL_SIGNS:
                            mapEventsListener.monitorAlertReceived((String) data);
                            logger.addMessage("Alarm: we are not receiving the Patient's vital sign");
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
