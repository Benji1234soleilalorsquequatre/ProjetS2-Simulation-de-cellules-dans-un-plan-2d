/**
 * The Controller class in a Java application initializes a simulation engine for a forest fire
 * simulation and updates the display accordingly.
 */
package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import model.Grid;
import model.State;
import model.Cell;
import simulation.AdvancedFireAlgorithm;
import simulation.FirePropagationAlgorithm;
import simulation.NaiveFireAlgorithm;
import simulation.PreventionFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;
import display.DisplayManager;

public class Controller {

    @FXML
    private Canvas canvas; 
    @FXML
    private TextField inputMinHumidity;
    @FXML
    private TextField inputMinFuel;

    private Timeline timeline;
    private boolean running = false;
    private boolean canadairMode = false;
    private Grid forest;
    private SimulationEngine engine;
    private DisplayManager displayManager;
    private final int CELL_SIZE = 8; // cell size

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            engine.step();
            displayManager.updateDisplay();

            if (!displayManager.containsFire()) {
                timeline.stop();
                running = false;
            }
        })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        System.out.println("INITIALIZE");
        forest = new Grid(70, 70);
        forest.setCellState(24, 24, State.BURNING);
        SimulationConfig config = new SimulationConfig();
        FirePropagationAlgorithm algorithm = new PreventionFireAlgorithm();

        
        engine = new SimulationEngine(
                forest,
                algorithm,
                config
        );
        displayManager = new DisplayManager(engine, canvas);
        canvas.setOnMouseClicked(this::handleCanvasClick);
    
        displayManager.updateDisplay();
    }

    public void handleCanvasClick(MouseEvent event) {

        int col = (int) (event.getX() / CELL_SIZE);
        int row = (int) (event.getY() / CELL_SIZE);

        Grid currentGrid = engine.getCurrentGrid();

        if (currentGrid.isInside(row, col)) {

            if(canadairMode) {
                displayManager.dropWater(row, col);

                canadairMode = false;
                canadairButton.setText("Canadair");

            displayManager.updateDisplay();
            return;
            }       

            Cell cell = currentGrid.getCell(row, col);

            stateLabel.setText("État : " + cell.getState());
            humidityLabel.setText("Humidité : " + cell.getHumidity());
            heatLabel.setText("Chaleur : " + cell.getHeat());
            fuelLabel.setText("Combustible : " + cell.getFuel());
        }
    }

    @FXML
    private void activateCanadair() {

        if (!running) {
            canadairMode = true;
            canadairButton.setText("Choisir une zone");
        }
    }


    /**
     * Lance une étape de simulation à chaque clic sur le bouton.
     */
    @FXML
    private void startSimulation() {

        if (running) {
            timeline.pause();
            startButton.setText("Start");
            running = false;
        } else {
            timeline.play();
            startButton.setText("Stop");
            running = true;
        }   
    }   

    @FXML
    private void handleResetSimulation() {
        // 1. On stoppe le rafraîchissement automatique en cours
        timeline.pause();
        running = false;
        startButton.setText("Start");

        try {
            // 2. On récupère ce que l'utilisateur a écrit dans l'interface
            int minHum = Integer.parseInt(inputMinHumidity.getText());
            int minFuel = Integer.parseInt(inputMinFuel.getText());

            // 3. On crée une toute nouvelle grille avec notre constructeur à 4 paramètres
            forest = new Grid(70, 70, minHum, minFuel);
            forest.setCellState(24, 24, State.BURNING);

            // 4. On réinjecte la grille dans un nouveau moteur de simulation
            SimulationConfig config = new SimulationConfig();
            engine = new SimulationEngine(forest, new PreventionFireAlgorithm(), config);

            // 5. On demande au gestionnaire d'affichage d'utiliser le nouveau moteur
            // (on doit recréer le displayManager car l'ancien pointe sur l'ancienne simulation)
            displayManager = new DisplayManager(engine, canvas);

            // 6. On redessine l'écran à blanc
            displayManager.updateDisplay();
            System.out.println("Simulation relancée ! Humidité min : " + minHum + " | Combustible min : " + minFuel);

        } catch (NumberFormatException e) {
            System.out.println("Erreur : Veuillez entrer des nombres valides dans les cases !");
        }
    }

    @FXML
    private Button canadairButton;

    @FXML 
    private Button startButton;

    @FXML
    private Label stateLabel;

    @FXML
    private Label humidityLabel;

    @FXML
    private Label heatLabel;

    @FXML
    private Label fuelLabel;
}