package com.example.fitnesstrackingapp.controller;

import com.example.fitnesstrackingapp.model.WorkoutPlan;
import com.example.fitnesstrackingapp.network.FitnessClient;
import com.example.fitnesstrackingapp.network.Response;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import com.example.fitnesstrackingapp.FitnessApplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Upravlja ekranom za planove treninga.
 */
public class WorkoutPlanController {

    /** ID podrazumevanog lokalnog korisnika. */
    private static final int DEFAULT_USER_ID = 1;

    /** Format datuma prikazanog u tabeli. */
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /** Tabela planova treninga. */
    @FXML
    private TableView<WorkoutPlan> planTable;

    /** Kolona identifikatora. */
    @FXML
    private TableColumn<WorkoutPlan, Integer> idColumn;

    /** Kolona naziva. */
    @FXML
    private TableColumn<WorkoutPlan, String> nameColumn;

    /** Kolona opisa. */
    @FXML
    private TableColumn<WorkoutPlan, String> descriptionColumn;

    /** Kolona datuma kreiranja. */
    @FXML
    private TableColumn<WorkoutPlan, String> createdAtColumn;

    /** Polje naziva plana. */
    @FXML
    private TextField nameField;

    /** Polje opisa plana. */
    @FXML
    private TextArea descriptionArea;

    /** Labela sa porukom za korisnika. */
    @FXML
    private Label statusLabel;

    /** Mrežni klijent. */
    private final FitnessClient fitnessClient =
            new FitnessClient();

    /** Planovi prikazani u tabeli. */
    private final ObservableList<WorkoutPlan> plans =
            FXCollections.observableArrayList();

    /**
     * Inicijalizuje ekran nakon učitavanja FXML-a.
     */
    @FXML
    private void initialize() {
        configureTableColumns();
        planTable.setItems(plans);

        planTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        populateForm(newValue)
                );

        loadPlans();
    }

    /**
     * Povezuje kolone sa atributima modela.
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

        descriptionColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                        cellData.getValue().getDescription()
                )
        );

        createdAtColumn.setCellValueFactory(cellData -> {
            WorkoutPlan plan = cellData.getValue();

            String formattedDate =
                    plan.getCreatedAt() == null
                            ? ""
                            : plan.getCreatedAt()
                            .format(DISPLAY_FORMATTER);

            return new ReadOnlyStringWrapper(
                    formattedDate
            );
        });
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
                    "Učitano planova: " + plans.size()
            );

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Server nije dostupan. Pokrenite FitnessServer."
            );
        }
    }

    /**
     * Popunjava formu podacima izabranog plana.
     *
     * @param plan izabrani plan
     */
    private void populateForm(WorkoutPlan plan) {
        if (plan == null) {
            return;
        }

        nameField.setText(plan.getName());
        descriptionArea.setText(plan.getDescription());
    }

    /**
     * Kreira plan na osnovu podataka iz forme.
     */
    @FXML
    private void handleCreate() {
        WorkoutPlan plan = new WorkoutPlan(
                DEFAULT_USER_ID,
                nameField.getText(),
                descriptionArea.getText()
        );

        try {
            Response<?> response =
                    fitnessClient.createPlan(plan);

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData() instanceof WorkoutPlan savedPlan) {
                plans.add(0, savedPlan);
                handleClear();
                showSuccess("Plan je uspešno dodat.");
            } else {
                showError(
                        "Server nije vratio sačuvani plan."
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Dodavanje nije uspelo jer server nije dostupan."
            );
        }
    }

    /**
     * Menja izabrani plan.
     */
    @FXML
    private void handleUpdate() {
        WorkoutPlan selectedPlan =
                planTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedPlan == null) {
            showError(
                    "Prvo izaberite plan iz tabele."
            );
            return;
        }

        WorkoutPlan updatedPlan = new WorkoutPlan(
                selectedPlan.getId(),
                selectedPlan.getUserId(),
                nameField.getText(),
                descriptionArea.getText(),
                selectedPlan.getCreatedAt()
        );

        try {
            Response<?> response =
                    fitnessClient.updatePlan(updatedPlan);

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            if (response.getData() instanceof WorkoutPlan savedPlan) {
                int selectedIndex =
                        planTable.getSelectionModel()
                                .getSelectedIndex();

                plans.set(selectedIndex, savedPlan);
                handleClear();
                showSuccess("Plan je uspešno izmenjen.");
            } else {
                showError(
                        "Server nije vratio izmenjeni plan."
                );
            }

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Izmena nije uspela jer server nije dostupan."
            );
        }
    }

    /**
     * Traži potvrdu i briše izabrani plan.
     */
    @FXML
    private void handleDelete() {
        WorkoutPlan selectedPlan =
                planTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedPlan == null) {
            showError(
                    "Prvo izaberite plan iz tabele."
            );
            return;
        }

        Alert confirmation = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Potvrda brisanja");
        confirmation.setHeaderText(
                "Brisanje plana: "
                        + selectedPlan.getName()
        );
        confirmation.setContentText(
                "Brisanjem plana biće uklonjene i njegove stavke."
        );

        Optional<ButtonType> selectedButton =
                confirmation.showAndWait();

        if (selectedButton.isEmpty()
                || selectedButton.get() != ButtonType.OK) {
            return;
        }

        try {
            Response<?> response =
                    fitnessClient.deletePlan(
                            selectedPlan.getId()
                    );

            if (!response.isSuccessful()) {
                showError(response.getMessage());
                return;
            }

            plans.remove(selectedPlan);
            handleClear();
            showSuccess("Plan je uspešno obrisan.");

        } catch (IOException | ClassNotFoundException exception) {
            showError(
                    "Brisanje nije uspelo jer server nije dostupan."
            );
        }
    }

    /**
     * Otvara prozor za uređivanje vežbi izabranog plana.
     */
    @FXML
    private void handleManageExercises() {
        WorkoutPlan selectedPlan =
                planTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedPlan == null) {
            showError(
                    "Prvo izaberite plan iz tabele."
            );
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    FitnessApplication.class.getResource(
                            "plan-details-view.fxml"
                    )
            );

            Parent root = loader.load();

            PlanDetailsController controller =
                    loader.getController();

            controller.setPlan(selectedPlan);

            Stage stage = new Stage();
            stage.setTitle(
                    "Vežbe plana - " + selectedPlan.getName()
            );
            stage.initModality(
                    Modality.APPLICATION_MODAL
            );
            stage.initOwner(
                    planTable.getScene().getWindow()
            );
            stage.setScene(
                    new Scene(root, 950, 600)
            );
            stage.setMinWidth(850);
            stage.setMinHeight(550);
            stage.showAndWait();

        } catch (IOException exception) {
            showError(
                    "Prozor za uređivanje plana nije moguće otvoriti."
            );
            exception.printStackTrace();
        }
    }


    @FXML
    private void handleClear() {
        planTable.getSelectionModel().clearSelection();
        nameField.clear();
        descriptionArea.clear();
        statusLabel.setText("");
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