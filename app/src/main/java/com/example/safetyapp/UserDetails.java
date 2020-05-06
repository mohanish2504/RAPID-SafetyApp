package com.example.safetyapp;

import java.util.Map;

public class UserDetails {
    private String FirstName,LastName,DOB,Gender,City;
    private Map userdetails;

    public UserDetails(Map userdetails){
            this.userdetails = userdetails;
            FirstName = (String) userdetails.get("FirstName");
            LastName = (String) userdetails.get("LastName");
            DOB = (String) userdetails.get("DOB");
            Gender = (String) userdetails.get("Gender");
            City = (String) userdetails.get("City");
    }

    public UserDetails(String firstName, String lastName, String DOB, String gender, String city) {
        FirstName = firstName;
        LastName = lastName;
        this.DOB = DOB;
        Gender = gender;
        City = city;
    }

    public UserDetails() {

    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}
