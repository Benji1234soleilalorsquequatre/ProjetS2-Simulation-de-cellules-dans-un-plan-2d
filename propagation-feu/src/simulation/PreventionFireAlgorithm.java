package simulation;

import model.Cell;
import model.Grid;
import model.State;
import model.Vegetation;
import model.Wind;

/**
 * Fire propagation algorithm with a prevention system.
 * Unlike standard algorithms, fire does not spread instantaneously.
 * Target cells first enter an alert state (PREVENTIVE) before catching fire,
 * allowing time for intervention (e.g., water drops from Canadair).
 */
public class PreventionFireAlgorithm implements FirePropagationAlgorithm {

    /**
     * 8 possible directions for fire propagation (Moore neighborhood).
     */
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    /**
     * Applies the prevention algorithm to a specific cell.
     * <ul>
     *   <li>RULE 1: If cell was PREVENTIVE, it becomes BURNING</li>
     *   <li>RULE 2: If cell is BURNING, it spreads and consumes fuel</li>
     * </ul>
     *
     * @param currentGrid The current grid state (read-only)
     * @param nextGrid    The next grid state (for modifications)
     * @param row         The row of the cell to process
     * @param col         The column of the cell to process
     * @param config      The simulation configuration
     */
    @Override
    public void apply(
            Grid currentGrid,
            Grid nextGrid,
            int row,
            int col,
            SimulationConfig config) {

        Cell current = currentGrid.getCell(row, col);

        // RULE 1: If cell was alert (PREVENTIVE), it catches fire (BURNING) next turn
        if (current.getState() == State.PREVENTIVE) {
            nextGrid.getCell(row, col).setState(State.BURNING);
            nextGrid.getCell(row, col).setHeat(100);
            return;
        }

        // If the cell is not burning, stop processing
        if (current.getState() != State.BURNING) {
            return;
        }

        // RULE 2: If cell is burning (BURNING), calculate fire spread to neighbors
        spreadFire(currentGrid, nextGrid, row, col, current, config);

        // And consume fuel of the burning cell
        updateBurningCell(nextGrid.getCell(row, col), config);
    }

    /**
     * Attempts to spread fire from the source cell to all valid neighbor cells.
     * Each neighbor's state is changed to PREVENTIVE if the spread probability succeeds.
     *
     * @param currentGrid The current grid state
     * @param nextGrid    The next grid state
     * @param row         The row index of the source cell
     * @param col         The column index of the source cell
     * @param source      The Cell object representing the fire source
     * @param config      The simulation configuration
     */
    private void spreadFire(
            Grid currentGrid,
            Grid nextGrid,
            int row,
            int col,
            Cell source,
            SimulationConfig config) {

        for (int[] dir : DIRECTIONS) {

            int neighborRow = row + dir[0];
            int neighborCol = col + dir[1];

            if (!currentGrid.isInside(neighborRow, neighborCol)) {
                continue;
            }

            Cell target = currentGrid.getCell(neighborRow, neighborCol);

            if (target.getState() != State.VEGETATION || !target.canBurn()) {
                continue;
            }

            double probability = computeSpreadProbability(source, target, dir, config);

            if (Math.random() < probability) {
                nextGrid.getCell(neighborRow, neighborCol).setState(State.PREVENTIVE);
            }
        }
    }

    /**
     * Computes the mathematical probability that fire spreads to a target cell.
     * Considers humidity, heat, wind alignment, and vegetation type.
     *
     * @param source    The burning source cell
     * @param target    The target cell at risk
     * @param direction The direction vector [dy, dx] from source to target
     * @param config    The configuration with impact weights
     * @return The final probability between 0.0 and 1.0
     */
    private double computeSpreadProbability(
            Cell source,
            Cell target,
            int[] direction,
            SimulationConfig config) {

        double probability = config.getBaseSpreadProbability();

        probability -= target.getHumidity() * config.getHumidityImpact();
        probability += source.getHeat() * config.getHeatImpact();

        Wind wind = config.getWind();
        int dx = direction[1];
        int dy = direction[0];

        double alignment = dx * wind.getWindX() - dy * wind.getWindY();
        alignment = Math.max(-1.0, Math.min(1.0, alignment));

        double windBonus = alignment * wind.getWindSpeed() * config.getWindImpact() * 0.1;
        probability += windBonus;

        double fuelFactor = 0.5 + target.getFuel() / 200.0;
        probability *= fuelFactor;

        // Vegetation effect
        if(target.getVegetation() == Vegetation.BRUSHWOOD){
            probability += 0.35; // Brushwood has higher fire spread probability
        } else {
            probability -= 0.05;
        }

        return Math.max(0.0, Math.min(1.0, probability));
    }

    /**
     * Updates the state of a burning cell by consuming fuel.
     * If fuel reaches zero, the cell becomes ash (ASH).
     *
     * @param cell   The burning cell to update
     * @param config The configuration specifying fuel consumption rate
     */
    private void updateBurningCell(Cell cell, SimulationConfig config) {
        int remainingFuel = (int) (cell.getFuel() - config.getFuelConsumption());
        cell.setFuel(Math.max(0, remainingFuel));

        if (cell.getFuel() <= 0) {
            cell.setState(State.ASH);
            cell.setHeat(20);
        } else {
            cell.setHeat(Math.max(20, Math.min(100, cell.getFuel())));
        }
    }
}