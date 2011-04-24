/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableFactory;
import com.wordpress.salaboy.events.keyboard.KeyboardPulseEventGenerator;
import com.wordpress.salaboy.model.Ambulance;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

/**
 *
 * @author esteban
 */
public class ParticularEmergencyRenderer implements EmergencyRenderer {

    private final WorldUI ui;
    private final GraphicableEmergency emergency;
    private GraphicableAmbulance vehicle;
    private float playerInitialX = 32;
    private float playerInitialY = 400;
    
    private boolean turbo;

    public ParticularEmergencyRenderer(WorldUI ui, GraphicableEmergency emergency) {
        this.emergency = emergency;
        this.ui = ui;
    }

    /**
     * 
     * @param ui 
     */
    @Override
    public void renderPolygon(GameContainer gc, Graphics g) {
        g.draw(emergency.getPolygon());
        if (vehicle != null) {
            g.draw(vehicle.getPolygon());
        }
    }

    public void renderAnimation(GameContainer gc, Graphics g) {
        g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
        if (vehicle != null) {
            g.drawAnimation(vehicle.getAnimation(), vehicle.getPolygon().getX(), vehicle.getPolygon().getY());
        }
    }

    public void addVehicle(Ambulance vehicle) {
        this.vehicle = GraphicableFactory.newAmbulance(vehicle);
    }

    public void onKeyPressed(int code, char key) {
        if (Input.KEY_ESCAPE == code) {
            this.ui.goToGlobalMap();
        } else if (Input.KEY_F1 == code) {
            addMockAmbulance();
        } else if (Input.KEY_LSHIFT == code){
            this.turbo = true;
        } else{
            KeyboardPulseEventGenerator.getInstance().generateEvent(key);
        }
        
    }

    public void onKeyReleased(int code, char key) {
        if (Input.KEY_LSHIFT == code){
            this.turbo = false;
        } 
    }
    
    public void onClick(int button, int x, int y, int count) {
    }

    public void update(GameContainer gc, int delta) {
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)){
            this.moveVehicle(Input.KEY_LEFT);
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)){
            this.moveVehicle(Input.KEY_RIGHT);
        } else if (gc.getInput().isKeyDown(Input.KEY_UP)){
            this.moveVehicle(Input.KEY_UP);
        } else if (gc.getInput().isKeyDown(Input.KEY_DOWN)){
            this.moveVehicle(Input.KEY_DOWN);
        }
    }
    
    private void moveVehicle(int direction) {
        if (this.vehicle == null) {
            return;
        }

        int current = this.vehicle.getAnimation().getFrame();
        int delta = 0;
        switch (direction) {
            case Input.KEY_LEFT:
                if (current < 7) {
                    current += 1;
                } else {
                    current = 4;
                }
                delta = -1;
                break;
            case Input.KEY_RIGHT:
                if (current < 3) {
                    current += 1;
                } else {
                    current = 0;
                }
                delta = +1;
                break;
            case Input.KEY_UP:
                if (current < 15) {
                    current += 1;
                } else {
                    current = 12;
                }
                delta = -1;
                break;
            case Input.KEY_DOWN:
                if (current < 11) {
                    current += 1;
                } else {
                    current = 8;
                }
                delta = +1;
                break;
        }

        this.vehicle.getAnimation().setCurrentFrame(current);

        if (turbo) {
            delta *=5;
        } 
        
        if (direction == Input.KEY_LEFT || direction == Input.KEY_RIGHT){
            playerInitialX += delta;
        }else if (direction == Input.KEY_UP || direction == Input.KEY_DOWN){
            playerInitialY += delta;
        }
        
        this.vehicle.getPolygon().setX(playerInitialX);
        this.vehicle.getPolygon().setY(playerInitialY);
        
        if (entityCollisionWith()) {
            if (direction == Input.KEY_LEFT || direction == Input.KEY_RIGHT){
                playerInitialX -= delta;
            }else if (direction == Input.KEY_UP || direction == Input.KEY_DOWN){
                playerInitialY -= delta;
            }
            this.vehicle.getPolygon().setX(playerInitialX);
            this.vehicle.getPolygon().setY(playerInitialY);
        }
    }

    
    private synchronized boolean entityCollisionWith() {
        for (int i = 0; i < BlockMap.entities.size(); i++) {
            Block entity1 = (Block) BlockMap.entities.get(i);
            if (this.vehicle.getPolygon().intersects(entity1.poly)) {
                return true;
            }
        }
        return false;
    }

    private void addMockAmbulance() {

        Ambulance ambulance = new Ambulance("Mock Ambulance");
        ambulance.setId(System.currentTimeMillis());
        ambulance.setPositionX(emergency.getCallX());
        ambulance.setPositionY(emergency.getCallY());

        this.ui.assignVehicleToEmergency(this.emergency.getCallId(), ambulance);
    }


}
