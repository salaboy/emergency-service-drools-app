package com.wordpress.salaboy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class Patient implements Serializable {

    
    private Long id;
    private int age;
    private String gender;
    private List<Alert> alerts;
    public static AtomicLong incrementalId = new AtomicLong();
    
    public Patient() {
        
    }

    public Patient( int age, String gender) {
        this.id = Patient.incrementalId.getAndIncrement();
        this.age = age;
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public List<Alert> getAlerts() {
        if (alerts == null) {
            alerts = new ArrayList<Alert>();
        }
        return alerts;
    }

    public void addAlert(Alert alert) {
        getAlerts().add(alert);
    }

    
}

