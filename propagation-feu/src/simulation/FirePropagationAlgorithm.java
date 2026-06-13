package simulation;

import model.Grid;

/**
 * Defines a fire propagation algorithm.
 * <p>
 * Implementations of this interface determine how fire evolves
 * from one simulation step to the next. The simulation engine
 * delegates the processing of each cell to an implementation
 * of this interface.
 * </p>
 */
public interface FirePropagationAlgorithm {

    /**
     * Applies the fire propagation logic to a specific cell.
     *
     * @param currentGrid The current grid state (read-only).
     * @param nextGrid The next grid state where updates are applied.
     * @param row The row index of the cell to process.
     * @param col The column index of the cell to process.
     * @param config The simulation configuration and physical parameters.
     */
    void apply(
        Grid currentGrid,
        Grid nextGrid,
        int row,
        int col,
        SimulationConfig config
    );
}