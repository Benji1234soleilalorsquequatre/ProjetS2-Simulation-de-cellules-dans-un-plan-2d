/**
 * The Controller class in a Java application initializes a simulation engine for a forest fire
 * simulation and updates the display accordingly.
 */
package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.Cell;
import model.Grid;
import model.State;
import simulation.AdvancedFireAlgorithm;
import simulation.FirePropagationAlgorithm;
import simulation.NaiveFireAlgorithm;
import simulation.PreventionFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;

public class Controller {

    @FXML
    private Canvas canvas; 

    private final int CELL_SIZE = 8; // cell size

    private Grid forest;
    private SimulationEngine engine;

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            engine.step();
            updateDisplay();

            if (!containsFire()) {
                timeline.stop();
                running = false;
            }
        })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        System.out.println("INITIALIZE");
        forest = new Grid(200, 200);
        forest.setCellState(24, 24, State.BURNING);
        SimulationConfig config = new SimulationConfig();
        FirePropagationAlgorithm algorithm = new PreventionFireAlgorithm();
        
        engine = new SimulationEngine(
                forest,
                algorithm,
                config
        );

        canvas.setOnMouseClicked(this::handleCanvasClick);
    
        updateDisplay();
    }

    private boolean containsFire() {

        Grid grid = engine.getCurrentGrid();

        for (int row = 0; row < grid.getHeight(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {

                if (grid.getCell(row, col).getState() == State.BURNING) {
                    return true;
                }
            }
        }
    return false;
    }

    private void handleCanvasClick(MouseEvent event) {

        int col = (int) (event.getX() / CELL_SIZE);
        int row = (int) (event.getY() / CELL_SIZE);

        Grid currentGrid = engine.getCurrentGrid();

        if (currentGrid.isInside(row, col)) {

            Cell cell = currentGrid.getCell(row, col);

            stateLabel.setText("État : " + cell.getState());
            humidityLabel.setText("Humidité : " + cell.getHumidity());
            heatLabel.setText("Chaleur : " + cell.getHeat());
            fuelLabel.setText("Combustible : " + cell.getFuel());
    }
    }

    private void updateDisplay() {
        Grid currentGrid = engine.getCurrentGrid();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // On efface la toile avant de redessiner
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int row = 0; row < 200; row++) {
            for (int col = 0; col < 200; col++) {
                State state = currentGrid.getCell(row, col).getState();

                
                switch (state) {
                    case TREE:
                        gc.setFill(Color.FORESTGREEN);
                        break;
                    case BURNING:
                        gc.setFill(Color.RED);
                        break;
                    case ASH:
                        gc.setFill(Color.DARKGRAY);
                        break;
                    case WATER:
                        gc.setFill(Color.BLUE);
                        break;
                    case FIREBREAK:
                        gc.setFill(Color.BROWN);
                        break;
                    case EMPTY:
                        gc.setFill(Color.WHITE);
                        break;
                    case PREVENTIVE:
                        gc.setFill(Color.ORANGE);
                        break;
                }

                // Dessin du rectangle coloré
                gc.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                
                // Dessin de la petite bordure de la case
                gc.setStroke(Color.web("#dcdcdc"));
                gc.setLineWidth(0.5);
                gc.strokeRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private Timeline timeline;
    private boolean running = false;
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
