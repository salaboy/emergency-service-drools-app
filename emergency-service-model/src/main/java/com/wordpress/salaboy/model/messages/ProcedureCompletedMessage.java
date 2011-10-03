/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.Date;

/**
 * Message that indicates the end of a procedure
 * @author esteban
 */
public class ProcedureCompletedMessage implements Serializable, EmergencyInterchangeMessage {

    private String emergencyId;
    private String procedureId;
    private Date time;

    public ProcedureCompletedMessage(String emergencyId, String procedureId, Date time) {
        this.emergencyId = emergencyId;
        this.procedureId = procedureId;
        this.time = time;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ProcedureEndsMessage{" + "emergencyId=" + emergencyId + ", procedureId=" + procedureId + ", time=" + time + '}';
    }
    
}
