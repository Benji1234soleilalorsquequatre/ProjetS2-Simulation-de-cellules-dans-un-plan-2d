package simulation;

import model.Grid;

/**
 * Interface for fire propagation algorithms.
 * Implementations define how fire spreads from one cell to its neighbors
 * based on physical parameters (wind, humidity, heat, fuel).
 */
public interface FirePropagationAlgorithm {

    /**
     * Applies the fire propagation algorithm to a specific cell.
     *
     * @param currentGrid The grid in its current state (read-only for source)
     * @param nextGrid    The grid for the next time step (where changes are applied)
     * @param row         The row index of the cell to process
     * @param col         The column index of the cell to process
     * @param config      The simulation configuration parameters
     */
    void apply(
        Grid currentGrid,
        Grid nextGrid,
        int row,
        int col,
        SimulationConfig config
    );
}