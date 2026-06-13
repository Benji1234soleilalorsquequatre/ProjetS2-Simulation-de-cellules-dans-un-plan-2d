package simulation;

import model.Cell;
import model.Grid;
import model.State;
import model.Wind;

/**
 * Advanced fire propagation algorithm.
 * <p>
 * This algorithm models fire propagation using several environmental
 * factors such as humidity, heat, wind, fuel quantity, and water drops.
 * Fire can spread to any of the eight neighboring cells (Moore neighborhood),
 * with a probability computed from the physical parameters defined in the
 * simulation configuration.
 * </p>
 */
public class AdvancedFireAlgorithm
        implements FirePropagationAlgorithm {

    /**
     * The eight possible propagation directions (Moore neighborhood).
     */
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    /**
     * Applies the advanced fire propagation algorithm to a specific cell.
     * If burning, spreads fire to all 8 neighbors and consumes fuel.
     *
     * @param currentGrid The current grid state
     * @param nextGrid    The next grid state
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

        if (current.getState() != State.BURNING) {
            return;
        }

        spreadFire(currentGrid, nextGrid, row, col, current, config);

        updateBurningCell(nextGrid.getCell(row, col), config);
    }

    /**
     * Spreads fire from the source cell to all 8 neighbors using Moore neighborhood.
     * Each neighbor has a probability of catching fire based on physical parameters.
     *
     * @param currentGrid The current grid state
     * @param nextGrid    The next grid state
     * @param row         The row of the burning cell
     * @param col         The column of the burning cell
     * @param source      The burning cell
     * @param config      The simulation configuration
     */
    private void spreadFire(Grid currentGrid, Grid nextGrid, int row, int col, Cell source, SimulationConfig config) {

        for (int[] dir : DIRECTIONS) {

            int neighborRow = row + dir[0];
            int neighborCol = col + dir[1];

            if (!currentGrid.isInside(neighborRow, neighborCol)) {
                continue;
            }

            Cell target =
                    currentGrid.getCell(neighborRow, neighborCol);

            if (!target.canBurn()) {
                continue;
            }

            double probability =
                    computeSpreadProbability(source, target, dir, config);

            if (Math.random() < probability) {

                nextGrid.getCell(neighborRow, neighborCol).ignite();
            }
        }
    }

    /**
     * Computes the probability that fire spreads from a burning cell
     * to a target cell.
     * <p>
     * The probability depends on humidity, heat, wind conditions,
     * available fuel, and the presence of water.
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

        // Humidity influence
        probability -= target.getHumidity() * config.getHumidityImpact();

        // Heat influence
        probability += source.getHeat() * config.getHeatImpact();

        // Wind influence
        Wind wind = config.getWind();

        int dx = direction[1];
        int dy = direction[0];

        double alignment = dx * wind.getWindX() - dy * wind.getWindY();

        alignment = Math.max(-1.0, Math.min(1.0, alignment));

        double windBonus = alignment * wind.getWindSpeed() * config.getWindImpact() * 0.1;

        probability += windBonus;

        // Fuel influence
        double fuelFactor = 0.5 + target.getFuel() / 200.0;

        probability *= fuelFactor;

        // Water drop influence
        if (target.getState() == State.WET) {
            probability *= 0.05;
        }

        // Final clamping
        probability = Math.max(0.0, Math.min(1.0, probability));

        return probability;
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

            cell.setHeat(
                Math.max(20, Math.min(100, cell.getFuel()))
            );
        }
    }
}