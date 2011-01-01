/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.ui;

import com.wordpress.salaboy.events.keyboard.KeyboardPulseEventGenerator;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;

/**
 *
 * @author esteban
 */
public class CityMapKeyListener implements KeyListener {
    private final CityMapUI cityMapUI;
    
    CityMapKeyListener(CityMapUI cityMapUI) {
        this.cityMapUI = cityMapUI;
    }

    @Override
    public void keyPressed(int i, char c) {
        if (Input.KEY_LSHIFT == i){
            this.cityMapUI.setTurbo(true);
        }else{
            KeyboardPulseEventGenerator.getInstance().generateEvent(c);
        }
    }

    @Override
    public void keyReleased(int i, char c) {
        if (Input.KEY_LSHIFT == i){
            this.cityMapUI.setTurbo(false);
        }
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
