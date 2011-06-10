package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class Ambulance implements Vehicle{

    private Long id;
    
    private Doctor doctorOnBoard;
    private List<MedicalKit> kits;
    private Date departureTime;
    private Date patientPickedUpTime;
    private float positionX;
    private float positionY;
    private String name;
    public static AtomicLong incrementalId = new AtomicLong();
    

	public Ambulance() {
		// Needed for serialization
	}

    public Ambulance(String name, Date departureTime) {
        this.id = Ambulance.incrementalId.getAndIncrement();
        this.name = name;
        this.departureTime = departureTime;
    }

    public Ambulance(String name) {
        this.id = Ambulance.incrementalId.getAndIncrement();
        this.name = name;
    }
    
    
    
    
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public List<MedicalKit> getKits() {
        return kits;
    }

    public void setKits(List<MedicalKit> kits) {
        this.kits = kits;
    }

    public Doctor getDoctorOnBoard() {
        return doctorOnBoard;
    }

    public void setDoctorOnBoard(Doctor doctorOnBoard) {
        this.doctorOnBoard = doctorOnBoard;
    }

    public void addKit(MedicalKit kit) {
        if (kits == null) {
            kits = new ArrayList<MedicalKit>();
        }
        kits.add(kit);
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setPatientPickedUpTime(Date patientPickedUpTime) {
        this.patientPickedUpTime = patientPickedUpTime;
    }

    public Date getPatientPickedUpTime() {
        return patientPickedUpTime;
    }

    

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    @Override
    public String toString() {
        return "Ambulance{" + "id=" + id + ", name=" + name + '}';
    }

   

    @Override
    public String getName() {
        return this.name;
    }

	public static AtomicLong getIncrementalId() {
		return incrementalId;
	}

	public static void setIncrementalId(AtomicLong incrementalId) {
		Ambulance.incrementalId = incrementalId;
	}

	public void setName(String name) {
		this.name = name;
	}

    
    
}
