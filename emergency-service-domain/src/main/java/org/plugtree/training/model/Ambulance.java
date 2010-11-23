package org.plugtree.training.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Ambulance {

	private String id;
	private Medic medicOnBoard;
	private List<MedicalKit> kits;
	private Date departureTime;
	private Date patientPickedUpTime;

	public Ambulance() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
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
	public void addKit(MedicalKit kit){
		if(kits == null){
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

}
