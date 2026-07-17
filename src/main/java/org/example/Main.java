package org.example;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        HospitalDAO dao = new HospitalDAO();
        Scanner sc = new Scanner(System.in);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("📡 HOSPITAL MANAGEMENT SYSTEM ONLINE");

        while(true){
            try{
            System.out.println("\n---MAIN MENU---");
            System.out.println("1.View Master Appointment Schedule.");
            System.out.println("2. Register New Patient.");
            System.out.println("3. View Patient Database.");
            System.out.println("4. Register an Appointment");
            System.out.println("5. Exit System.");

            System.out.println("\n Choose an option: ");

            int choice = sc.nextInt();

                if (choice == 1) {
                    sc.nextLine();
                    System.out.println("\nFetching Master Appointment Calendar...");
                    List<Appointments> schedule = dao.getAllAppointments();
                    if (schedule.isEmpty()) {
                        System.out.println("No appointments found in the System DataBase");
                    } else {
                        System.out.printf("%-15s | %-20s | %-15s | %-20s | %-25s \n",
                                "APPOINTMENT ID", "PATIENT NAME", "DOCTOR", "DATE/TIME", "REASON");
                        System.out.println("--------------------------------------------------------------------------------");
                        for (Appointments apt : schedule) {
                            System.out.printf("%-15d | %-20s | %-15s | %-20s | %-25s\n",
                                    apt.getAppointmentId(),
                                    apt.getPatientFullName(),
                                    apt.getDoctorName(),
                                    apt.getAppointmentDate().toString(),
                                    apt.getReasonForVisit());

                        }

                    }

                } else if (choice == 2) {
                    System.out.println("\n👤 REGISTER NEW PATIENT PROFILE");
                    sc.nextLine();
                    System.out.print("Enter the First Name: ");
                    String firstName = sc.nextLine();
                    System.out.print("Enter the Last Name: ");
                    String lastName = sc.nextLine();
                    System.out.print("Enter Date of Birth (yyyy-MM-dd)");
                    String dobInput = sc.nextLine();

                    try {
                        LocalDate dob = LocalDate.parse(dobInput, dateFormatter);
                        Patient newPatient = new Patient(firstName, lastName, dob);
                        int success = dao.registerPatient(newPatient);
                        if (success!=-1) {
                            System.out.println("✅Patient Profile successfully created");
                            System.out.println("Assigned Patient ID: "+success+" (Save it for booking appointments)");
                        } else {
                            System.out.println("🏗️Database dropped the request Check application status logs");
                        }
                    } catch (Exception e) {
                        System.out.println("❌Format error. Ensure date matches the required format.");
                    }

                } else if (choice == 3) {
                    sc.nextLine();
                    List<Patient> records = dao.getPatientRecords();
                    if (records.isEmpty()) {
                        System.out.println("Unable to retrieve Patients Data.");
                    } else {
                        System.out.println("-HOSPITAL PATIENT'S RECORDS-");
                        System.out.println("--------------------------------------------------------------------");
                        System.out.printf("%-15s | %-20s | %-20s | %-15s ", "PATIENT ID", "FIRST NAME", "LAST NAME", "DATE OF BIRTH");
                        for (Patient pts : records) {
                            System.out.printf("\n %-15d | %-20s | %-20s | %-15s ",
                                    pts.getPatientId(),
                                    pts.getFirstName(),
                                    pts.getLastName(),
                                    pts.getDateOfBirth());
                        }
                    }
                } else if (choice==4) {
                    sc.nextLine();
                    System.out.println("👤Register an Appointment");
                    System.out.print("Enter Patient ID:");
                    int Id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Doctor name:");
                    String doc =sc.nextLine();
                    System.out.print(" Enter appointment Date (yyyy-MM-dd): ");
                    String doa = sc.nextLine();
                    System.out.println("Enter appointment time(HH:MM 24 hr format)");
                    String time = sc.nextLine();
                    System.out.println("Enter reason for visit: ");
                    String reason = sc.nextLine();

                    try{
                        String combinedDateTime = doa+"T"+time;
                        LocalDateTime appointmentDateTime = LocalDateTime.parse(combinedDateTime);
                        if (appointmentDateTime.isBefore(LocalDateTime.now())){
                            System.out.println("❌Error: Invalid Date");
                            continue;
                        }
                        if(dao.isDoctorBooked(doc,appointmentDateTime)){
                            System.out.println("Scheduling Conflict "+doc+" is already booked");
                            continue;
                        }
                        Appointments newAppointment = new Appointments(Id,doc,appointmentDateTime,reason);
                        boolean executed = dao.registerAppointments(newAppointment);
                        if(executed){
                            System.out.println("✅ Appointment executed successfully");
                        }else{
                            System.out.println("❌ Failed to book an appointment");
                        }
                    } catch (Exception e) {
                        System.out.println("Try Again");
                        continue;
                    }
                } else if (choice == 5) {
                    System.out.println("Exiting system. Goodbye👋");
                    sc.close();
                    dao.shutDown();
                    break;
                } else {
                    System.out.println("Invalid Selection. Retry...");
                }
            }catch (InputMismatchException e){
                System.out.println("Invalid Input. Try again");
                sc.nextLine();
            }
        }
    }
}
