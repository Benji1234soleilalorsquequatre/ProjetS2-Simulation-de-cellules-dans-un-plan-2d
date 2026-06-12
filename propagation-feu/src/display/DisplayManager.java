package display;

import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import model.Grid;
import model.Vegetation;
import model.State;
import simulation.SimulationEngine;
import model.Cell;



public class DisplayManager{

    private final SimulationEngine engine;
    private final Canvas canvas;
    
    private boolean canadairMode = false;
    private final int CELL_SIZE = 8; // cell size


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
    

    public DisplayManager(SimulationEngine engine, Canvas canvas){
        this.engine = engine;
        this.canvas = canvas;
    }

    

    public boolean containsFire() {

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

    
    public void updateDisplay() {
        Grid currentGrid = engine.getCurrentGrid();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // On efface la toile avant de redessiner
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int row = 0; row < 70; row++) {
            for (int col = 0; col < 70; col++) {
                State state = currentGrid.getCell(row, col).getState();

                
                switch (state) {
                    case TREE:
                        if (currentGrid.getCell(row, col).getVegetation() == Vegetation.BRUSHWOOD) {
                            gc.setFill(Color.YELLOWGREEN); // Vert clair pour les broussailles
                        } else {
                            gc.setFill(Color.DARKGREEN); // Vert foncé pour les grands arbres
                        }
                        break;
                    case BURNING:
                        gc.setFill(Color.RED);
                        break;
                    case ASH:
                        gc.setFill(Color.DARKGRAY);
                        break;
                    case FIREBREAK:
                        gc.setFill(Color.BROWN);
                        break;
                    case EMPTY:
                        gc.setFill(Color.WHITE);
                        break;
                    case WET:
                        gc.setFill(Color.LIGHTBLUE);
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

    public void dropWater(int centerRow, int centerCol) {

        Grid grid = engine.getCurrentGrid();

        int radius = 2;

        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {

                if (dr * dr + dc * dc > radius * radius) {
                    continue;
                }

                int row = centerRow + dr;
                int col = centerCol + dc;

                if (!grid.isInside(row, col)) {
                    continue;
                }

                Cell cell = grid.getCell(row, col);

                if (cell.getState() == State.ASH) {
                    continue;
                }

                cell.setState(State.WET);
                cell.setWetTime(5);

                cell.setHumidity(100);
                cell.setHeat(20);
            }
        }
    }
}
