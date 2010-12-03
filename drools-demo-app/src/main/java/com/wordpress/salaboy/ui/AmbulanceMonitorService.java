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
    private WorkingMemoryEntryPoint patientHeartbeatsEntryPoint;

    private class HeartBeatsEmulator implements Runnable {

        private boolean continueMonitoring = true;

        public HeartBeatsEmulator() {
        }

        @Override
        public void run() {
            while (continueMonitoring) {
                try {
                    sendNotification(235, 235, 235);
                    Thread.sleep(1);
                } catch (Exception ex) {
                    Logger.getLogger(EmergencyMonitorPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public void stop() {
            this.continueMonitoring = false;
        }

        public boolean isRunning() {
            return this.continueMonitoring;
        }
    }

    public AmbulanceMonitorService(StatefulKnowledgeSession ksession, MapEventsNotifier notifier) {
        this.ksession = ksession;
        this.notifier = notifier;
        this.patientHeartbeatsEntryPoint = ksession.getWorkingMemoryEntryPoint("patientHeartbeats");
    }

    public void start() {
        if (heartBeatsEmulator != null && heartBeatsEmulator.isRunning()) {
            throw new IllegalStateException("Heart Beats Emulator is already running!");
        }
        try {
            this.heartBeatsEmulator = new HeartBeatsEmulator();
            Thread t = new Thread(heartBeatsEmulator);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (this.heartBeatsEmulator != null && this.heartBeatsEmulator.isRunning()) {
            this.heartBeatsEmulator.stop();
        }
    }

    
    public void sendNotification(int x, int y, int z){
        WiiMoteEvent event = new WiiMoteEvent(new AccelerometerEvent<Mote>(null, x, y, z), "acc");
        this.patientHeartbeatsEntryPoint.insert(event);
        notifier.notifyMapEventsListeners(MapEventsNotifier.EVENT_TYPE.HEART_BEAT_RECEIVED, (double)y);
    }
    
}
