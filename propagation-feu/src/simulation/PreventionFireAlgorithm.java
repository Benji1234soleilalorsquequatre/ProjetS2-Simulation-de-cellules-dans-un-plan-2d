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
    public void apply(Grid currentGrid, Grid nextGrid, int row, int col, SimulationConfig config) {

        Cell current = currentGrid.getCell(row, col);

        // Rule 1: Preventive cells become burning cells at the next step.
        if (current.getState() == State.PREVENTIVE) {
            nextGrid.getCell(row, col).setState(State.BURNING);
            nextGrid.getCell(row, col).setHeat(100);
            return;
        }

        // Stop processing if the cell is not burning.
        if (current.getState() != State.BURNING) {
            return;
        }

        // Rule 2: Burning cells attempt to spread fire to their neighbors.
        spreadFire(currentGrid, nextGrid, row, col, current, config);

        // Burning cells consume their own fuel.
        updateBurningCell(nextGrid.getCell(row, col), config);
    }

    /**
     * Attempts to spread fire from a source cell to all valid neighboring cells.
     *
     * @param currentGrid The current grid.
     * @param nextGrid The next grid.
     * @param row The row index of the source cell.
     * @param col The column index of the source cell.
     * @param source The source burning cell.
     * @param config The simulation configuration.
     */
    private void spreadFire(Grid currentGrid, Grid nextGrid, int row, int col, Cell source, SimulationConfig config) {

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
     * Computes the probability that fire spreads from a burning cell
     * to a target cell.
     * <p>
     * The probability depends on several factors:
     * humidity, heat, wind direction and speed, fuel quantity,
     * and vegetation type.
     * </p>
     *
     * @param source The burning source cell.
     * @param target The target cell.
     * @param direction The propagation direction vector [row, column].
     * @param config The simulation parameters.
     * @return A probability value between 0.0 and 1.0.
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

        // Vegetation influence.
        if (target.getVegetation() == Vegetation.BRUSHWOOD) {
            probability += 0.35;
        } else {
            probability -= 0.05;
        }

        return Math.max(0.0, Math.min(1.0, probability));
    }

    /**
     * Updates a burning cell by consuming its fuel.
     * <p>
     * When the fuel reaches zero, the cell becomes ash ({@code ASH}).
     * Otherwise, its heat is adjusted according to the remaining fuel.
     * </p>
     *
     * @param cell The burning cell to update.
     * @param config The simulation configuration containing the fuel
     * consumption rate.
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