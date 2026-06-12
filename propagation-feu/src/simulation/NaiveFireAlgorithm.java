package simulation;

import model.Cell;
import model.Grid;
import model.State;

public class NaiveFireAlgorithm implements FirePropagationAlgorithm {

    /** 
     * @param currentGrid
     * @param nextGrid
     * @param row
     * @param col
     * @param config
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
     * @param currentGrid
     * @param nextGrid
     * @param row
     * @param col
     * @param config
     */
    private void spreadToNeighbors(Grid currentGrid, Grid nextGrid, int row, int col, SimulationConfig config) {
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

            if (neighbor.getState() == State.TREE) {
                double probability = config.getBaseSpreadProbability();

                if (Math.random() < probability) {
                    nextGrid.getCell(neighborRow, neighborCol).setState(State.BURNING);
                }
            }
        }
    }

    /** 
     * @param cell
     */
    private void burnCurrentCell(Cell cell) {
        cell.setState(State.ASH);
    }
}