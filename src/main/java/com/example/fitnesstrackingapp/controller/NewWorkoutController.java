package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.model.WorkoutSession;
import com.example.fitnesstrackingapp.network.FitnessClient;
import com.example.fitnesstrackingapp.network.Response;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Upravlja ekranom za evidentiranje novog treninga.
 */
public class NewWorkoutController {

    private static final int DEFAULT_USER_ID = 1;

    @FXML
    private ComboBox<WorkoutPlan> planComboBox;

    @FXML
    private DatePicker workoutDatePicker;

    @FXML
    private TextField durationField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label statusLabel;

    private final FitnessClient fitnessClient =
            new FitnessClient();

    private final ObservableList<WorkoutPlan> plans =
            FXCollections.observableArrayList();

    /**
     * Podešava formu i učitava dostupne planove.
     */
    @FXML
    private void initialize() {
        workoutDatePicker.setValue(LocalDate.now());

        configurePlanComboBox();
        loadPlans();
    }

    /**
     * Određuje kako se plan prikazuje u ComboBox kontroli.
     */
    private void configurePlanComboBox() {
        planComboBox.setItems(plans);
        planComboBox.setPromptText(
                "Bez unapred definisanog plana"
        );

        planComboBox.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(WorkoutPlan plan) {
                        return plan == null
                                ? ""
                                : plan.getName();
                    }

                    @Override
                    public WorkoutPlan fromString(String text) {
                        return null;
                    }
                }
        );
    }

    /**
     * Učitava planove sa servera.
     */
    private void loadPlans() {
        try {
            Response<?> response =
                    fitnessClient.getAllPlans();

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            plans.clear();

            if (response.getData() instanceof Iterable<?> items) {
                for (Object item : items) {
                    if (item instanceof WorkoutPlan plan) {
                        plans.add(plan);
                    }
                }
            }

            showSuccess(
                    "Planovi su učitani. Možete evidentirati trening."
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Server nije dostupan. Pokrenite FitnessServer."
            );
        }
    }

    /**
     * Evidentira novi održani trening.
     */
    @FXML
    private void handleCreateSession() {
        int durationMinutes;

        try {
            durationMinutes = Integer.parseInt(
                    durationField.getText().trim()
            );
        } catch (NumberFormatException exception) {
            showError(
                    "Trajanje mora biti ceo broj minuta."
            );
            return;
        }

        WorkoutPlan selectedPlan =
                planComboBox.getValue();

        Integer planId = selectedPlan == null
                ? null
                : selectedPlan.getId();

        WorkoutSession session = new WorkoutSession(
                DEFAULT_USER_ID,
                planId,
                workoutDatePicker.getValue(),
                durationMinutes,
                notesArea.getText()
        );

        try {
            Response<?> response =
                    fitnessClient.createSession(session);

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData()
                    instanceof WorkoutSession savedSession) {

                clearForm();

                showSuccess(
                        "Trening je evidentiran. ID treninga: "
                                + savedSession.getId()
                );
            } else {
                showError(
                        "Server nije vratio sačuvani trening."
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Evidentiranje nije uspelo jer server nije dostupan."
            );
        }
    }

    /**
     * Vraća formu na početne vrednosti.
     */
    @FXML
    private void clearForm() {
        planComboBox.getSelectionModel().clearSelection();
        workoutDatePicker.setValue(LocalDate.now());
        durationField.clear();
        notesArea.clear();
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText(message);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }
}