package simulation;
import model.Grid;
import model.Cell;
import model.State;

/**
 * Main simulation engine orchestrating the temporal loop and fire propagation.
 * Applies fire propagation algorithms and manages global grid states
 * (such as water evaporation from the Canadair water bomber).
 */
public class SimulationEngine {

    private Grid currentGrid;
    private FirePropagationAlgorithm algorithm;
    private SimulationConfig config;
    private int stepCounter;

    /**
     * Constructs a new simulation engine.
     *
     * @param initialGrid The starting grid containing the forest
     * @param algorithm   The fire propagation algorithm to use
     * @param config      The physical configuration (wind, probabilities)
     */
    public SimulationEngine(Grid initialGrid, FirePropagationAlgorithm algorithm, SimulationConfig config) {
        this.currentGrid = initialGrid;
        this.algorithm = algorithm;
        this.config = config;
        this.stepCounter = 0;
    }

    /**
     * Executes one complete simulation step (one time unit).
     * Duplicates the grid, manages water evaporation, applies fire to each cell,
     * then replaces the old grid with the new one.
     */
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

    /**
     * Manages water evaporation from Canadair drops.
     * Decreases humidity of wet cells each turn until they return to normal vegetation state.
     *
     * @param grid The grid to apply evaporation to
     */
    private void updateWetCells(Grid grid) {
        for (int row = 0; row < grid.getHeight(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {

                Cell cell = grid.getCell(row, col);

                if (cell.getState() == State.WET) {
                    cell.setHumidity(Math.max(20, cell.getHumidity() - 4));

                    if (cell.getHumidity() <= 40) {
                        cell.setState(State.VEGETATION);
                    }
                }
            }
        }
    }

    /**
     * Returns the current grid state.
     *
     * @return The current grid
     */
    public Grid getCurrentGrid() {
        return currentGrid;
    }

    /**
     * Returns the number of steps since simulation start.
     *
     * @return The step counter value
     */
    public int getStepCounter() {
        return stepCounter;
    }

    /**
     * Allows changing which algorithm is used for fire propagation.
     *
     * @param algorithm The new algorithm to apply
     */
    public void setAlgorithm(FirePropagationAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}