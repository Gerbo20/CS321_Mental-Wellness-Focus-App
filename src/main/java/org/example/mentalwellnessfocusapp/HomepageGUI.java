package org.example.mentalwellnessfocusapp;


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomepageGUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(">>> HomepageGUI.start() called");
        FXMLLoader fxmlLoader =
                new FXMLLoader(HomepageGUI.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 360, 640);

        stage.setTitle("Welcome to MindAlign!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        System.err.println(">>> HomepageGUI started successfully");
    }

    public static void main(String[] args) {
        launch();
    }
}