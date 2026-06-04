/**
 * The Controller class in a Java application initializes a simulation engine for a forest fire
 * simulation and updates the display accordingly.
 */
package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Grid;
import model.State;
import simulation.NaiveFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;

public class Controller {

    @FXML
    private Canvas canvas; 

    private final int CELL_SIZE = 20; // cell size

    private Grid forest;
    private SimulationEngine engine;

    @FXML
    public void initialize() {
        System.out.println("INITIALIZE");
        forest = new Grid(20, 20);
        forest.setCellState(10, 10, State.BURNING);
        SimulationConfig config = new SimulationConfig();
        NaiveFireAlgorithm algorithm = new NaiveFireAlgorithm();
        
        engine = new SimulationEngine(
                forest,
                algorithm,
                config
        );

    
        updateDisplay();
    }

    private void updateDisplay() {
        Grid currentGrid = engine.getCurrentGrid();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // On efface la toile avant de redessiner
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
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

    /**
     * Lance une étape de simulation à chaque clic sur le bouton.
     */
    @FXML
    private void startSimulation() {
        engine.step();
        updateDisplay();
    }
}