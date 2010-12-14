/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.plugtree.training.model;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import org.newdawn.slick.Animation;
import org.newdawn.slick.geom.Polygon;

/**
 *
 * @author salaboy
 */
public class Emergency implements Serializable, Graphicable{

    
    
    public enum EmergencyType {

        FIRE, CAR_CRASH, HEART_ATTACK, ASSAULT
    };
    private Long id;
    private Call call;
    private Patient patient;
    private Ambulance ambulance;
    private Location location;
    private EmergencyType type;
    private Date approximateTime;
    private Hospital selectedHospital;
    private boolean processed;
    public static AtomicLong incrementalId = new AtomicLong();
    
    //Graphicable
    private transient Animation animation;
    private transient Polygon polygon;

    public Emergency() {
        this.id = Emergency.incrementalId.getAndIncrement();
        this.patient = new Patient();
        this.location = new Location();
        this.selectedHospital = new Hospital();
        
    }
    
    public Emergency(Animation animation, Polygon polygon){
        this();
        this.animation = animation;
        this.polygon = polygon;
    }

    public Ambulance getAmbulance() {
        return ambulance;
    }

    public void setAmbulance(Ambulance ambulance) {
        this.ambulance = ambulance;
    }

    public Date getApproximateTime() {
        return approximateTime;
    }

    public void setApproximateTime(Date approximateTime) {
        this.approximateTime = approximateTime;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Hospital getSelectedHospital() {
        return selectedHospital;
    }

    public void setSelectedHospital(Hospital selectedHospital) {
        this.selectedHospital = selectedHospital;
    }

    public EmergencyType getType() {
        return type;
    }

    public void setType(EmergencyType type) {
        this.type = type;
    }
    public void setType(String type){
        this.type = EmergencyType.valueOf(type);
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isProcessed() {
        return processed;
    }
    
    public Animation getAnimation() {
        return this.animation;
    }

    public Polygon getPolygon() {
        return this.polygon;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    @Override
    public String toString() {
        return "Emergency{" + "id=" + id + ", call=" + call + ", patient=" + patient + ", ambulance=" + ambulance + ", location=" + location + ", type=" + type + ", approximateTime=" + approximateTime + ", selectedHospital=" + selectedHospital + ", processed=" + processed + '}';
    }
    
}
