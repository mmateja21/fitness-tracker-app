package com.example.fitnesstrackingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FitnessApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FitnessApplication.class.getResource("exercise-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(),
                1050, 650);


        stage.setTitle("Fitness tracking manager");
        stage.setScene(scene);
        stage.setMinHeight(550);
        stage.setMinWidth(900);
        stage.show();
    }
}
