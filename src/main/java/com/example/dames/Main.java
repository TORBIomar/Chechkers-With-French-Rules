package com.example.dames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/dames/board.fxml"));
        Parent root = loader.load();
        // Set minimum size for the window
        int minSize = 8 * 60; // Minimum tile size of 60px
        Scene scene = new Scene(root, minSize, minSize);
        scene.getStylesheets().add(getClass().getResource("/com/example/dames/styles.css").toExternalForm());
        primaryStage.setTitle("Jeu de Dames - Regles Francaises/Marocaines");
        
        // Set window icon (logo)
        try {
            Image icon = new Image(getClass().getResourceAsStream("/com/example/dames/icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            // Icon file not found - that's okay, window will use default icon
            System.out.println("Icon not found. Place icon.png in src/main/resources/com/example/dames/");
        }
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(minSize);
        primaryStage.setMinHeight(minSize);
        primaryStage.setResizable(true); // Allow resizing
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
