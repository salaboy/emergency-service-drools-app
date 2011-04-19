package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.events.MapEventsNotifier;
import com.wordpress.salaboy.services.GridEmergencyService;
import com.wordpress.salaboy.events.PatientVitalSignNotifierEvent;
import com.wordpress.salaboy.events.PulseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author salaboy
 * @author esteban
 */
public class AmbulanceMonitorService {

    private static AmbulanceMonitorService monitorService;
    private HeartBeatsEmulator heartBeatsEmulator;
    
    private boolean running = false;
    public synchronized static AmbulanceMonitorService getInstance() {
        if (monitorService == null) {
            monitorService = new AmbulanceMonitorService();
        }
        return monitorService;
    }

    private AmbulanceMonitorService() {
        
    }

    public synchronized void start() {
        System.out.println("Starting heart beats emulator at "+this);
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
        this.running = true;
    }

    public void stop() {
        if (this.heartBeatsEmulator != null && this.heartBeatsEmulator.isRunning()) {
            this.heartBeatsEmulator.stop();
        }
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }
    
    

   
    private class HeartBeatsEmulator implements Runnable {

        private boolean continueMonitoring = true;

        public HeartBeatsEmulator() {
        }

        @Override
        public void run() {
            while (continueMonitoring) {
                try {
                    PulseEvent evt = new PulseEvent(235);
                    // Add the AmbulanceId
                    GridEmergencyService.getInstance().getMapEventsNotifier().notifyMapEventsListeners(MapEventsNotifier.EventType.HEART_BEAT_RECEIVED, new PatientVitalSignNotifierEvent(evt, null));
                    Thread.sleep(1000);
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
}
