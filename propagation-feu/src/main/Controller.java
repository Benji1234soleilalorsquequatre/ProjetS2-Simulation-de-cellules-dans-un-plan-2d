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