package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.model.SessionExercise;
import com.example.fitnesstrackingapp.model.WorkoutSession;
import com.example.fitnesstrackingapp.network.FitnessClient;
import com.example.fitnesstrackingapp.network.Response;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import java.io.IOException;

/**
 * Upravlja vežbama odrađenim tokom izabranog treninga.
 */
public class SessionDetailsController {

    @FXML
    private Label sessionLabel;

    @FXML
    private TableView<SessionExercise> sessionExerciseTable;

    @FXML
    private TableColumn<SessionExercise, String> exerciseNameColumn;

    @FXML
    private TableColumn<SessionExercise, Integer> setsColumn;

    @FXML
    private TableColumn<SessionExercise, Integer> repsColumn;

    @FXML
    private TableColumn<SessionExercise, Double> weightColumn;

    @FXML
    private TableColumn<SessionExercise, Double> volumeColumn;

    @FXML
    private ComboBox<Exercise> exerciseComboBox;

    @FXML
    private TextField setsField;

    @FXML
    private TextField repsField;

    @FXML
    private TextField weightField;

    @FXML
    private Label totalVolumeLabel;

    @FXML
    private Label statusLabel;

    private final FitnessClient fitnessClient =
            new FitnessClient();

    private final ObservableList<SessionExercise> sessionExercises =
            FXCollections.observableArrayList();

    private final ObservableList<Exercise> availableExercises =
            FXCollections.observableArrayList();

    private WorkoutSession selectedSession;

    /**
     * Podešava kontrole i učitava dostupne vežbe.
     */
    @FXML
    private void initialize() {
        configureTableColumns();
        configureExerciseComboBox();

        sessionExerciseTable.setItems(sessionExercises);
        exerciseComboBox.setItems(availableExercises);

        loadAvailableExercises();
        sessionExerciseTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        populateForm(newValue)
                );
    }

    /**
     * Postavlja trening čije se vežbe prikazuju.
     */
    public void setSession(WorkoutSession session) {
        selectedSession = session;

        sessionLabel.setText(
                "Trening od " + session.getWorkoutDate()
        );

        loadSessionExercises();
    }

    /**
     * Povezuje kolone sa podacima modela.
     */
    private void configureTableColumns() {
        exerciseNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        findExerciseName(
                                cellData.getValue().getExerciseId()
                        )
                )
        );

        setsColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getCompletedSets()
                ).asObject()
        );

        repsColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getCompletedReps()
                ).asObject()
        );

        weightColumn.setCellValueFactory(cellData ->
                new ReadOnlyDoubleWrapper(
                        cellData.getValue().getWeight()
                ).asObject()
        );

        volumeColumn.setCellValueFactory(cellData -> {
            SessionExercise exercise = cellData.getValue();

            double volume =
                    exercise.getCompletedSets()
                            * exercise.getCompletedReps()
                            * exercise.getWeight();

            return new ReadOnlyDoubleWrapper(volume)
                    .asObject();
        });
    }

    /**
     * Prikazuje naziv vežbe u padajućoj listi.
     */
    private void configureExerciseComboBox() {
        exerciseComboBox.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(Exercise exercise) {
                        return exercise == null
                                ? ""
                                : exercise.getName();
                    }

                    @Override
                    public Exercise fromString(String text) {
                        return null;
                    }
                }
        );
    }

    /**
     * Učitava sve dostupne vežbe.
     */
    private void loadAvailableExercises() {
        try {
            Response<?> response =
                    fitnessClient.getAllExercises();

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            availableExercises.clear();

            if (response.getData() instanceof Iterable<?> items) {
                for (Object item : items) {
                    if (item instanceof Exercise exercise) {
                        availableExercises.add(exercise);
                    }
                }
            }

            sessionExerciseTable.refresh();

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Dostupne vežbe nije moguće učitati."
            );
        }
    }

    /**
     * Učitava vežbe izabranog treninga.
     */
    private void loadSessionExercises() {
        if (selectedSession == null) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.getSessionExercises(
                            selectedSession.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            sessionExercises.clear();

            if (response.getData() instanceof Iterable<?> items) {
                for (Object item : items) {
                    if (item instanceof SessionExercise exercise) {
                        sessionExercises.add(exercise);
                    }
                }
            }

            sessionExerciseTable.refresh();
            updateTotalVolume();

            showSuccess(
                    "Učitano vežbi: " + sessionExercises.size()
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Odrađene vežbe nije moguće učitati."
            );
        }
    }

    /**
     * Dodaje odrađenu vežbu treningu.
     */
    @FXML
    private void handleAdd() {
        if (selectedSession == null) {
            showError("Trening nije izabran.");
            return;
        }

        Exercise selectedExercise =
                exerciseComboBox.getValue();

        if (selectedExercise == null) {
            showError("Izaberite vežbu.");
            return;
        }

        try {
            SessionExercise sessionExercise =
                    new SessionExercise(
                            selectedSession.getId(),
                            selectedExercise.getId(),
                            parsePositiveInteger(
                                    setsField,
                                    "Broj serija"
                            ),
                            parsePositiveInteger(
                                    repsField,
                                    "Broj ponavljanja"
                            ),
                            parseNonNegativeDouble(
                                    weightField,
                                    "Težina"
                            )
                    );

            Response<?> response =
                    fitnessClient.addSessionExercise(
                            sessionExercise
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData()
                    instanceof SessionExercise savedExercise) {

                sessionExercises.add(savedExercise);
                sessionExerciseTable.refresh();
                updateTotalVolume();
                handleClear();

                showSuccess(
                        "Odrađena vežba je uspešno dodata."
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException | ClassNotFoundException exception) {
            showError("Server nije dostupan.");
        }
    }

    /**
     * Menja izabranu odrađenu vežbu.
     */
    @FXML
    private void handleUpdate() {
        SessionExercise selectedItem =
                sessionExerciseTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            showError(
                    "Izaberite odrađenu vežbu iz tabele."
            );
            return;
        }

        Exercise selectedExercise =
                exerciseComboBox.getValue();

        if (selectedExercise == null) {
            showError("Izaberite vežbu.");
            return;
        }

        try {
            SessionExercise updatedExercise =
                    new SessionExercise(
                            selectedItem.getId(),
                            selectedSession.getId(),
                            selectedExercise.getId(),
                            parsePositiveInteger(
                                    setsField,
                                    "Broj serija"
                            ),
                            parsePositiveInteger(
                                    repsField,
                                    "Broj ponavljanja"
                            ),
                            parseNonNegativeDouble(
                                    weightField,
                                    "Težina"
                            )
                    );

            Response<?> response =
                    fitnessClient.updateSessionExercise(
                            updatedExercise
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData()
                    instanceof SessionExercise savedExercise) {

                int selectedIndex =
                        sessionExerciseTable
                                .getSelectionModel()
                                .getSelectedIndex();

                sessionExercises.set(
                        selectedIndex,
                        savedExercise
                );

                sessionExerciseTable.refresh();
                updateTotalVolume();
                handleClear();

                showSuccess(
                        "Odrađena vežba je uspešno izmenjena."
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException | ClassNotFoundException exception) {
            showError("Server nije dostupan.");
        }
    }

    /**
     * Uklanja izabranu odrađenu vežbu.
     */
    @FXML
    private void handleRemove() {
        SessionExercise selectedItem =
                sessionExerciseTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            showError(
                    "Izaberite odrađenu vežbu iz tabele."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Potvrda uklanjanja");
        confirmation.setHeaderText(
                "Uklanjanje odrađene vežbe"
        );
        confirmation.setContentText(
                "Da li želite da uklonite izabranu vežbu?"
        );

        Optional<ButtonType> selectedButton =
                confirmation.showAndWait();

        if (selectedButton.isEmpty()
                || selectedButton.get() != ButtonType.OK) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.removeSessionExercise(
                            selectedItem.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            sessionExercises.remove(selectedItem);
            updateTotalVolume();
            handleClear();

            showSuccess(
                    "Odrađena vežba je uspešno uklonjena."
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError("Server nije dostupan.");
        }
    }

    /**
     * Popunjava formu podacima izabrane vežbe.
     */
    private void populateForm(SessionExercise item) {
        if (item == null) {
            return;
        }

        availableExercises.stream()
                .filter(exercise ->
                        exercise.getId() == item.getExerciseId()
                )
                .findFirst()
                .ifPresent(exerciseComboBox::setValue);

        setsField.setText(
                String.valueOf(item.getCompletedSets())
        );

        repsField.setText(
                String.valueOf(item.getCompletedReps())
        );

        weightField.setText(
                String.valueOf(item.getWeight())
        );
    }

    /**
     * Računa i prikazuje ukupan volumen treninga.
     */

    private void updateTotalVolume() {
        double totalVolume = sessionExercises.stream()
                .mapToDouble(exercise ->
                        exercise.getCompletedSets()
                                * exercise.getCompletedReps()
                                * exercise.getWeight()
                )
                .sum();

        totalVolumeLabel.setText(
                String.format(
                        "Ukupan volumen: %.2f kg",
                        totalVolume
                )
        );
    }

    private String findExerciseName(int exerciseId) {
        return availableExercises.stream()
                .filter(exercise ->
                        exercise.getId() == exerciseId
                )
                .map(Exercise::getName)
                .findFirst()
                .orElse("Nepoznata vežba");
    }

    private int parsePositiveInteger(
            TextField field,
            String fieldName
    ) {
        try {
            int value = Integer.parseInt(
                    field.getText().trim()
            );

            if (value <= 0) {
                throw new NumberFormatException();
            }

            return value;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName
                            + " mora biti pozitivan ceo broj."
            );
        }
    }

    private double parseNonNegativeDouble(
            TextField field,
            String fieldName
    ) {
        try {
            double value = Double.parseDouble(
                    field.getText()
                            .trim()
                            .replace(',', '.')
            );

            if (value < 0) {
                throw new NumberFormatException();
            }

            return value;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName
                            + " mora biti broj jednak ili veći od nule."
            );
        }
    }

    /**
     * Čisti polja forme.
     */
    @FXML
    private void handleClear() {
        sessionExerciseTable.getSelectionModel()
                .clearSelection();
        exerciseComboBox.setValue(null);
        setsField.clear();
        repsField.clear();
        weightField.clear();
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