/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.Graphicable;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableFactory;
import com.wordpress.salaboy.events.keyboard.KeyboardPulseEventGenerator;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.Ambulance;
import com.wordpress.salaboy.model.FireTruck;
import com.wordpress.salaboy.model.PoliceCar;
import com.wordpress.salaboy.model.Vehicle;
import com.wordpress.salaboy.model.messages.VehicleHitsCornerMessage;
import com.wordpress.salaboy.model.messages.VehicleHitsEmergencyMessage;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;
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
    private Graphicable graphicableVehicle;
    private Vehicle vehicle;
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
        if (graphicableVehicle != null) {
            g.draw(graphicableVehicle.getPolygon());
        }
    }

    public void renderAnimation(GameContainer gc, Graphics g) {
        g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
        if (graphicableVehicle != null) {
            g.drawAnimation(graphicableVehicle.getAnimation(), graphicableVehicle.getPolygon().getX(), graphicableVehicle.getPolygon().getY());
        }
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.graphicableVehicle = GraphicableFactory.newVehicle(vehicle);
        this.playerInitialX = this.graphicableVehicle.getPolygon().getX();
        this.playerInitialY = this.graphicableVehicle.getPolygon().getY();
    }

    public void onKeyPressed(int code, char key) {
        if (Input.KEY_ESCAPE == code) {
            this.ui.goToGlobalMap();
        } else if (Input.KEY_F1 == code) {
            addMockAmbulance();
        } else if (Input.KEY_F2 == code) {
            addMockFireTruck();
        } else if (Input.KEY_F3 == code) {
            addMockPoliceCar();
        } else if (Input.KEY_LSHIFT == code) {
            this.turbo = true;
        } else {
            KeyboardPulseEventGenerator.getInstance().generateEvent(key);
        }

    }

    public void onKeyReleased(int code, char key) {
        if (Input.KEY_LSHIFT == code) {
            this.turbo = false;
        }
    }

    public void onClick(int button, int x, int y, int count) {
    }

    public void update(GameContainer gc, int delta) {
        if (gc.getInput().isKeyDown(Input.KEY_LEFT)) {
            this.moveVehicle(Input.KEY_LEFT);
        } else if (gc.getInput().isKeyDown(Input.KEY_RIGHT)) {
            this.moveVehicle(Input.KEY_RIGHT);
        } else if (gc.getInput().isKeyDown(Input.KEY_UP)) {
            this.moveVehicle(Input.KEY_UP);
        } else if (gc.getInput().isKeyDown(Input.KEY_DOWN)) {
            this.moveVehicle(Input.KEY_DOWN);
        }

        //check for collitions
        checkCornerCollision();
        checkEmergencyCollision();
        checkHospitalCollision();
    }

    private void moveVehicle(int direction) {
        if (this.graphicableVehicle == null) {
            return;
        }

        int current = this.graphicableVehicle.getAnimation().getFrame();
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

        this.graphicableVehicle.getAnimation().setCurrentFrame(current);

        if (turbo) {
            delta *= 5;
        }

        if (direction == Input.KEY_LEFT || direction == Input.KEY_RIGHT) {
            playerInitialX += delta;
        } else if (direction == Input.KEY_UP || direction == Input.KEY_DOWN) {
            playerInitialY += delta;
        }

        this.graphicableVehicle.getPolygon().setX(playerInitialX);
        this.graphicableVehicle.getPolygon().setY(playerInitialY);

        if (checkEntityCollision()) {
            if (direction == Input.KEY_LEFT || direction == Input.KEY_RIGHT) {
                playerInitialX -= delta;
            } else if (direction == Input.KEY_UP || direction == Input.KEY_DOWN) {
                playerInitialY -= delta;
            }
            this.graphicableVehicle.getPolygon().setX(playerInitialX);
            this.graphicableVehicle.getPolygon().setY(playerInitialY);
        }
    }

    private synchronized boolean checkEntityCollision() {
        for (int i = 0; i < BlockMap.entities.size(); i++) {
            Block entity1 = (Block) BlockMap.entities.get(i);
            if (this.graphicableVehicle.getPolygon().intersects(entity1.poly)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean checkEmergencyCollision() {
        //if no vehicle, no collition
        if (this.graphicableVehicle == null) {
            return false;
        }

        if (this.graphicableVehicle.getPolygon().intersects(emergency.getPolygon())) {
            try {
                System.out.println("EMERGENCY REACHED!");
                MessageFactory.sendMessage(new VehicleHitsEmergencyMessage(this.vehicle.getId(), this.emergency.getCallId(), new Date()));
                return true;
            } catch (HornetQException ex) {
                Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public synchronized boolean checkHospitalCollision() {
        //if no vehicle, no collition
        if (this.graphicableVehicle == null) {
            return false;
        }

        for (int i = 0; i < BlockMap.hospitals.size(); i++) {
            Block entity1 = (Block) BlockMap.hospitals.get(i);
            if (this.graphicableVehicle.getPolygon().intersects(entity1.poly)) {
                //TODO: add custom code. Maybe we should notify the ui about it
                //and not add a message to the queue here
                System.out.println("HOSPITAL REACHED!");
                return true;
            }
        }
        return false;
    }

    public boolean checkCornerCollision() {
        //if no vehicle, no collition
        if (this.graphicableVehicle == null) {
            return false;
        }

        for (int i = 0; i < BlockMap.corners.size(); i++) {
            Block entity1 = (Block) BlockMap.corners.get(i);
            if (this.graphicableVehicle.getPolygon().intersects(entity1.poly)) {
                try {
                    System.out.println("CORNER REACHED!");
                    MessageFactory.sendMessage(new VehicleHitsCornerMessage(this.vehicle.getId(), (int)this.graphicableVehicle.getPolygon().getX(), (int)this.graphicableVehicle.getPolygon().getY()));
                    return true;
                } catch (HornetQException ex) {
                    Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }
        }
        return false;
    }

    private void addMockAmbulance() {

        Ambulance ambulance = new Ambulance("Mock Ambulance");
        ambulance.setId(System.currentTimeMillis());

        this.ui.assignVehicleToEmergency(this.emergency.getCallId(), ambulance);
    }

    private void addMockFireTruck() {

        FireTruck fireTruck = new FireTruck("Mock Fire Truck");
        fireTruck.setId(System.currentTimeMillis());

        this.ui.assignVehicleToEmergency(this.emergency.getCallId(), fireTruck);
    }

    private void addMockPoliceCar() {

        PoliceCar policeCar = new PoliceCar("Mock Police Car");
        policeCar.setId(System.currentTimeMillis());

        this.ui.assignVehicleToEmergency(this.emergency.getCallId(), policeCar);
    }
}
