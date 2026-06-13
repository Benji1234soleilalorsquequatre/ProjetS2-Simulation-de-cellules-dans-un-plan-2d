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
 * JavaFX controller managing user interactions with the interface.
 * Bridges the display (DisplayManager) and simulation logic (SimulationEngine).
 */
public class Controller {

    @FXML private Canvas canvas;
    @FXML private TextField inputMinHumidity;
    @FXML private TextField inputMinFuel;
    @FXML private TextField inputMaxHumidity;
    @FXML private TextField inputMaxFuel;
    @FXML private TextField inputSpeed;
    @FXML private TextField inputCanadairRadius;
    @FXML private TextField inputHeat;
    @FXML private Button canadairButton;
    @FXML private Button startButton;
    @FXML private Button stepButton;
    @FXML private Label stateLabel;
    @FXML private Label humidityLabel;
    @FXML private Label heatLabel;
    @FXML private Label fuelLabel;
    @FXML private TextField inputGridWidth;
    @FXML private TextField inputGridHeight;

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
     * Called automatically when the application starts.
     * Sets up the animation loop and initializes the first forest grid.
     */
    @FXML
    public void initialize() {

        createTimeline(200);

        forest = new Grid(70, 70);
        forest.setCellState(24, 24, State.BURNING); // Initial fire source

        engine = new SimulationEngine(forest, new PreventionFireAlgorithm(), new SimulationConfig());
        displayManager = new DisplayManager(engine, canvas);

        double cellSize = 8;

        double offsetX = (canvas.getWidth() - forest.getWidth() * cellSize) / 2;
        double offsetY = (canvas.getHeight() - forest.getHeight() * cellSize) / 2;

        displayManager.getCamera().setPosition(offsetX, offsetY);

        setupCanvasControls();

        displayManager.updateDisplay();
    }

    /**
     * Creates the animation timeline that drives the simulation.
     *
     * @param speed The milliseconds between each simulation step
     */
    private void createTimeline(double speed) {
        timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> {
            engine.step();
            displayManager.updateDisplay();

            if (!displayManager.containsFire()) {
                timeline.stop();
                running = false;
                startButton.setText("Start");
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Handles mouse clicks on the grid.
     * Depending on the current mode, displays cell information or drops water.
     *
     * @param event The mouse event containing the click coordinates
     */
    public void handleCanvasClick(MouseEvent event) {
        int col = displayManager.getColFromScreenX(event.getX());
        int row = displayManager.getRowFromScreenY(event.getY());

        Grid currentGrid = engine.getCurrentGrid();

        if (!currentGrid.isInside(row, col)) {
            return;
        }

        if (canadairMode) {

            int radius = Integer.parseInt(inputCanadairRadius.getText());

            radius = Math.max(1, Math.min(10, radius));

            displayManager.dropWater(row, col, radius);

            canadairMode = false;
            canadairButton.setText("Canadair");
            displayManager.updateDisplay();
            return;
        }

        Cell cell = currentGrid.getCell(row, col);

        stateLabel.setText("State: " + cell.getState());
        humidityLabel.setText("Humidity: " + cell.getHumidity());
        heatLabel.setText("Heat: " + cell.getHeat());
        fuelLabel.setText("Fuel: " + cell.getFuel());
    }

    /**
     * Activates Canadair mode for the next grid click.
     * Allows the user to drop water on a selected area.
     */
    @FXML
    private void activateCanadair() {
        if (!running) {
            canadairMode = true;
            canadairButton.setText("Select area");
        }
    }

    /**
     * Configures mouse controls for the canvas.
     * Left-click drag moves the map, mouse wheel zooms, single clicks interact with cells.
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
     * Pauses or resumes the automatic simulation.
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
     * Advances the simulation by one step and pauses automatic playback.
     * Allows step-by-step inspection of the simulation.
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
     * Stops the current simulation and generates a new forest
     * using user-specified parameters.
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
            int gridWidth= Integer.parseInt(inputGridWidth.getText());
            int gridHeight= Integer.parseInt(inputGridHeight.getText());
            int heat = Integer.parseInt(inputHeat.getText());

            int speed = Integer.parseInt(inputSpeed.getText());


            forest = new Grid(gridHeight, gridWidth, minHum, minFuel, maxHum, maxFuel, heat);
            forest.setCellState(gridHeight/2,gridWidth/2, State.BURNING);

            engine = new SimulationEngine(forest, new PreventionFireAlgorithm(), new SimulationConfig());
            displayManager = new DisplayManager(engine, canvas);
            createTimeline(speed);
            double cellSize = 8;

            double offsetX = (canvas.getWidth() - forest.getWidth() * cellSize) / 2;
            double offsetY = (canvas.getHeight() - forest.getHeight() * cellSize) / 2;

            displayManager.getCamera().setPosition(offsetX, offsetY);
            displayManager.updateDisplay();

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers in all fields!");
        }
    }
}