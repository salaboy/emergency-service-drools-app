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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Graphicable activeGraphicableVehicle;
    private List<Graphicable> graphicableVehicles;
    private Vehicle activeVehicle;
    private Map<Graphicable,Vehicle> vehicles;
    private boolean turbo;

    public ParticularEmergencyRenderer(WorldUI ui, GraphicableEmergency emergency) {
        this.emergency = emergency;
        this.ui = ui;
        this.vehicles = new HashMap<Graphicable, Vehicle>();
        this.graphicableVehicles = new ArrayList<Graphicable>();
    }

    /**
     * 
     * @param ui 
     */
    @Override
    public void renderPolygon(GameContainer gc, Graphics g) {
        g.draw(emergency.getPolygon());
        for (Graphicable vehicle : graphicableVehicles) {
            g.draw(vehicle.getPolygon());
        }
    }

    public void renderAnimation(GameContainer gc, Graphics g) {
        g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
        for (Graphicable vehicle : graphicableVehicles) {
            //the active vehicle is rendered at the end
            if (activeGraphicableVehicle == null || activeGraphicableVehicle != vehicle){
                g.drawAnimation(vehicle.getAnimation(), vehicle.getPolygon().getX(), vehicle.getPolygon().getY());
            }
        }
        
        if (activeGraphicableVehicle != null){
            g.drawAnimation(activeGraphicableVehicle.getAnimation(), activeGraphicableVehicle.getPolygon().getX(), activeGraphicableVehicle.getPolygon().getY());
        }
    }

    public void addVehicle(Vehicle vehicle) {
        
        this.activeGraphicableVehicle = GraphicableFactory.newVehicle(vehicle);
        
        vehicle.setPositionX(this.activeGraphicableVehicle.getPolygon().getX());
        vehicle.setPositionY(this.activeGraphicableVehicle.getPolygon().getY());

        this.vehicles.put(activeGraphicableVehicle,vehicle);
        this.graphicableVehicles.add(activeGraphicableVehicle);
        
        this.activeVehicle = vehicle;
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
        for (Graphicable graphicable : graphicableVehicles) {
            if (graphicable.getPolygon().contains(x, y)){
                this.activeGraphicableVehicle = graphicable;
                this.activeVehicle = vehicles.get(this.activeGraphicableVehicle);
                return;
            }
        }
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
        if (this.activeGraphicableVehicle == null) {
            return;
        }

        int current = this.activeGraphicableVehicle.getAnimation().getFrame();
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

        this.activeGraphicableVehicle.getAnimation().setCurrentFrame(current);
        Vehicle currentVehicle = this.vehicles.get(this.activeGraphicableVehicle);

        if (turbo) {
            delta *= 5;
        }

        int playerX = (int) currentVehicle.getPositionX();
        int playerY = (int) currentVehicle.getPositionY();
        
        if (direction == Input.KEY_LEFT || direction == Input.KEY_RIGHT) {
            playerX += delta;
        } else if (direction == Input.KEY_UP || direction == Input.KEY_DOWN) {
            playerY += delta;
        }

        this.activeGraphicableVehicle.getPolygon().setX(playerX);
        this.activeGraphicableVehicle.getPolygon().setY(playerY);
        currentVehicle.setPositionX(playerX);
        currentVehicle.setPositionY(playerY);
        
        if (checkEntityCollision()) {
            if (direction == Input.KEY_LEFT || direction == Input.KEY_RIGHT) {
                playerX -= delta;
            } else if (direction == Input.KEY_UP || direction == Input.KEY_DOWN) {
                playerY -= delta;
            }
            this.activeGraphicableVehicle.getPolygon().setX(playerX);
            this.activeGraphicableVehicle.getPolygon().setY(playerY);
            currentVehicle.setPositionX(playerX);
            currentVehicle.setPositionY(playerY);
        }
    }

    private synchronized boolean checkEntityCollision() {
        for (int i = 0; i < BlockMap.entities.size(); i++) {
            Block entity1 = (Block) BlockMap.entities.get(i);
            if (this.activeGraphicableVehicle.getPolygon().intersects(entity1.poly)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean checkEmergencyCollision() {
        //if no vehicle, no collition
        if (this.activeGraphicableVehicle == null) {
            return false;
        }

        if (this.activeGraphicableVehicle.getPolygon().intersects(emergency.getPolygon())) {
            try {
                System.out.println("EMERGENCY REACHED!");
                MessageFactory.sendMessage(new VehicleHitsEmergencyMessage(this.activeVehicle.getId(), this.emergency.getCallId(), new Date()));
                return true;
            } catch (HornetQException ex) {
                Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public synchronized boolean checkHospitalCollision() {
        //if no vehicle, no collition
        if (this.activeGraphicableVehicle == null) {
            return false;
        }

        for (int i = 0; i < BlockMap.hospitals.size(); i++) {
            Block entity1 = (Block) BlockMap.hospitals.get(i);
            if (this.activeGraphicableVehicle.getPolygon().intersects(entity1.poly)) {
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
        if (this.activeGraphicableVehicle == null) {
            return false;
        }

        for (int i = 0; i < BlockMap.corners.size(); i++) {
            Block entity1 = (Block) BlockMap.corners.get(i);
            if (this.activeGraphicableVehicle.getPolygon().intersects(entity1.poly)) {
                try {
                    System.out.println("CORNER REACHED!");
                    MessageFactory.sendMessage(new VehicleHitsCornerMessage(this.activeVehicle.getId(), (int)this.activeGraphicableVehicle.getPolygon().getX(), (int)this.activeGraphicableVehicle.getPolygon().getY()));
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
