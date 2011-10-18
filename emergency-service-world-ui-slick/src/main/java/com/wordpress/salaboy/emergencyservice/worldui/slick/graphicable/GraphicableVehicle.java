/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.emergencyservice.worldui.slick.graphicable;

/**
 *
 * @author esteban
 */
public abstract class GraphicableVehicle implements Graphicable {
    private boolean isCollidingWithACorner;
    private boolean isCollidingWithAHospital;

    public boolean isIsCollidingWithACorner() {
        return isCollidingWithACorner;
    }

    public void setIsCollidingWithACorner(boolean isCollidingWithACorner) {
        this.isCollidingWithACorner = isCollidingWithACorner;
    }

    public boolean isIsCollidingWithAHospital() {
        return isCollidingWithAHospital;
    }

    public void setIsCollidingWithAHospital(boolean isCollidingWithAHospital) {
        this.isCollidingWithAHospital = isCollidingWithAHospital;
    }

    
}
