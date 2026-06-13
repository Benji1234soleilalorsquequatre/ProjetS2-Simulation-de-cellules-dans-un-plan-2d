package simulation;

import model.Grid;
import model.Cell;
import model.State;

/**
 * Main simulation engine.
 * <p>
 * This class manages the temporal evolution of the forest by applying
 * a fire propagation algorithm at each simulation step.
 * It stores the current state of the grid, updates wet cells,
 * and applies the physical parameters defined in the simulation
 * configuration.
 * </p>
 */
public class SimulationEngine {

    /** Grid representing the current state of the simulation. */
    private Grid currentGrid;

    /** Algorithm used to compute fire propagation. */
    private FirePropagationAlgorithm algorithm;

    /** Physical parameters of the simulation (wind, probabilities, etc.). */
    private SimulationConfig config;

    /** Number of simulation steps executed since the start. */
    private int stepCounter;

    /**
     * Creates a new simulation engine.
     *
     * @param initialGrid The initial grid containing the forest.
     * @param algorithm The fire propagation algorithm to use.
     * @param config The simulation configuration and physical parameters.
     */
    public SimulationEngine(Grid initialGrid, FirePropagationAlgorithm algorithm, SimulationConfig config) {
        this.currentGrid = initialGrid;
        this.algorithm = algorithm;
        this.config = config;
        this.stepCounter = 0;
    }

    /**
     * Executes a complete simulation step.
     * <p>
     * A copy of the current grid is created in order to compute the
     * next state without modifying the current one during processing.
     * Wet cells are updated first, then the propagation algorithm is
     * applied to every cell of the grid.
     * </p>
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
     * Updates wet cells in the grid.
     * <p>
     * At each simulation step, the humidity of watered cells
     * gradually decreases. When a cell becomes dry enough,
     * it returns to its normal vegetation state.
     * </p>
     *
     * @param grid The grid to update.
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
     * Returns the current simulation grid.
     *
     * @return The current grid state.
     */
    public Grid getCurrentGrid() {
        return currentGrid;
    }

    /**
     * Returns the number of simulation steps executed since the start
     * of the simulation.
     *
     * @return The simulation step counter.
     */
    public int getStepCounter() {
        return stepCounter;
    }
}
