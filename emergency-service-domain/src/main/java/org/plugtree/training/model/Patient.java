package org.plugtree.training.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author salaboy
 */
public class Patient {

    public static final int DEFAULT_PATIENT_AGE = 30;
    private Long id;
    private int age;
    private String name;
    private String gender;
    private List<Alert> alerts;
    public static AtomicLong incrementalId = new AtomicLong();
    
    public Patient() {
        this.age = DEFAULT_PATIENT_AGE;
        
    }

    public Patient(Long id, String name, int age, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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
