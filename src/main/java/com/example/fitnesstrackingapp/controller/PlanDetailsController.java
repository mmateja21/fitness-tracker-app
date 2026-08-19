package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.model.Exercise;
import com.example.fitnesstrackingapp.model.PlanExercise;
import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.network.FitnessClient;
import com.example.fitnesstrackingapp.network.Response;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Optional;

/**
 * Upravlja vežbama i parametrima izabranog plana.
 */
public class PlanDetailsController {

    @FXML
    private Label planNameLabel;

    @FXML
    private TableView<PlanExercise> planExerciseTable;

    @FXML
    private TableColumn<PlanExercise, Integer> positionColumn;

    @FXML
    private TableColumn<PlanExercise, String> exerciseNameColumn;

    @FXML
    private TableColumn<PlanExercise, Integer> setsColumn;

    @FXML
    private TableColumn<PlanExercise, Integer> repsColumn;

    @FXML
    private TableColumn<PlanExercise, Double> weightColumn;

    @FXML
    private ComboBox<Exercise> exerciseComboBox;

    @FXML
    private TextField setsField;

    @FXML
    private TextField repsField;

    @FXML
    private TextField weightField;

    @FXML
    private TextField positionField;

    @FXML
    private Label statusLabel;

    /** Mrežni klijent. */
    private final FitnessClient fitnessClient =
            new FitnessClient();

    /** Stavke prikazane u tabeli. */
    private final ObservableList<PlanExercise> planItems =
            FXCollections.observableArrayList();

    /** Sve dostupne vežbe. */
    private final ObservableList<Exercise> availableExercises =
            FXCollections.observableArrayList();

    /** Plan koji se trenutno uređuje. */
    private WorkoutPlan selectedPlan;

    /**
     * Inicijalizuje kontrole i učitava dostupne vežbe.
     */
    @FXML
    private void initialize() {
        configureTableColumns();
        configureExerciseComboBox();

        planExerciseTable.setItems(planItems);
        exerciseComboBox.setItems(availableExercises);

        planExerciseTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        populateForm(newValue)
                );

        loadAvailableExercises();
    }

    /**
     * Postavlja plan čije se stavke uređuju.
     *
     * @param plan izabrani plan
     */
    public void setPlan(WorkoutPlan plan) {
        selectedPlan = plan;
        planNameLabel.setText(plan.getName());
        loadPlanItems();
    }

    /**
     * Povezuje kolone sa podacima modela.
     */
    private void configureTableColumns() {
        positionColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getPosition()
                ).asObject()
        );

        exerciseNameColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        findExerciseName(
                                cellData.getValue().getExerciseId()
                        )
                )
        );

        setsColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getTargetSets()
                ).asObject()
        );

        repsColumn.setCellValueFactory(cellData ->
                new ReadOnlyIntegerWrapper(
                        cellData.getValue().getTargetReps()
                ).asObject()
        );

        weightColumn.setCellValueFactory(cellData ->
                new ReadOnlyDoubleWrapper(
                        cellData.getValue().getTargetWeight()
                ).asObject()
        );
    }

    /**
     * Podešava prikaz naziva vežbe u ComboBox kontroli.
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
     * Učitava sve dostupne vežbe sa servera.
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

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Dostupne vežbe nije moguće učitati."
            );
        }
    }

    /**
     * Učitava stavke trenutno izabranog plana.
     */
    private void loadPlanItems() {
        if (selectedPlan == null) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.getPlanExercises(
                            selectedPlan.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            planItems.clear();

            if (response.getData() instanceof Iterable<?> items) {
                for (Object item : items) {
                    if (item instanceof PlanExercise planExercise) {
                        planItems.add(planExercise);
                    }
                }
            }

            planExerciseTable.refresh();

            showSuccess(
                    "Učitano stavki: " + planItems.size()
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Stavke plana nije moguće učitati."
            );
        }
    }

    /**
     * Dodaje izabranu vežbu u plan.
     */
    @FXML
    private void handleAdd() {
        if (selectedPlan == null) {
            showError("Plan nije izabran.");
            return;
        }

        Exercise exercise = exerciseComboBox.getValue();

        if (exercise == null) {
            showError("Izaberite vežbu.");
            return;
        }

        try {
            PlanExercise item = new PlanExercise(
                    selectedPlan.getId(),
                    exercise.getId(),
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
                    ),
                    parsePositiveInteger(
                            positionField,
                            "Redosled"
                    )
            );

            Response<?> response =
                    fitnessClient.addExerciseToPlan(item);

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData() instanceof PlanExercise savedItem) {
                planItems.add(savedItem);
                planItems.sort(
                        (first, second) -> Integer.compare(
                                first.getPosition(),
                                second.getPosition()
                        )
                );

                planExerciseTable.refresh();
                handleClear();
                showSuccess(
                        "Vežba je uspešno dodata u plan."
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException | ClassNotFoundException exception) {
            showError("Server nije dostupan.");
        }
    }

    /**
     * Menja izabranu stavku plana.
     */
    @FXML
    private void handleUpdate() {
        PlanExercise selectedItem =
                planExerciseTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            showError(
                    "Izaberite stavku plana iz tabele."
            );
            return;
        }

        Exercise exercise = exerciseComboBox.getValue();

        if (exercise == null) {
            showError("Izaberite vežbu.");
            return;
        }

        try {
            PlanExercise updatedItem = new PlanExercise(
                    selectedItem.getId(),
                    selectedPlan.getId(),
                    exercise.getId(),
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
                    ),
                    parsePositiveInteger(
                            positionField,
                            "Redosled"
                    )
            );

            Response<?> response =
                    fitnessClient.updatePlanExercise(
                            updatedItem
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData() instanceof PlanExercise savedItem) {
                int selectedIndex =
                        planExerciseTable.getSelectionModel()
                                .getSelectedIndex();

                planItems.set(selectedIndex, savedItem);
                planItems.sort(
                        (first, second) -> Integer.compare(
                                first.getPosition(),
                                second.getPosition()
                        )
                );

                planExerciseTable.refresh();
                handleClear();
                showSuccess(
                        "Stavka plana je uspešno izmenjena."
                );
            }

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        } catch (IOException | ClassNotFoundException exception) {
            showError("Server nije dostupan.");
        }
    }

    /**
     * Uklanja izabranu stavku iz plana.
     */
    @FXML
    private void handleRemove() {
        PlanExercise selectedItem =
                planExerciseTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedItem == null) {
            showError(
                    "Izaberite stavku plana iz tabele."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );
        confirmation.setTitle("Potvrda uklanjanja");
        confirmation.setHeaderText(
                "Uklanjanje vežbe iz plana"
        );
        confirmation.setContentText(
                "Da li želite da uklonite izabranu stavku?"
        );

        Optional<ButtonType> selectedButton =
                confirmation.showAndWait();

        if (selectedButton.isEmpty()
                || selectedButton.get() != ButtonType.OK) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.removeExerciseFromPlan(
                            selectedItem.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            planItems.remove(selectedItem);
            handleClear();

            showSuccess(
                    "Vežba je uspešno uklonjena iz plana."
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError("Server nije dostupan.");
        }
    }

    /**
     * Popunjava formu podacima izabrane stavke.
     *
     * @param item izabrana stavka
     */
    private void populateForm(PlanExercise item) {
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
                String.valueOf(item.getTargetSets())
        );
        repsField.setText(
                String.valueOf(item.getTargetReps())
        );
        weightField.setText(
                String.valueOf(item.getTargetWeight())
        );
        positionField.setText(
                String.valueOf(item.getPosition())
        );
    }

    /**
     * Vraća naziv vežbe prema ID-u.
     *
     * @param exerciseId identifikator vežbe
     * @return naziv vežbe
     */
    private String findExerciseName(int exerciseId) {
        return availableExercises.stream()
                .filter(exercise ->
                        exercise.getId() == exerciseId
                )
                .map(Exercise::getName)
                .findFirst()
                .orElse("Nepoznata vežba");
    }

    /**
     * Čita pozitivan ceo broj iz polja.
     *
     * @param field tekstualno polje
     * @param fieldName naziv podatka
     * @return pozitivan broj
     */
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

    /**
     * Čita nenegativan decimalni broj iz polja.
     *
     * @param field tekstualno polje
     * @param fieldName naziv podatka
     * @return nenegativan decimalni broj
     */
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
     * Čisti formu.
     */
    @FXML
    private void handleClear() {
        planExerciseTable.getSelectionModel()
                .clearSelection();

        exerciseComboBox.setValue(null);
        setsField.clear();
        repsField.clear();
        weightField.clear();
        positionField.clear();
        statusLabel.setText("");
    }

    /**
     * Prikazuje uspešnu poruku.
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