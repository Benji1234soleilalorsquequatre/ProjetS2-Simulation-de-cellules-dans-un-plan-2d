package simulation;

import model.Cell;
import model.Grid;
import model.State;

/**
 * Simple fire propagation algorithm using only the 4-neighborhood (no diagonals).
 * Fire spreads to adjacent cells with a constant probability, regardless of
 * environmental factors like wind or heat.
 */
public class NaiveFireAlgorithm implements FirePropagationAlgorithm {

    /**
     * Applies the naive fire propagation to the specified cell.
     * If the cell is burning, it spreads fire to its 4 neighbors.
     *
     * @param currentGrid The current grid state
     * @param nextGrid    The next grid state to modify
     * @param row         The row of the cell to process
     * @param col         The column of the cell to process
     * @param config      The simulation configuration
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
     * Spreads fire to the 4 adjacent cells (North, South, East, West).
     * Each neighbor has a chance to ignite based on the base spread probability.
     *
     * @param currentGrid The current grid state
     * @param nextGrid    The next grid state
     * @param row         The row of the burning cell
     * @param col         The column of the burning cell
     * @param config      The simulation configuration
     */
    private void spreadToNeighbors(Grid currentGrid, Grid nextGrid, int row, int col, SimulationConfig config) {
        // 4-neighborhood: Up, Down, Left, Right
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
                double probability = config.getBaseSpreadProbability();

                if (Math.random() < probability) {
                    nextGrid.getCell(neighborRow, neighborCol).setState(State.BURNING);
                }
            }
        }
    }

    /**
     * Converts a burning cell to ash.
     *
     * @param cell The cell to burn
     */
    private void burnCurrentCell(Cell cell) {
        cell.setState(State.ASH);
    }
}