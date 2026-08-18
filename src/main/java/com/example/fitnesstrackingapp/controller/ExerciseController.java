package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.model.MuscleGroup;
import com.example.fitnesstrackingapp.network.FitnessClient;
import com.example.fitnesstrackingapp.network.Response;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

import java.io.IOException;

/**
 * Upravlja ekranom za pregled i uređivanje vežbi.
 */
public class ExerciseController {

    /** Tabela svih vežbi. */
    @FXML
    private TableView<Exercise> exerciseTable;

    /** Kolona identifikatora. */
    @FXML
    private TableColumn<Exercise, Integer> idColumn;

    /** Kolona naziva. */
    @FXML
    private TableColumn<Exercise, String> nameColumn;

    /** Kolona mišićne grupe. */
    @FXML
    private TableColumn<Exercise, MuscleGroup> muscleGroupColumn;

    /** Kolona opreme. */
    @FXML
    private TableColumn<Exercise, String> equipmentColumn;

    /** Kolona opisa. */
    @FXML
    private TableColumn<Exercise, String> descriptionColumn;

    /** Polje za naziv vežbe. */
    @FXML
    private TextField nameField;

    /** Izbor mišićne grupe. */
    @FXML
    private ComboBox<MuscleGroup> muscleGroupComboBox;

    /** Polje za opremu. */
    @FXML
    private TextField equipmentField;

    /** Polje za opis vežbe. */
    @FXML
    private TextArea descriptionArea;

    /** Labela za poruke korisniku. */
    @FXML
    private Label statusLabel;

    /** Mrežni klijent za komunikaciju sa serverom. */
    private final FitnessClient fitnessClient =
            new FitnessClient();

    /** Podaci prikazani u tabeli. */
    private final ObservableList<Exercise> exercises =
            FXCollections.observableArrayList();

    /**
     * Inicijalizuje kontrole nakon učitavanja FXML-a.
     */
    @FXML
    private void initialize() {
        configureTableColumns();

        muscleGroupComboBox.getItems().setAll(
                MuscleGroup.values()
        );

        exerciseTable.setItems(exercises);

        exerciseTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        populateForm(newValue)
                );

        loadExercises();
    }

    /**
     * Povezuje kolone tabele sa atributima Exercise modela.
     */
    private void configureTableColumns() {
        idColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getId()
                ).asObject()
        );

        nameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getName()
                )
        );

        muscleGroupColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue().getMuscleGroup()
                )
        );

        equipmentColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getEquipment()
                )
        );

        descriptionColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getDescription()
                )
        );
    }

    /**
     * Učitava sve vežbe sa servera.
     */
    private void loadExercises() {
        try {
            Response<?> response =
                    fitnessClient.getAllExercises();

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            exercises.clear();

            if (response.getData() instanceof Iterable<?> items) {
                for (Object item : items) {
                    if (item instanceof Exercise exercise) {
                        exercises.add(exercise);
                    }
                }
            }

            showSuccess(
                    "Učitano vežbi: " + exercises.size()
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Server nije dostupan. Pokrenite FitnessServer."
            );
        }
    }

    /**
     * Popunjava formu podacima iz izabranog reda.
     *
     * @param exercise izabrana vežba
     */
    private void populateForm(Exercise exercise) {
        if (exercise == null) {
            return;
        }

        nameField.setText(exercise.getName());
        muscleGroupComboBox.setValue(
                exercise.getMuscleGroup()
        );
        equipmentField.setText(exercise.getEquipment());
        descriptionArea.setText(exercise.getDescription());
    }
    /**
     * Čita podatke iz forme i zahteva kreiranje vežbe.
     */
    @FXML
    private void handleCreate() {
        Exercise exercise = new Exercise(
                nameField.getText(),
                muscleGroupComboBox.getValue(),
                equipmentField.getText(),
                descriptionArea.getText()
        );

        try {
            Response<?> response =
                    fitnessClient.createExercise(exercise);

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData() instanceof Exercise savedExercise) {
                exercises.add(savedExercise);
                handleClear();

                showSuccess(
                        "Vežba je uspešno dodata."
                );
            } else {
                showError(
                        "Server nije vratio sačuvanu vežbu."
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Dodavanje nije uspelo jer server nije dostupan."
            );
        }
    }



    /**
     * Šalje izmenjene podatke izabrane vežbe serveru.
     */
    @FXML
    private void handleUpdate() {
        Exercise selectedExercise =
                exerciseTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedExercise == null) {
            showError(
                    "Prvo izaberite vežbu iz tabele."
            );
            return;
        }

        Exercise updatedExercise = new Exercise(
                selectedExercise.getId(),
                nameField.getText(),
                muscleGroupComboBox.getValue(),
                equipmentField.getText(),
                descriptionArea.getText()
        );

        try {
            Response<?> response =
                    fitnessClient.updateExercise(
                            updatedExercise
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData() instanceof Exercise savedExercise) {
                int selectedIndex =
                        exerciseTable.getSelectionModel()
                                .getSelectedIndex();

                exercises.set(
                        selectedIndex,
                        savedExercise
                );

                handleClear();

                showSuccess(
                        "Vežba je uspešno izmenjena."
                );
            } else {
                showError(
                        "Server nije vratio izmenjenu vežbu."
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Izmena nije uspela jer server nije dostupan."
            );
        }
    }

    /**
     * Traži potvrdu i briše izabranu vežbu.
     */
    @FXML
    private void handleDelete() {
        Exercise selectedExercise =
                exerciseTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedExercise == null) {
            showError(
                    "Prvo izaberite vežbu iz tabele."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Potvrda brisanja");
        confirmation.setHeaderText(
                "Brisanje vežbe: "
                        + selectedExercise.getName()
        );
        confirmation.setContentText(
                "Da li ste sigurni da želite da obrišete vežbu?"
        );

        Optional<ButtonType> selectedButton =
                confirmation.showAndWait();

        if (selectedButton.isEmpty()
                || selectedButton.get() != ButtonType.OK) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.deleteExercise(
                            selectedExercise.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            exercises.remove(selectedExercise);
            handleClear();

            showSuccess(
                    "Vežba je uspešno obrisana."
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Brisanje nije uspelo jer server nije dostupan."
            );
        }
    }

    /**
     * Čisti formu i izbor u tabeli.
     */
    @FXML
    private void handleClear() {
        exerciseTable.getSelectionModel().clearSelection();
        nameField.clear();
        muscleGroupComboBox.setValue(null);
        equipmentField.clear();
        descriptionArea.clear();
        statusLabel.setText("");
    }

    /**
     * Prikazuje poruku o uspešnoj operaciji.
     *
     * @param message tekst poruke
     */
    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText(message);
    }

    /**
     * Prikazuje poruku o grešci.
     *
     * @param message tekst poruke
     */
    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }
}