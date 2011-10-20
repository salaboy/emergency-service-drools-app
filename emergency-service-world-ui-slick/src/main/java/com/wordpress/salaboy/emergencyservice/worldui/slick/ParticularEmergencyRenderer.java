/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.*;
import com.wordpress.salaboy.messaging.MessageFactory;
import com.wordpress.salaboy.model.*;
import com.wordpress.salaboy.model.command.Command;
import com.wordpress.salaboy.model.messages.*;
import com.wordpress.salaboy.model.messages.patient.HeartBeatMessage;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author esteban
 */
public class ParticularEmergencyRenderer implements EmergencyRenderer {

    private final WorldUI ui;
    private GraphicableEmergency emergency;
    private GraphicableVehicle activeGraphicableVehicle;
    private List<GraphicableVehicle> graphicableVehicles;
    private Vehicle activeVehicle;
    private Map<Graphicable, Vehicle> vehicles;
    private GraphicableHighlightedHospital selectedHospital;
    private GraphicableHighlightedFirefighterDepartment selectedFirefighterDepartment;
    private boolean turbo;
    private boolean hideEmergency;

    public ParticularEmergencyRenderer(WorldUI ui, GraphicableEmergency emergency) {
        this.emergency = emergency;
        this.ui = ui;
        this.vehicles = new HashMap<Graphicable, Vehicle>();
        this.graphicableVehicles = new ArrayList<GraphicableVehicle>();
    }

    /**
     * 
     * @param ui 
     */
    @Override
    public void renderPolygon(GameContainer gc, Graphics g) {
        
        if (!hideEmergency){
            g.draw(emergency.getPolygon());
        }
        
        for (Graphicable vehicle : graphicableVehicles) {
            g.draw(vehicle.getPolygon());
        }
        if (selectedHospital != null) {
            g.draw(selectedHospital.getPolygon());
        }
        if (selectedFirefighterDepartment != null) {
            g.draw(selectedFirefighterDepartment.getPolygon());
        }
    }

    @Override
    public void renderAnimation(GameContainer gc, Graphics g) {
        if (!hideEmergency){        
            g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
        }
        
        for (Graphicable vehicle : graphicableVehicles) {
            //the active vehicle is rendered at the end
            if (activeGraphicableVehicle == null || activeGraphicableVehicle != vehicle) {
                g.drawAnimation(vehicle.getAnimation(), vehicle.getPolygon().getX(), vehicle.getPolygon().getY());
            }
        }

        if (activeGraphicableVehicle != null) {
            g.drawAnimation(activeGraphicableVehicle.getAnimation(), activeGraphicableVehicle.getPolygon().getX(), activeGraphicableVehicle.getPolygon().getY());
        }
        if (selectedHospital != null) {
            g.drawAnimation(selectedHospital.getAnimation(), selectedHospital.getPolygon().getX() - 32, selectedHospital.getPolygon().getY() - 80);
        }
        if (selectedFirefighterDepartment != null) {
            g.drawAnimation(selectedFirefighterDepartment.getAnimation(), selectedFirefighterDepartment.getPolygon().getX() - 32, selectedFirefighterDepartment.getPolygon().getY() - 80);
        }
    }

    public void addVehicle(Vehicle vehicle) {

        this.activeGraphicableVehicle = GraphicableFactory.newVehicle(vehicle);

        vehicle.setPositionX(this.activeGraphicableVehicle.getPolygon().getX());
        vehicle.setPositionY(this.activeGraphicableVehicle.getPolygon().getY());

        this.vehicles.put(activeGraphicableVehicle, vehicle);
        this.graphicableVehicles.add(activeGraphicableVehicle);

        this.activeVehicle = vehicle;
    }

    public void selectHospital(Hospital hospital) {
        selectedHospital = GraphicableFactory.newHighlightedHospital(hospital);
    }
    
    public void selectFirefighterDepartment(FirefightersDepartment firefigthersDepartment) {
        selectedFirefighterDepartment = GraphicableFactory.newHighlightedFirefighterDepartment(firefigthersDepartment);
    }

    @Override
    public void onKeyPressed(int code, char key) {
        if (Input.KEY_ESCAPE == code) {
            this.ui.goToGlobalMap();
        } else if (Input.KEY_Q == code) {
            this.sendHeartBeat(new Random().nextInt(50));
        } else if (Input.KEY_W == code) {
            if (this.activeGraphicableVehicle instanceof GraphicableAmbulance){
                this.sendHeartBeat(-1 * new Random().nextInt(50));
            } else if(this.activeGraphicableVehicle instanceof GraphicableFireTruck){
                this.throwWaterOnFire();
            }
        } else if (Input.KEY_E == code) {
            this.notifyAboutVehicleHittingTheEmergency();
        } else if (Input.KEY_F1 == code) {
            addMockAmbulance();
        } else if (Input.KEY_F2 == code) {
            addMockFireTruck();
        } else if (Input.KEY_F3 == code) {
            addMockPoliceCar();
        } else if (Input.KEY_F4 == code) {
            selectMockHospital(0);
        } else if (Input.KEY_F5 == code) {
            selectMockHospital(1);
        } else if (Input.KEY_F6 == code) {
            selectMockHospital(2);
        } else if (Input.KEY_F7 == code) {
            selectMockFireDepartment(0);
        } else if (Input.KEY_LSHIFT == code) {
            this.turbo = true;
        }

    }

    @Override
    public void onKeyReleased(int code, char key) {
        if (Input.KEY_LSHIFT == code) {
            this.turbo = false;
        }
    }

    @Override
    public void onClick(int button, int x, int y, int count) {
        for (GraphicableVehicle graphicable : graphicableVehicles) {
            if (graphicable.getPolygon().contains(x, y)) {
                this.activeGraphicableVehicle = graphicable;
                this.activeVehicle = vehicles.get(this.activeGraphicableVehicle);
                return;
            }
        }
    }

    @Override
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

        //check for collisions
        checkCornerCollision();
        //checkEmergencyCollision();
        checkHospitalCollision();
        checkFireDepartmentCollision();
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
        //if no vehicle, no collision
        if (this.activeGraphicableVehicle == null) {
            return false;
        }
        
        return this.activeGraphicableVehicle.getPolygon().intersects(emergency.getPolygon());

    }

    public synchronized boolean checkHospitalCollision() {

        //no active vehicle -> no collision
        if (this.activeGraphicableVehicle == null) {
            return false;
        }
        
        //the active vehicle is not an ambulance? -> no collision
        if (!(this.activeGraphicableVehicle instanceof GraphicableAmbulance)){
            return false;
        }
        
        //no previously selected hospital -> no collision
        if (this.selectedHospital == null) {
            return false;
        }
        
        
        Polygon collidesWith = null;
        if (this.activeGraphicableVehicle.getPolygon().intersects(selectedHospital.getPolygon())) {
            collidesWith = selectedHospital.getPolygon();
        }
        boolean collides = collidesWith != null;
        if (collides && !this.activeGraphicableVehicle.isIsCollidingWithABuilding()) {
            System.out.println("Hospital REACHED!: "+this.selectedHospital.getName());
            try {
                //notify the event
                MessageFactory.sendMessage(new VehicleHitsHospitalMessage(this.activeVehicle.getId(), selectedHospital.getHospital(), this.emergency.getCallId(), new Date()));
                //hide the hospital
                this.selectedHospital = null;
            } catch (HornetQException ex) {
                Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.activeGraphicableVehicle.setIsCollidingWithABuilding(collides);

        return collides;

    }
    
    public synchronized boolean checkFireDepartmentCollision() {

        //no active vehicle -> no collision
        if (this.activeGraphicableVehicle == null) {
            return false;
        }
        
        //the active vehicle is not a fire truck? -> no collision
        if (!(this.activeGraphicableVehicle instanceof GraphicableFireTruck)){
            return false;
        }
        
        //no previously selected fire department -> no collision
        if (this.selectedFirefighterDepartment == null) {
            return false;
        }
        
        
        Polygon collidesWith = null;
        if (this.activeGraphicableVehicle.getPolygon().intersects(selectedFirefighterDepartment.getPolygon())) {
            collidesWith = selectedFirefighterDepartment.getPolygon();
        }
        boolean collides = collidesWith != null;
        if (collides && !this.activeGraphicableVehicle.isIsCollidingWithABuilding()) {
            System.out.println("Fire Department REACHED!: "+this.selectedFirefighterDepartment.getName());
            try {
                //get the emergencyId attached to the call
                String emergencyId = this.ui.getTrackingService().getEmergencyAttachedToCall(this.emergency.getCallId());
                
                //notify the event
                MessageFactory.sendMessage(new VehicleHitsFireDepartmentMessage(this.activeVehicle.getId(), selectedFirefighterDepartment.getFirefightersDepartment(), this.emergency.getCallId(), emergencyId, new Date()));
                //hide the fire Department
                this.selectedFirefighterDepartment = null;
            } catch (HornetQException ex) {
                Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.activeGraphicableVehicle.setIsCollidingWithABuilding(collides);

        return collides;

    }

    public boolean checkCornerCollision() {
        //if no vehicle, no collision
        if (this.activeGraphicableVehicle == null) {
            return false;
        }

        Block collidesWith = null;
        for (int i = 0; i < BlockMap.corners.size(); i++) {
            Block entity1 = (Block) BlockMap.corners.get(i);
            if (this.activeGraphicableVehicle.getPolygon().intersects(entity1.poly)) {
                collidesWith = entity1;
                break;
            }
        }

        boolean collides = collidesWith != null;

        if (collides && !this.activeGraphicableVehicle.isIsCollidingWithACorner()) {
            try {
                System.out.println("CORNER REACHED!");
                MessageFactory.sendMessage(new VehicleHitsCornerMessage(this.emergency.getCallId(), this.activeVehicle.getId(), (int) collidesWith.poly.getX(), (int) collidesWith.poly.getY()));
            } catch (HornetQException ex) {
                Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        this.activeGraphicableVehicle.setIsCollidingWithACorner(collides);


        return collides;
    }

    private void sendHeartBeat(int pulse) {
        //only if the active vehicle is an ambulance
        if (this.activeGraphicableVehicle == null || !(this.activeGraphicableVehicle instanceof GraphicableAmbulance)) {
            return;
        }

        pulse += 235;
        try {
            MessageFactory.sendMessage(new HeartBeatMessage(this.emergency.getCallId(), this.activeVehicle.getId(), pulse, new Date()));
        } catch (HornetQException ex) {
            Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendWaterLevelDecreased() {
        //only if the active vehicle is a firetruck
        if (this.activeGraphicableVehicle == null || !(this.activeGraphicableVehicle instanceof GraphicableFireTruck)) {
            return;
        }
        
        try {
            MessageFactory.sendMessage(new FireTruckDecreaseWaterLevelMessage(this.emergency.getCallId(), this.activeVehicle.getId(), new Date()));
        } catch (HornetQException ex) {
            Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void removeEmergency(){
        if (this.emergency == null){
            return;
        }
                
        ui.removeEmergency(emergency.getCallId());
        
        this.emergency = null;
        
    }
    
    private void setHideEmergency(boolean hide){
        if (this.emergency == null){
            return;
        }
        
        this.hideEmergency = hide;
        
    }
    
    private void addMockAmbulance() {
        this.addMockVehicle(new Ambulance("Mock Ambulance"));
    }

    private void addMockFireTruck() {
        this.addMockVehicle(new FireTruck("Mock Fire Truck", 10, 10));
    }

    private void addMockPoliceCar() {
        this.addMockVehicle(new PoliceCar("Mock Police Car"));
    }
    
    private void addMockVehicle(Vehicle vehicle){
        try {
            //if there is no real emergency, create one
            String emergencyId = this.ui.getTrackingService().getEmergencyAttachedToCall(this.emergency.getCallId());
            if ( emergencyId == null){
                Emergency mockEmergency = new Emergency();
                mockEmergency.setCall(this.ui.getPersistenceService().loadCall(this.emergency.getCallId()));
                mockEmergency.setLocation(new Location(this.emergency.getCallX(), this.emergency.getCallY()));
                mockEmergency.setNroOfPeople(10);
                
                if (vehicle instanceof Ambulance){
                    mockEmergency.setType(Emergency.EmergencyType.HEART_ATTACK);
                }else if (vehicle instanceof FireTruck){
                    mockEmergency.setType(Emergency.EmergencyType.FIRE);
                }else{
                    mockEmergency.setType(Emergency.EmergencyType.UNDEFINED);
                }
                
                this.ui.getPersistenceService().storeEmergency(mockEmergency);
                
                this.ui.getTrackingService().attachEmergency(this.emergency.getCallId(), mockEmergency.getId());
                
                emergencyId = mockEmergency.getId();
            }
            
            this.ui.getPersistenceService().storeVehicle(vehicle);
            MessageFactory.sendMessage(new VehicleDispatchedMessage(emergencyId, vehicle.getId()));
        } catch (HornetQException ex) {
            Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void selectMockHospital(int index) {
        Hospital mock = null;
        for (int i = 0; i < index+1; i++) {
            mock = this.ui.getPersistenceService().getAllHospitals().iterator().next();
        }
        
        try {
            MessageFactory.sendMessage(new HospitalSelectedMessage(this.emergency.getCallId(), mock));
        } catch (HornetQException ex) {
            Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void selectMockFireDepartment(int index) {
        FirefightersDepartment mock = null;
        for (int i = 0; i < index+1; i++) {
            mock = this.ui.getPersistenceService().getAllFirefighterDepartments().iterator().next();
        }
        try {
            MessageFactory.sendMessage(new FirefightersDepartmentSelectedMessage(this.emergency.getCallId(), mock));
        } catch (HornetQException ex) {
            Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Notifies that the active vehicle hit the emergency sending a 
     * {@link VehicleHitsEmergencyMessage}. 
     * The message is only sent if the active vehicle is actually hitting
     * the emergency.
     */
    private void notifyAboutVehicleHittingTheEmergency() {
        //if no emergency -> no hit
        if (this.emergency == null){
            return;
        }
            
        //if no active vehicle -> no hit
        if (this.activeGraphicableVehicle == null) {
            return;
        }
        
        //if no collition between the active vehicle and the emergency -> no notification
        if (!this.checkEmergencyCollision()){
            return;
        }
        
        
        System.out.println("EMERGENCY REACHED!");
        try {
            //get the emergency that is related to this call
            String emergencyId = this.ui.getTrackingService().getEmergencyAttachedToCall(this.emergency.getCallId());
            
            //send notification
            MessageFactory.sendMessage(new VehicleHitsEmergencyMessage(this.activeVehicle.getId(), emergencyId, new Date()));
            
        } catch (HornetQException ex) {
            Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void throwWaterOnFire() {
        FireTruck fireTruck = (FireTruck) this.ui.getPersistenceService().loadVehicle(this.activeVehicle.getId());
        
        if (fireTruck.getTankLevel() <=0){
            //Are you kidding me? You don't have enough water! Get out of here!
            return;
        }
        
        //if no collision -> no water
        if (!this.checkEmergencyCollision()){
            return;
        }
        
        String emergencyId = this.ui.getTrackingService().getEmergencyAttachedToCall(this.emergency.getCallId());
        final Emergency realEmergency = this.ui.getPersistenceService().loadEmergency(emergencyId);
        
        //HACK: We are using NroOfPeople as the life of the fire
        realEmergency.setNroOfPeople(realEmergency.getNroOfPeople()-1);
        this.ui.getPersistenceService().storeEmergency(realEmergency);
        
        this.sendWaterLevelDecreased();

        //refresh the number
        this.ui.addRenderCommand(new Command() {

            @Override
            public void execute() {
                if (emergency != null){
                    emergency.setAnimation(AnimationFactory.getEmergencyAnimation(realEmergency.getType(), realEmergency.getNroOfPeople()));
                }
            }
        });
        
        //If there is no more fire, send a message and remove the emergency from the ui
        if (realEmergency.getNroOfPeople() == 0){
            this.setHideEmergency(true);
            try {
                MessageFactory.sendMessage(new FireExtinctedMessage(emergencyId, new Date()));
            } catch (HornetQException ex) {
                Logger.getLogger(ParticularEmergencyRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
