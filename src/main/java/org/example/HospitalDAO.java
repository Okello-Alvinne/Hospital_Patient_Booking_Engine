package org.example;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import java.sql.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
public class HospitalDAO {
    private final HikariDataSource dataSource;
    public HospitalDAO(){
        HikariConfig config = new HikariConfig();
        //credentials
        config.setUsername("postgres");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/FirstDB");
        config.setPassword("PASSWORD GOES HERE");
        //perfomance setups
        config.setConnectionTimeout(30000);
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts",true);
        config.addDataSourceProperty("prepStmtCacheSize","250");
        this.dataSource=new HikariDataSource(config);

    }

    public int registerPatient(Patient patient){
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth) VALUES (?,?,?)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setObject(3,patient.getDateOfBirth());

            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected>0){
                try (ResultSet rs = pstmt.getGeneratedKeys()){
                    if(rs.next()){
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }
    public boolean isDoctorBooked(String DoctorName, LocalDateTime dateTime){
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_name = ? AND appointment_date = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1,DoctorName);
            pstmt.setObject(2,dateTime);
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1) > 0;
                }
            }
        }catch (SQLException e){
            System.out.println("Error occured while booking");
        }
        return false;
    }

    public List<Appointments> getAllAppointments(){
        List<Appointments> appointments = new ArrayList<>();
        String sql = """
                SELECT a.appointment_id, a.patient_id, (p.first_name ||' ' || p.last_name) AS full_name,
                a.doctor_name,a.appointment_date,a.reason_for_visit
                FROM appointments a
                INNER JOIN patients p ON a.patient_id = p.patient_id; 
                """;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()){
            while(rs.next()){
               Appointments appt = new Appointments(
                       rs.getInt("appointment_id"),
                       rs.getInt("patient_id"),
                       rs.getString("full_name"),
                       rs.getString("doctor_name"),
                       rs.getObject("appointment_date", LocalDateTime.class),
                       rs.getString("reason_for_visit")
               ) ;
               appointments.add(appt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public List<Patient> getPatientRecords(){
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT patient_id, first_name , last_name, date_of_birth FROM patients;";

        try(Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()){
            while(rs.next()){
                Patient pts = new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getObject("date_of_birth", LocalDate.class)
                );
                patients.add(pts);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return patients;
    }
    public boolean registerAppointments(Appointments appt){
        String sql = "INSERT INTO appointments (patient_id,doctor_name,appointment_date,reason_for_visit) VALUES (?,?,?,?)";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1,appt.getPatientId());
            pstmt.setString(2,appt.getDoctorName());
            pstmt.setObject(3,appt.getAppointmentDate());
            pstmt.setString(4, appt.getReasonForVisit());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected>0;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public void shutDown(){
        if(this.dataSource!=null){
            this.dataSource.close();
        }
    }
}
