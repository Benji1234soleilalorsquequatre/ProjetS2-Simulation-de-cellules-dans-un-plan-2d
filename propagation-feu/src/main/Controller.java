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
import simulation.PreventionFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;
import display.DisplayManager;
/**
 * Contrôleur JavaFX qui gère les interactions de l'utilisateur avec l'interface.
 * Fait le lien entre l'affichage (DisplayManager) et le moteur logique (SimulationEngine).
 */
public class Controller {

    @FXML private Canvas canvas; 
    @FXML private TextField inputMinHumidity;
    @FXML private TextField inputMinFuel;
    @FXML private TextField inputMaxHumidity;
    @FXML private TextField inputMaxFuel;
    @FXML private Button canadairButton;
    @FXML private Button startButton;
    @FXML private Button stepButton;
    @FXML private Label stateLabel;
    @FXML private Label humidityLabel;
    @FXML private Label heatLabel;
    @FXML private Label fuelLabel;
   

    private Timeline timeline;
    private boolean running = false;
    private boolean canadairMode = false;
    private Grid forest;
    private SimulationEngine engine;
    private DisplayManager displayManager;
    private double lastMouseX;
    private double lastMouseY;
    private boolean draggingCamera = false;
    private boolean mouseMovedDuringDrag = false;

    /**
     * Méthode appelée automatiquement au démarrage de l'application.
     * Configure la boucle d'animation temporelle (Timeline) et initialise la première forêt.
     */
    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            engine.step();
            displayManager.updateDisplay();

            if (!displayManager.containsFire()) {
                timeline.stop();
                running = false;
                startButton.setText("Start");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        forest = new Grid(70, 70);
        forest.setCellState(24, 24, State.BURNING); // Foyer initial
        
        engine = new SimulationEngine(forest, new PreventionFireAlgorithm(), new SimulationConfig());
        displayManager = new DisplayManager(engine, canvas);
        setupCanvasControls();
    
        displayManager.updateDisplay();
    }

    /**
     * Handles mouse clicks on the grid.
     * <p>
     * Depending on the current mode, this method either displays the clicked
     * cell information or drops water on the selected area.
     * </p>
     *
     * @param event The mouse event containing the click coordinates.
     */
    public void handleCanvasClick(MouseEvent event) {
        int col = displayManager.getColFromScreenX(event.getX());
        int row = displayManager.getRowFromScreenY(event.getY());

        Grid currentGrid = engine.getCurrentGrid();

        if (!currentGrid.isInside(row, col)) {
            return;
        }

        if (canadairMode) {
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

    /**
     * Active le mode ciblage du Canadair pour le prochain clic sur la grille.
     */
    @FXML
    private void activateCanadair() {
        if (!running) {
            canadairMode = true;
            canadairButton.setText("Choisir une zone");
        }
    }

    /**
     * Configures the mouse controls used on the canvas.
     * <p>
     * The user can drag the map with the left mouse button, zoom with the
     * mouse wheel and click on a cell to display its information or drop water
     * when Canadair mode is enabled.
     * </p>
     */
    private void setupCanvasControls() {
        canvas.setOnMousePressed(event -> {
            lastMouseX = event.getX();
            lastMouseY = event.getY();
            draggingCamera = true;
            mouseMovedDuringDrag = false;
        });

        canvas.setOnMouseDragged(event -> {
            if (!draggingCamera) {
                return;
            }

            double deltaX = event.getX() - lastMouseX;
            double deltaY = event.getY() - lastMouseY;

            if (Math.abs(deltaX) > 2 || Math.abs(deltaY) > 2) {
                mouseMovedDuringDrag = true;
            }

            displayManager.moveCamera(deltaX, deltaY);
            displayManager.updateDisplay();

            lastMouseX = event.getX();
            lastMouseY = event.getY();
        });

        canvas.setOnMouseReleased(event -> {
            draggingCamera = false;

            if (!mouseMovedDuringDrag) {
                handleCanvasClick(event);
            }
        });

        canvas.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;

            displayManager.zoomCameraAt(zoomFactor, event.getX(), event.getY());
            displayManager.updateDisplay();

            event.consume();
        });
    }
    
    /**
     * Met en pause ou relance la simulation automatique.
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
    
    /**
     * Pauses the automatic simulation and advances the simulation by one step.
     * <p>
     * This method allows the user to inspect the simulation step by step. If the
     * automatic timeline is running, it is paused before applying the next step.
     * The canvas is then redrawn to display the new grid state.
     * </p>
     */
    @FXML
    private void stepSimulation() {
        if (running) {
            timeline.pause();
            running = false;
            startButton.setText("Start");
        }

        engine.step();
        displayManager.updateDisplay();

        if (!displayManager.containsFire()) {
            running = false;
            startButton.setText("Start");
        }
    }

    /**
     * Stoppe la simulation en cours et génère une toute nouvelle forêt 
     * en utilisant les paramètres saisis par l'utilisateur.
     */
    @FXML
    private void handleResetSimulation() {
        timeline.pause();
        running = false;
        startButton.setText("Start");

        try {
            int minHum = Integer.parseInt(inputMinHumidity.getText());
            int minFuel = Integer.parseInt(inputMinFuel.getText());
            int maxHum = Integer.parseInt(inputMaxHumidity.getText());
            int maxFuel = Integer.parseInt(inputMaxFuel.getText());


            forest = new Grid(70, 70, minHum, minFuel, maxHum, maxFuel);
            forest.setCellState(24, 24, State.BURNING);

            engine = new SimulationEngine(forest, new PreventionFireAlgorithm(), new SimulationConfig());
            displayManager = new DisplayManager(engine, canvas);
            displayManager.updateDisplay();

        } catch (NumberFormatException e) {
            System.out.println("Erreur : Veuillez entrer des nombres valides dans les cases !");
        }
    }
}