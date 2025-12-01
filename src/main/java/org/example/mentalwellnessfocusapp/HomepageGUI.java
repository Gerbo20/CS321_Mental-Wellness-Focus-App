package org.example.mentalwellnessfocusapp;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomepageGUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader =
                new FXMLLoader(HomepageGUI.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 360, 640);

        stage.setTitle("Mental Wellness Focus App!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}