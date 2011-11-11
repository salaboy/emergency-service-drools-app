/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.buildings.Hospital;
import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class HospitalSelectedMessage implements Serializable, EmergencyInterchangeMessage{
    private Hospital hospital;
    private String callId;

    public HospitalSelectedMessage(String callId, Hospital hospital) {
        this.hospital = hospital;
        this.callId = callId;
    }

    public String getEmergencyId() {
        return callId;
    }
    
    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    @Override
    public String toString() {
        return "HospitalSelectedMessage{" + "hospital=" + hospital + ", callId=" + callId + '}';
    }
    
    
    
    
}
