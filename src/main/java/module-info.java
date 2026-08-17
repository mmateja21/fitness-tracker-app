module com.example.fitnesstrackingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.fitnesstrackingapp.controller to javafx.fxml;
    exports com.example.fitnesstrackingapp;
}