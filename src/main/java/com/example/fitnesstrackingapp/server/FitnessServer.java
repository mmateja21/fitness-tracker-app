package com.example.fitnesstrackingapp.server;

import com.example.fitnesstrackingapp.util.DatabaseManager;
import javafx.scene.chart.PieChart;

import java.io.IOException;
import java.sql.SQLException;

//serverski deo aplikacije
public class FitnessServer {

    // Pokreće server i inicijalizuje bazu podataka

    static void main() {
        try{
            DatabaseManager.initializeDatabase();
            System.out.println("Baza je uspesno pokrenuta");
            System.out.println("Server je spreman");
    } catch (SQLException | IOException exception){
            System.err.println(
                    "Pokretanje servera neuspesno: "+ exception.getMessage()
            );
            exception.printStackTrace();
        }
    }
}