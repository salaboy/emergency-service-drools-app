package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.events.WiiMoteEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import motej.Mote;
import motej.event.AccelerometerEvent;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

/**
 *
 * @author esteban
 */
public class AmbulanceMonitorService {

    private StatefulKnowledgeSession ksession;
    private HeartBeatsEmulator heartBeatsEmulator;
    private MapEventsNotifier notifier;

    public AmbulanceMonitorService(StatefulKnowledgeSession ksession, MapEventsNotifier notifier) {
        this.ksession = ksession;
        this.notifier = notifier;
    }

    public void start() {
        if (heartBeatsEmulator != null && heartBeatsEmulator.isRunning()){
            throw new IllegalStateException("Heart Beats Emulator is already running!");
        }
        this.heartBeatsEmulator = new HeartBeatsEmulator(ksession, notifier);
        Thread t = new Thread(heartBeatsEmulator);
        t.start();
    }
    
    public void stop() {
        if (this.heartBeatsEmulator != null && this.heartBeatsEmulator.isRunning()){
            this.heartBeatsEmulator.stop();
        }
    }

}


class HeartBeatsEmulator implements Runnable{
    
    private boolean continueMonitoring = true;
    private StatefulKnowledgeSession ksession;
    private MapEventsNotifier notifier;

    public HeartBeatsEmulator(StatefulKnowledgeSession ksession, MapEventsNotifier notifier) {
        this.ksession = ksession;
        this.notifier = notifier;
    }
    
    @Override
    public void run() {
        WorkingMemoryEntryPoint patientHeartbeatsEntryPoint = ksession.getWorkingMemoryEntryPoint("patientHeartbeats");
        while (continueMonitoring){
            try {
            WiiMoteEvent event = new WiiMoteEvent(new AccelerometerEvent<Mote>(null, 235, 235, 235));
            patientHeartbeatsEntryPoint.insert(event);
            notifier.notifyMapEventsListeners(MapEventsNotifier.EVENT_TYPE.HEART_BEAT_RECEIVED, (double)event.getEvent().getY());
            Thread.sleep(500);
            } catch (Exception ex) {
                Logger.getLogger(EmergencyMonitorPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void stop() {
        this.continueMonitoring = false;
    }
    
    public boolean isRunning(){
        return this.continueMonitoring;
    }
    
}