package com.example.fitnesstrackingapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FitnessApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(FitnessApplication.class.getResource("main-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(),
                1280, 720);


        stage.setTitle("Fitness tracking app");
        stage.setScene(scene);
        stage.setMinHeight(650);
        stage.setMinWidth(1100);
        stage.show();
    }
}
