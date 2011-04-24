/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.events.keyboard.KeyboardPulseEventGenerator;
import com.wordpress.salaboy.model.Ambulance;
import java.util.Iterator;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/**
 *
 * @author esteban
 */
public class WorldKeyListener implements KeyListener {

    private final WorldUI worldUI;

    WorldKeyListener(WorldUI cityMapUI) {
        this.worldUI = cityMapUI;
    }

    @Override
    public void keyPressed(int i, char c) {
        if (Input.KEY_SPACE == i) {
            this.worldUI.addRandomEmergency();
        } else if (Input.KEY_ESCAPE == i) {
            this.worldUI.goToGlobalMap();
        } else if (Input.KEY_F1 == i) {
            addMockAmbulance(1);
        } else if (Input.KEY_F2 == i) {
            addMockAmbulance(2);
        } else if (Input.KEY_F3 == i) {
            addMockAmbulance(3);
        } else {
            KeyboardPulseEventGenerator.getInstance().generateEvent(c);
        }
    }

    private void addMockAmbulance(int emergencyIndex) {
        if (this.worldUI.getEmergencies().size() < emergencyIndex) {
            return;
        }
        Long callId = 0L;
        
        Iterator<Long> iterator = this.worldUI.getEmergencies().keySet().iterator();
        for (int i = 0; i < emergencyIndex; i++) {
            callId = iterator.next();
        }
        GraphicableEmergency emergency = this.worldUI.getEmergencies().get(callId);

        Ambulance ambulance = new Ambulance("Mock Ambulance");
        ambulance.setId(System.currentTimeMillis());
        ambulance.setPositionX(emergency.getCallX());
        ambulance.setPositionY(emergency.getCallY());

        this.worldUI.assignVehicleToEmergency(callId, ambulance);
    }

    @Override
    public void keyReleased(int i, char c) {
    }

    @Override
    public void setInput(Input input) {
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public void inputStarted() {
    }
}
