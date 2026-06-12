package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /** 
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(Main.class.getResource("view.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Simulation Feu de Forêt");

        stage.setScene(scene);

        stage.show();
    }

    /** 
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}