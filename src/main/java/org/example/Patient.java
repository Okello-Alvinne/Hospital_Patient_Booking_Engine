package org.example;

import java.time.LocalDate;

public class Patient {
    private String firstName;
    private String lastName;
    private int patientId;
    private LocalDate dateOfBirth;

    public Patient(int patientId,String firstName,String lastName, LocalDate dateOfBirth){
        this.firstName=firstName;
        this.lastName=lastName;
        this.patientId=patientId;
        this.dateOfBirth=dateOfBirth;
    }
    public Patient(String firstName, String lastName, LocalDate dateOfBirth){
        this.firstName=firstName;
        this.lastName=lastName;
        this.dateOfBirth=dateOfBirth;
    }


    public String getFirstName() {
        return firstName;
    }
    public String getLastName(){return lastName;}

    public int getPatientId() {
        return patientId;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}
