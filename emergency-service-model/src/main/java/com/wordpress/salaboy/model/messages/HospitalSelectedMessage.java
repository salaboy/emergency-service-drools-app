/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import com.wordpress.salaboy.model.Hospital;
import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class HospitalSelectedMessage implements Serializable, EmergencyInterchangeMessage{
    private Hospital hospital;
    private Long callId;

    public HospitalSelectedMessage(Long callId, Hospital hospital) {
        this.hospital = hospital;
        this.callId = callId;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
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
