package simulation;
import model.Grid;

public class SimulationEngine {

    private Grid currentGrid;
    private FirePropagationAlgorithm algorithm;
    private SimulationConfig config;
    private int stepCounter;

    public SimulationEngine(Grid initialGrid, FirePropagationAlgorithm algorithm, SimulationConfig config) {
        this.currentGrid = initialGrid;
        this.algorithm = algorithm;
        this.config = config;
        this.stepCounter = 0;
    }

    public void step() {
        Grid nextGrid = currentGrid.copy();

        for (int row = 0; row < currentGrid.getHeight(); row++) {
            for (int col = 0; col < currentGrid.getWidth(); col++) {
                algorithm.apply(currentGrid, nextGrid, row, col, config);
            }
        }

        currentGrid = nextGrid;
        stepCounter++;
    }

    public Grid getCurrentGrid() {
        return currentGrid;
    }

    public int getStepCounter() {
        return stepCounter;
    }

    public void setAlgorithm(FirePropagationAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
