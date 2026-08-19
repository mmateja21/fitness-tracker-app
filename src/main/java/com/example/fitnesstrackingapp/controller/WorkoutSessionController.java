package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.model.WorkoutSession;
import com.example.fitnesstrackingapp.network.FitnessClient;
import com.example.fitnesstrackingapp.network.Response;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import com.example.fitnesstrackingapp.FitnessApplication;
import com.example.fitnesstrackingapp.model.WorkoutSummary;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Upravlja ekranom istorije održanih treninga.
 */
public class WorkoutSessionController {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @FXML
    private TableView<WorkoutSession> sessionTable;

    @FXML
    private TableColumn<WorkoutSession, Integer> idColumn;

    @FXML
    private TableColumn<WorkoutSession, String> dateColumn;

    @FXML
    private TableColumn<WorkoutSession, String> planColumn;

    @FXML
    private TableColumn<WorkoutSession, Integer> durationColumn;

    @FXML
    private TableColumn<WorkoutSession, String> notesColumn;

    @FXML
    private Label statusLabel;
    @FXML
    private Label totalWorkoutsLabel;

    @FXML
    private Label totalDurationLabel;

    @FXML
    private Label averageDurationLabel;

    private final FitnessClient fitnessClient =
            new FitnessClient();

    private final ObservableList<WorkoutSession> sessions =
            FXCollections.observableArrayList();

    /**
     * Podešava tabelu i učitava treninge sa servera.
     */
    @FXML
    private void initialize() {
        configureTableColumns();
        sessionTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );
        sessionTable.setItems(sessions);
        loadSessions();
    }

    /**
     * Povezuje kolone tabele sa atributima modela.
     */
    private void configureTableColumns() {
        idColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getId()
                ).asObject()
        );

        dateColumn.setCellValueFactory(cellData -> {
            WorkoutSession session = cellData.getValue();

            String date = session.getWorkoutDate() == null
                    ? ""
                    : session.getWorkoutDate()
                    .format(DATE_FORMATTER);

            return new ReadOnlyStringWrapper(date);
        });

        planColumn.setCellValueFactory(cellData -> {
            Integer planId =
                    cellData.getValue().getPlanId();

            String planText = planId == null
                    ? "Bez plana"
                    : "Plan #" + planId;

            return new ReadOnlyStringWrapper(planText);
        });

        durationColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue()
                                .getDurationMinutes()
                ).asObject()
        );

        notesColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getNotes()
                )
        );
    }

    /**
     * Učitava istoriju treninga sa servera.
     */
    @FXML
    private void loadSessions() {
        try {
            Response<?> response =
                    fitnessClient.getAllSessions();

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            sessions.clear();

            if (response.getData() instanceof Iterable<?> items) {
                for (Object item : items) {
                    if (item instanceof WorkoutSession session) {
                        sessions.add(session);
                    }
                }
            }

            showSuccess(
                    "Učitano treninga: " + sessions.size()
            );
            loadSummary();

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Server nije dostupan. Pokrenite FitnessServer."
            );
        }
    }
    /**
     * Učitava zbirne podatke o treninzima sa servera.
     */
    private void loadSummary() {
        try {
            Response<?> response =
                    fitnessClient.getWorkoutSummary();

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData()
                    instanceof WorkoutSummary summary) {

                totalWorkoutsLabel.setText(
                        "Broj treninga: "
                                + summary.getTotalWorkouts()
                );

                totalDurationLabel.setText(
                        "Ukupno trajanje: "
                                + summary.getTotalDurationMinutes()
                                + " min"
                );

                averageDurationLabel.setText(
                        String.format(
                                "Prosečno trajanje: %.1f min",
                                summary.getAverageDurationMinutes()
                        )
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Pregled treninga nije moguće učitati."
            );
        }
    }

@FXML
    private void handleManageExercises() {
        WorkoutSession selectedSession =
                sessionTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedSession == null) {
            showError(
                    "Prvo izaberite trening iz tabele."
            );
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    FitnessApplication.class.getResource(
                            "session-details-view.fxml"
                    )
            );

            Parent root = loader.load();

            SessionDetailsController controller =
                    loader.getController();

            controller.setSession(selectedSession);

            Stage stage = new Stage();

            stage.setTitle(
                    "Odrađene vežbe - "
                            + selectedSession.getWorkoutDate()
                            .format(DATE_FORMATTER)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.initOwner(
                    sessionTable.getScene().getWindow()
            );

            stage.setScene(
                    new Scene(root, 950, 600)
            );

            stage.setMinWidth(850);
            stage.setMinHeight(550);
            stage.showAndWait();

        } catch (IOException exception) {
            showError(
                    "Prozor sa odrađenim vežbama nije moguće otvoriti."
            );
            exception.printStackTrace();
        }
    }
    @FXML
    private void handleDelete() {
        WorkoutSession selectedSession =
                sessionTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedSession == null) {
            showError(
                    "Prvo izaberite trening iz tabele."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Potvrda brisanja");
        confirmation.setHeaderText(
                "Brisanje treninga od "
                        + selectedSession.getWorkoutDate()
                        .format(DATE_FORMATTER)
        );
        confirmation.setContentText(
                "Biće obrisane i sve evidentirane vežbe ovog treninga."
        );

        Optional<ButtonType> selectedButton =
                confirmation.showAndWait();

        if (selectedButton.isEmpty()
                || selectedButton.get() != ButtonType.OK) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.deleteSession(
                            selectedSession.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            sessions.remove(selectedSession);
            loadSummary();


            showSuccess(
                    "Trening je uspešno obrisan."
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Brisanje nije uspelo jer server nije dostupan."
            );
        }
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