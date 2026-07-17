package org.example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.TextArea;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class AppApp extends Application {
    private HospitalDAO dao;
    @Override
    public void init(){
        this.dao=new HospitalDAO();
    }
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("🏥 Okelo's Hospital Management System V1");
        BorderPane root = new BorderPane();
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.getStyleClass().add("sidebar");
        Label titleLabel = new Label("MANAGEMENT");
        titleLabel.getStyleClass().add("sidebar-title");

        Button btnViewPatients = new Button("👥 Patient Database");
        Button btnRegisterPatients = new Button("➕ Register Patient");



        Button btnBookApt = new Button("👣 Book Appointment");

        btnViewPatients.setMaxWidth(Double.MAX_VALUE);
        btnRegisterPatients.setMaxWidth(Double.MAX_VALUE);
        btnBookApt.setMaxWidth(Double.MAX_VALUE);
        btnViewPatients.getStyleClass().add("sidebar-button");
        btnRegisterPatients.getStyleClass().add("sidebar-button");
        btnBookApt.getStyleClass().add("sidebar-button");

        sidebar.getChildren().addAll(titleLabel,btnViewPatients,btnRegisterPatients,btnBookApt);
        root.setLeft(sidebar);

        VBox centreWorkspace = new VBox(20);
        centreWorkspace.setPadding(new Insets(30));
        centreWorkspace.setAlignment(Pos.CENTER);

        //Listener to swap workspace view when clicked
        btnRegisterPatients.setOnAction(e->{
            centreWorkspace.getChildren().clear();

            GridPane patientForm = createRegisterPatientForm(centreWorkspace);
            centreWorkspace.getChildren().add(patientForm);
        });
        btnViewPatients.setOnAction(e->{
            centreWorkspace.getChildren().clear();

            VBox databaseView = createPatientDatabaseView();
            centreWorkspace.getChildren().add(databaseView);
        });
        btnBookApt.setOnAction(e->{
            centreWorkspace.getChildren().clear();
            GridPane appointmentForm = createBookAppointmentForm(centreWorkspace);
            centreWorkspace.getChildren().add(appointmentForm);
        });
        Label welcomeMessage = new Label("Welome to Okelo's Management");
        welcomeMessage.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        Label subMessage = new Label("Select an action from Navigation sidebar to begin");
        centreWorkspace.getChildren().addAll(welcomeMessage,subMessage);
        root.setCenter(centreWorkspace);

        Scene scene = new Scene(root,900,600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createRegisterPatientForm(VBox centreWorlspace){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        Label lbFormTitle = new Label("➕ Register new Patients.");
        lbFormTitle.getStyleClass().add("form-title");

        Label lbFirstName = new Label("First Name");
        TextField txtFirstName = new TextField();

        Label lbLastName = new Label("Last Name");
        TextField txtLastName = new TextField();

        Label lbDOB = new Label("Date Of Birth");
        DatePicker datePicker = new DatePicker();

        Button btnSubmit = new Button("Create Profile");
        btnSubmit.getStyleClass().add("action-button");

        Label lbStatus = new Label("");

        grid.add(lbFormTitle,0,0,2,1);
        grid.add(lbFirstName,0,1);
        grid.add(txtFirstName,1,1);
        grid.add(lbLastName,0,2);
        grid.add(txtLastName,1,2);
        grid.add(lbDOB,0,3);
        grid.add(datePicker,1,3);
        grid.add(btnSubmit,1,4);
        grid.add(lbStatus,1,5);

        //Hooking part LOL
        btnSubmit.setOnAction(e ->{
            String fName = txtFirstName.getText();
            String lName = txtLastName.getText();
            java.time.LocalDate dob=datePicker.getValue();

            if(fName.isEmpty() || lName.isEmpty() || dob == null){
                lbStatus.setText("❌ Error: All fields are required");
                lbStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            Patient freshPatient = new Patient(fName,lName,dob);
            int assignedID = dao.registerPatient(freshPatient);

            if(assignedID!=-1){
                lbStatus.setText("✅ Profile created. Assigned ID: "+assignedID);
                lbStatus.setStyle("-fx-text-fill: green;");

                txtFirstName.clear();
                txtLastName.clear();
                datePicker.setValue(null);
            }else{
                lbStatus.setText("❌ Database Rejected the Transaction");
                lbStatus.setStyle("-fx-text-fill: red;");
            }
        });
        return grid;
    }
    private VBox createPatientDatabaseView() {
        VBox viewContainer = new VBox(15);
        viewContainer.setPadding(new Insets(10));
        viewContainer.setAlignment(Pos.TOP_LEFT);

        Label lblHeader = new Label("👥 System Patient Database Directory");
        lblHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // 1. Create the TableView component
        TableView<Patient> table = new TableView<>();

        // 2. Define the Table Columns and map them to Patient class field getters
        TableColumn<Patient, Integer> colId = new TableColumn<>("Patient ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("patientId")); // Calls getId()
        colId.setPrefWidth(100);

        TableColumn<Patient, String> colFirstName = new TableColumn<>("First Name");
        colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName")); // Calls getFirstName()
        colFirstName.setPrefWidth(150);

        TableColumn<Patient, String> colLastName = new TableColumn<>("Last Name");
        colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName")); // Calls getLastName()
        colLastName.setPrefWidth(150);

        TableColumn<Patient, java.time.LocalDate> colDob = new TableColumn<>("Date of Birth");
        colDob.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth")); // Calls getDateOfBirth()
        colDob.setPrefWidth(150);

        // Add columns to the table
        table.getColumns().addAll(colId, colFirstName, colLastName, colDob);

        // 3. Fetch data from your existing DAO and wrap it in a JavaFX ObservableList
        java.util.List<Patient> patientList = dao.getPatientRecords();
        ObservableList<Patient> observablePatients = FXCollections.observableArrayList(patientList);
        table.setItems(observablePatients);

        // Make the table expand to fill the visual workspace nicely
        table.setPrefHeight(450);

        viewContainer.getChildren().addAll(lblHeader, table);
        return viewContainer;
    }
    private GridPane createBookAppointmentForm(VBox centerWorkspace) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // 1. Visual Form Components
        Label lblFormTitle = new Label("📅 Schedule New Appointment");
        lblFormTitle.getStyleClass().add("form-title");

        Label lblPatientId = new Label("Patient ID:");
        TextField txtPatientId = new TextField();
        txtPatientId.setPromptText("e.g., 4");

        Label lblDocName = new Label("Doctor Name:");
        TextField txtDocName = new TextField();
        txtDocName.setPromptText("e.g., Dr. Smith Roe");

        Label lblDate = new Label("Appointment Date:");
        DatePicker datePicker = new DatePicker();

        Label lblTime = new Label("Appointment Time (HH:mm):");
        TextField txtTime = new TextField();
        txtTime.setPromptText("e.g., 14:30 (24hr format)");

        Label lblReason = new Label("Reason for Visit:");
        TextArea txtReason = new TextArea();
        txtReason.setPrefRowCount(3);
        txtReason.setPromptText("Brief description of symptoms...");

        Button btnSubmit = new Button("Book Appointment");
        btnSubmit.getStyleClass().add("action-button");

        Label lblStatus = new Label("");

        // 2. Map Components to the Grid
        grid.add(lblFormTitle, 0, 0, 2, 1);
        grid.add(lblPatientId, 0, 1);
        grid.add(txtPatientId, 1, 1);
        grid.add(lblDocName, 0, 2);
        grid.add(txtDocName, 1, 2);
        grid.add(lblDate, 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(lblTime, 0, 4);
        grid.add(txtTime, 1, 4);
        grid.add(lblReason, 0, 5);
        grid.add(txtReason, 1, 5);
        grid.add(btnSubmit, 1, 6);
        grid.add(lblStatus, 1, 7);

        // 3. Action Event Hook — Harnessing your validation engine
        btnSubmit.setOnAction(e -> {
            String patientIdStr = txtPatientId.getText().trim();
            String docName = txtDocName.getText().trim();
            LocalDate localDate = datePicker.getValue();
            String timeStr = txtTime.getText().trim();
            String reason = txtReason.getText().trim();

            // Guard A: Check empty fields
            if (patientIdStr.isEmpty() || docName.isEmpty() || localDate == null || timeStr.isEmpty() || reason.isEmpty()) {
                lblStatus.setText("❌ Error: All fields must be filled!");
                lblStatus.setStyle("-fx-text-fill: red;");
                return;
            }

            try {
                int patientId = Integer.parseInt(patientIdStr);

                // Stitch date and time together exactly like the console version
                String combinedDateTimeStr = localDate.toString() + "T" + timeStr;
                LocalDateTime appointmentDateTime = LocalDateTime.parse(combinedDateTimeStr);

                // Guard B: Past Date Check
                if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                    lblStatus.setText("🛑 Error: Cannot schedule appointments in the past!");
                    lblStatus.setStyle("-fx-text-fill: red;");
                    return;
                }

                // Guard C: Doctor Double-Booking Conflict Check
                if (dao.isDoctorBooked(docName, appointmentDateTime)) {
                    lblStatus.setText("🛑 Conflict: Dr. " + docName + " is already booked at that time!");
                    lblStatus.setStyle("-fx-text-fill: red;");
                    return;
                }

                // If clean, pack into model and execute transaction
                Appointments newAppointment = new Appointments(patientId, docName, appointmentDateTime, reason);
                boolean success = dao.registerAppointments(newAppointment);

                if (success) {
                    lblStatus.setText("✅ Appointment successfully confirmed!");
                    lblStatus.setStyle("-fx-text-fill: green;");

                    // Reset fields
                    txtPatientId.clear();
                    txtDocName.clear();
                    datePicker.setValue(null);
                    txtTime.clear();
                    txtReason.clear();
                } else {
                    lblStatus.setText("❌ Error: Verification failed. Is the Patient ID valid?");
                    lblStatus.setStyle("-fx-text-fill: red;");
                }

            } catch (NumberFormatException ex) {
                lblStatus.setText("❌ Error: Patient ID must be a valid number.");
                lblStatus.setStyle("-fx-text-fill: red;");
            } catch (java.time.format.DateTimeParseException ex) {
                lblStatus.setText("❌ Error: Time format must be HH:mm (e.g., 14:30).");
                lblStatus.setStyle("-fx-text-fill: red;");
            }
        });

        return grid;
    }

    public static void main(String[] args){
        launch(args);
    }
}
