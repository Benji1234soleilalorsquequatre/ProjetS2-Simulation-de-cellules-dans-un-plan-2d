package simulation;

import model.Grid;

public interface FirePropagationAlgorithm {
    void apply(Grid currentGrid, Grid nextGrid, int row, int col, SimulationConfig config);
}