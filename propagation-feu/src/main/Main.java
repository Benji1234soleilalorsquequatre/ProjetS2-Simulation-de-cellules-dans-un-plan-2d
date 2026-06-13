package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the Forest Fire Simulation application.
 * This JavaFX application loads the FXML interface and applies the stylesheet.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application.
     * Loads the main FXML interface file and initializes the application window.
     *
     * @param stage The primary stage for this application
     * @throws Exception If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("view.fxml"));

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("Style.css").toExternalForm());

        stage.setTitle("Simulation Feu de Forêt");

        stage.setScene(scene);

        stage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        launch();
    }
}