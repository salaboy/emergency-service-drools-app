package org.plugtree.training.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class Ambulance implements Serializable {

    private Long id;
    private String description;
    private Medic medicOnBoard;
    private List<MedicalKit> kits;
    private Date departureTime;
    private Date patientPickedUpTime;
    private float positionX;
    private float positionY;
    public static AtomicLong incrementalId = new AtomicLong();

    public Ambulance() {
        this.id = Ambulance.incrementalId.getAndIncrement();
    }

    public Ambulance(String description, Date departureTime) {
        this.id = Ambulance.incrementalId.getAndIncrement();
        this.description = description;
        this.departureTime = departureTime;
    }

    public Ambulance(String description) {
        this.id = Ambulance.incrementalId.getAndIncrement();
        this.description = description;
    }
    
    
    
    
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<MedicalKit> getKits() {
        return kits;
    }

    public void setKits(List<MedicalKit> kits) {
        this.kits = kits;
    }

    public Medic getMedicOnBoard() {
        return medicOnBoard;
    }

    public void setMedicOnBoard(Medic medicOnBoard) {
        this.medicOnBoard = medicOnBoard;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Ambulance{" + "id=" + id + ", description=" + description + ", medicOnBoard=" + medicOnBoard + ", kits=" + kits + ", departureTime=" + departureTime + ", patientPickedUpTime=" + patientPickedUpTime + ", positionX=" + positionX + ", positionY=" + positionY + '}';
    }
    
    
    
}
