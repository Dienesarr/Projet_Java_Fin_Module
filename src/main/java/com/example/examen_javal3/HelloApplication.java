package com.example.examen_javal3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
        public void start(Stage stage) throws IOException, Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);

            // Centrer la fenêtre sur l'écran
            stage.centerOnScreen();

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch();

        }
}