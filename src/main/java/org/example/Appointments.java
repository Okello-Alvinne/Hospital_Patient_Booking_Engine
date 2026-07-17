package org.example;

import java.time.LocalDateTime;

public class Appointments {
    private int appointmentId;
    private int patientId;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private String reasonForVisit;
    private String patientFullName;

    //For scheduling appointments
    public Appointments(int patientId,String doctorName,LocalDateTime appointmentDate,String reasonForVisit){
        this.patientId=patientId;
        this.doctorName=doctorName;
        this.appointmentDate=appointmentDate;
        this.reasonForVisit=reasonForVisit;
    }

    public Appointments(int appointmentId,int patientId,String patientFullName,String doctorName,LocalDateTime appointmentDate,String reasonForVisit){
        this.appointmentId=appointmentId;
        this.patientId=patientId;
        this.doctorName=doctorName;
        this.appointmentDate=appointmentDate;
        this.reasonForVisit=reasonForVisit;
        this.patientFullName=patientFullName;
    }//For reading data with the full patient name

    public int getPatientId() {
        return patientId;
    }
    public int getAppointmentId() {
        return appointmentId;
    }
    public String getDoctorName() {
        return doctorName;
    }
    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }
    public String getReasonForVisit() {
        return reasonForVisit;
    }
    public String getPatientFullName() {return patientFullName;}
}
