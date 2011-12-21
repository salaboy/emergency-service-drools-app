/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class BedRequest implements Serializable{
    private String id;
    private long timestamp;
    private String requestingEntity;
    private int patientAge;
    private String patientName;
    private String patientGender;
    private String patientStatus;
    private int assignedBed;
    private long processInstanceId;
    
    public BedRequest(String id) {
        this.id = id;
    }

    public BedRequest(String id, long timestamp, String requestingEntity, int patientAge, String patientName, String patientGender, String patientStatus) {
        this.id = id;
        this.timestamp = timestamp;
        this.requestingEntity = requestingEntity;
        this.patientAge = patientAge;
        this.patientName = patientName;
        this.patientGender = patientGender;
        this.patientStatus = patientStatus;
    }
    
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    public String getRequestingEntity() {
        return requestingEntity;
    }

    public void setRequestingEntity(String requestingEntity) {
        this.requestingEntity = requestingEntity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAssignedBed() {
        return assignedBed;
    }

    public void setAssignedBed(int assignedBed) {
        this.assignedBed = assignedBed;
    }

    public long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String toString() {
        return "BedRequest{" + "id=" + id + ", timestamp=" + timestamp + ", requestingEntity=" + requestingEntity + ", patientAge=" + patientAge + ", patientName=" + patientName + ", patientGender=" + patientGender + ", patientStatus=" + patientStatus + ", assignedBed=" + assignedBed + ", processInstanceId=" + processInstanceId + '}';
    }
    
    
    
    
    
    
}
