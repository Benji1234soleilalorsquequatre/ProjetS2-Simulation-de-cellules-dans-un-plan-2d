package simulation;

import model.Cell;
import model.Grid;
import model.State;

/**
 * Simple fire propagation algorithm.
 * <p>
 * This algorithm uses a basic propagation model where fire spreads
 * only to the four orthogonal neighboring cells (Von Neumann neighborhood).
 * Each neighboring vegetation cell has a fixed probability of catching fire.
 * Burning cells are immediately converted to ash after spreading the fire.
 * </p>
 */
public class NaiveFireAlgorithm implements FirePropagationAlgorithm {

    /**
     * Applies the fire propagation algorithm to a specific cell.
     *
     * @param currentGrid The current grid state (read-only).
     * @param nextGrid The next grid state where updates are applied.
     * @param row The row index of the cell to process.
     * @param col The column index of the cell to process.
     * @param config The simulation configuration.
     */
    @Override
    public void apply(Grid currentGrid, Grid nextGrid, int row, int col, SimulationConfig config) {
        Cell cell = currentGrid.getCell(row, col);

        if (cell.getState() != State.BURNING) {
            return;
        }

        spreadToNeighbors(currentGrid, nextGrid, row, col, config);
        burnCurrentCell(nextGrid.getCell(row, col));
    }

    /**
     * Attempts to spread fire to the four orthogonal neighboring cells.
     * <p>
     * Each neighboring vegetation cell has a fixed probability
     * of catching fire.
     * </p>
     *
     * @param currentGrid The current grid.
     * @param nextGrid The next grid.
     * @param row The row index of the burning cell.
     * @param col The column index of the burning cell.
     * @param config The simulation configuration.
     */
    private void spreadToNeighbors(
            Grid currentGrid,
            Grid nextGrid,
            int row,
            int col,
            SimulationConfig config) {

        int[][] directions = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
        };

        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = col + direction[1];

            if (!currentGrid.isInside(neighborRow, neighborCol)) {
                continue;
            }

            Cell neighbor = currentGrid.getCell(neighborRow, neighborCol);

            if (neighbor.getState() == State.VEGETATION) {
                double probability = 0.5;

                if (Math.random() < probability) {
                    nextGrid.getCell(neighborRow, neighborCol).setState(State.BURNING);
                }
            }
        }
    }

    /**
     * Updates a burning cell after fire propagation.
     * <p>
     * In this simplified model, a burning cell immediately becomes ash
     * after spreading fire to its neighbors.
     * </p>
     *
     * @param cell The burning cell to update.
     */
    private void burnCurrentCell(Cell cell) {
        cell.setState(State.ASH);
    }
}