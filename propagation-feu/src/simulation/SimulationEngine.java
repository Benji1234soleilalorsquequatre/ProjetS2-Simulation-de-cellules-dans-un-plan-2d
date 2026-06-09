package simulation;
import model.Grid;
import model.Cell;
import model.State;

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

        updateWetCells(nextGrid);

        for (int row = 0; row < currentGrid.getHeight(); row++) {
            for (int col = 0; col < currentGrid.getWidth(); col++) {
                algorithm.apply(currentGrid, nextGrid, row, col, config);
            }
        }   

        currentGrid = nextGrid;
        stepCounter++;
    }

    private void updateWetCells(Grid grid) {

        for (int row = 0; row < grid.getHeight(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {

                Cell cell = grid.getCell(row, col);

                if (cell.getState() == State.WET) {

                    cell.setHumidity(
                    Math.max(20, cell.getHumidity() - 4)
                    );

                    if (cell.getHumidity() <= 40) {
                        cell.setState(State.TREE);
                    }
                }
            }
        }
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