package com.wordpress.salaboy.model;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class Patient implements Serializable {

    private String id;
    private int age;
    private String gender;

    public Patient() {
    }

    public Patient(int age, String gender) {

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Patient{" + "id=" + id + ", age=" + age + ", gender=" + gender + '}';
    }

}
