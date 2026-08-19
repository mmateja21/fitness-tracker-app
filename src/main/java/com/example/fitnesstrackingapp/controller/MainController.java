package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.FitnessApplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Upravlja glavnom navigacijom aplikacije.
 */
public class MainController {

    /** Centralni prostor u kojem se prikazuju ekrani. */
    @FXML
    private StackPane contentPane;

    /**
     * Prikazuje ekran vežbi pri pokretanju aplikacije.
     */
    @FXML
    private void initialize() {
        showExercises();
    }

    /**
     * Prikazuje ekran za upravljanje vežbama.
     */
    @FXML
    private void showExercises() {
        loadView("exercise-view.fxml");
    }

    /**
     * Prikazuje ekran za upravljanje planovima.
     */
    @FXML
    private void showPlans() {
        loadView("plan-view.fxml");
    }

    /**
     * Učitava FXML ekran u centralni deo aplikacije.
     *
     * @param resourceName naziv FXML resursa
     */
    private void loadView(String resourceName) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    FitnessApplication.class.getResource(
                            resourceName
                    )
            );

            Parent view = loader.load();
            contentPane.getChildren().setAll(view);

        } catch (IOException exception) {
            showLoadError(resourceName, exception);
        }
    }

    /**
     * Prikazuje dijalog ako ekran ne može da se učita.
     *
     * @param resourceName naziv neuspešnog resursa
     * @param exception nastala greška
     */
    private void showLoadError(
            String resourceName,
            IOException exception
    ) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Greška");
        alert.setHeaderText(
                "Ekran nije moguće učitati."
        );
        alert.setContentText(
                resourceName + ": " + exception.getMessage()
        );
        alert.showAndWait();
    }
}