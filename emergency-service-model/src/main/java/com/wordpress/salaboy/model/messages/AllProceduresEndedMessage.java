/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.model.messages;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Message that indicates the end of a all the procedures in an emergency
 * @author esteban
 */
public class AllProceduresEndedMessage implements Serializable, EmergencyInterchangeMessage {

    private String emergencyId;
    private List<String> endedProcedures;
    private Date time;

    public AllProceduresEndedMessage(String emergencyId, List<String> endedProcedures, Date time) {
        this.emergencyId = emergencyId;
        this.endedProcedures = endedProcedures;
        this.time = time;
    }

    public String getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(String emergencyId) {
        this.emergencyId = emergencyId;
    }

    public List<String> getEndedProcedures() {
        return endedProcedures;
    }

    public void setEndedProcedures(List<String> endedProcedures) {
        this.endedProcedures = endedProcedures;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AllProceduresEndedMessage{" + "emergencyId=" + emergencyId + ", endedProcedures=" + endedProcedures + ", time=" + time + '}';
    }
}
