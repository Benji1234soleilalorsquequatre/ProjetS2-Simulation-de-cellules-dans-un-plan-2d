package main;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import model.Grid;
import model.State;
import model.Cell;
import model.Wind;
import simulation.FirePropagationAlgorithm;
import simulation.NaiveFireAlgorithm;
import simulation.AdvancedFireAlgorithm;
import simulation.PreventionFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;
import display.DisplayManager;

/**
 * JavaFX controller that manages user interactions with the interface.
 * Acts as a bridge between the display (DisplayManager) and the logical engine
 * (SimulationEngine).
 */
public class Controller {

    @FXML
    private Canvas canvas;
    @FXML
    private TextField inputMinHumidity;
    @FXML
    private TextField inputMinFuel;
    @FXML
    private TextField inputMaxHumidity;
    @FXML
    private TextField inputMaxFuel;
    @FXML
    private TextField inputSpeed;
    @FXML
    private TextField inputCanadairRadius;
    @FXML
    private TextField inputHeat;
    @FXML
    private Button canadairButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stepButton;
    @FXML
    private Label stateLabel;
    @FXML
    private Label humidityLabel;
    @FXML
    private Label heatLabel;
    @FXML
    private Label fuelLabel;
    @FXML
    private TextField inputGridWidth;
    @FXML
    private TextField inputGridHeight;
    @FXML
    private TextField inputWindSpeed;
    @FXML
    private ComboBox<String> windDirectionCombo;
    @FXML
    private ComboBox<String> algorithmComboBox;

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
     * Method called automatically when the application starts.
     * Configures the animation loop (Timeline) and initializes the first forest.
     */
    @FXML
    public void initialize() {

        algorithmComboBox.getItems().setAll("Naive", "Advanced", "Prevention");

        algorithmComboBox.setValue("Prevention");

        createTimeline(200);

        forest = new Grid(70, 70);
        forest.setCellState(35, 35, State.BURNING); // Initial fire source
        windDirectionCombo.setValue("Nord");

        engine = new SimulationEngine(forest, createSelectedAlgorithm(), new SimulationConfig());
        displayManager = new DisplayManager(engine, canvas);

        double cellSize = 8;

        double offsetX = (canvas.getWidth() - forest.getWidth() * cellSize) / 2;
        double offsetY = (canvas.getHeight() - forest.getHeight() * cellSize) / 2;

        displayManager.getCamera().setPosition(offsetX, offsetY);

        setupCanvasControls();

        displayManager.updateDisplay();
    }

    private void createTimeline(double speed) {
        timeline = new Timeline(new KeyFrame(Duration.millis(speed), e -> {
            engine.step();
            displayManager.updateDisplay();

            if (!engine.getCurrentGrid().containsFire()) {
                timeline.stop();
                running = false;
                startButton.setText("Start");
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
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
            int radius = Integer.parseInt(inputCanadairRadius.getText());
            radius = Math.max(1, Math.min(10, radius));

            displayManager.dropWater(row, col, radius);

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
     * Activates the Canadair targeting mode for the next click on the grid.
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

        if (!engine.getCurrentGrid().containsFire()) {
            running = false;
            startButton.setText("Start");
        }
    }

    /**
     * Creates the fire propagation algorithm selected by the user.
     * <p>
     * This method applies the Strategy pattern. The simulation engine receives
     * a FirePropagationAlgorithm without needing to know which concrete algorithm
     * was selected in the graphical interface.
     * </p>
     *
     * @return The fire propagation algorithm selected by the user.
     */
    private FirePropagationAlgorithm createSelectedAlgorithm() {
        String selectedAlgorithm = algorithmComboBox.getValue();

        if ("Naive".equals(selectedAlgorithm)) {
            return new NaiveFireAlgorithm();
        }

        if ("Advanced".equals(selectedAlgorithm)) {
            return new AdvancedFireAlgorithm();
        }

        if ("Prevention".equals(selectedAlgorithm)) {
            return new PreventionFireAlgorithm();
        }

        return new PreventionFireAlgorithm();
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
            int gridWidth = Integer.parseInt(inputGridWidth.getText());
            int gridHeight = Integer.parseInt(inputGridHeight.getText());
            int heat = Integer.parseInt(inputHeat.getText());
            int windSpeed = Integer.parseInt(inputWindSpeed.getText());
            String windDirection = windDirectionCombo.getValue();

            int dx = 0;
            int dy = 0;

            switch (windDirection) {
                case ("Nord"):
                    dx = 0;
                    dy = 1;
                    break;
                case "Sud":
                    dx = 0;
                    dy = -1;
                    break;
                case "Est":
                    dx = 1;
                    dy = 0;
                    break;
                case "Ouest":
                    dx = -1;
                    dy = 0;
                    break;
                case "Nord-Est":
                    dx = 1;
                    dy = 1;
                    break;
                case "Nord-Ouest":
                    dx = -1;
                    dy = 1;
                    break;
                case "Sud-Est":
                    dx = 1;
                    dy = -1;
                    break;
                case "Sud-Ouest":
                    dx = -1;
                    dy = -1;
                    break;
            }

            Wind wind = new Wind(windSpeed, dx, dy);
            SimulationConfig config = new SimulationConfig();
            config.setWind(wind);

            gridHeight = Math.max(10, Math.min(200, gridHeight));
            gridWidth = Math.max(10, Math.min(200, gridWidth));

            int speed = Integer.parseInt(inputSpeed.getText());

            forest = new Grid(gridHeight, gridWidth, minHum, minFuel, maxHum, maxFuel, heat);
            forest.setCellState(gridHeight / 2, gridWidth / 2, State.BURNING);

            FirePropagationAlgorithm selectedAlgorithm = createSelectedAlgorithm();
            engine = new SimulationEngine(forest, selectedAlgorithm, config);

            displayManager = new DisplayManager(engine, canvas);
            createTimeline(speed);
            double cellSize = 8;

            double offsetX = (canvas.getWidth() - forest.getWidth() * cellSize) / 2;
            double offsetY = (canvas.getHeight() - forest.getHeight() * cellSize) / 2;

            displayManager.getCamera().setPosition(offsetX, offsetY);
            displayManager.updateDisplay();

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers in the input fields!");
        }
    }
}