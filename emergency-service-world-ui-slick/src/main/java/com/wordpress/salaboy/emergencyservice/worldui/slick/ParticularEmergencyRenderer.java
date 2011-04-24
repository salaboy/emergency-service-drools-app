/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.emergencyservice.worldui.slick;

import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableAmbulance;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableEmergency;
import com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable.GraphicableFactory;
import com.wordpress.salaboy.model.Ambulance;
import java.util.Iterator;
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
    private GraphicableAmbulance ambulance;

    public ParticularEmergencyRenderer(WorldUI ui, GraphicableEmergency emergency) {
        this.emergency = emergency;
        this.ui = ui;
    }
    
    /**
     * 
     * @param ui 
     */
    @Override
    public void renderPolygon(GameContainer gc, Graphics g){
        g.draw(emergency.getPolygon());
        if (ambulance != null){
            g.draw(ambulance.getPolygon());
        }
    }

    public void renderAnimation(GameContainer gc, Graphics g) {
        g.drawAnimation(emergency.getAnimation(), emergency.getPolygon().getX(), emergency.getPolygon().getY());
        if (ambulance != null){
            g.drawAnimation(ambulance.getAnimation(), ambulance.getPolygon().getX(), ambulance.getPolygon().getY());
        }
    }

    public void addVehicle(Ambulance vehicle) {
        ambulance = GraphicableFactory.newAmbulance(vehicle);
    }

    public void onKeyPressed(int code, char key) {
        if (Input.KEY_ESCAPE == code) {
            this.ui.goToGlobalMap();
        } else if (Input.KEY_F1 == code) {
            addMockAmbulance();
        } 
    }

    public void onClick(int button, int x, int y, int count) {
    }
    
    private void addMockAmbulance() {

        Ambulance ambulance = new Ambulance("Mock Ambulance");
        ambulance.setId(System.currentTimeMillis());
        ambulance.setPositionX(emergency.getCallX());
        ambulance.setPositionY(emergency.getCallY());

        this.ui.assignVehicleToEmergency(this.emergency.getCallId(), ambulance);
    }

}
