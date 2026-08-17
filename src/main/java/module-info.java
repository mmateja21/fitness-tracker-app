module com.example.fitnesstrackingapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.fitnesstrackingapp.controller to javafx.fxml;
    exports com.example.fitnesstrackingapp;
}