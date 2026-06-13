package simulation;

import model.Cell;
import model.Grid;
import model.State;
import model.Wind;

/**
 * Advanced fire propagation algorithm considering all physical factors.
 * Uses 8-neighborhood (Moore neighborhood including diagonals) and calculates
 * spread probability based on wind, humidity, heat, fuel, and vegetation type.
 */
public class AdvancedFireAlgorithm
        implements FirePropagationAlgorithm {

    /**
     * 8 possible directions for fire propagation (Moore neighborhood).
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

        spreadFire(
                currentGrid,
                nextGrid,
                row,
                col,
                current,
                config
        );

        updateBurningCell(
                nextGrid.getCell(row, col),
                config
        );
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

            if (!currentGrid.isInside(
                    neighborRow,
                    neighborCol)) {
                continue;
            }

            Cell target = currentGrid.getCell(
                            neighborRow,
                            neighborCol);

            if (!target.canBurn()) {
                continue;
            }

            double probability = computeSpreadProbability(
                    source,
                    target,
                    dir,
                    config);

            if (Math.random() < probability) {

                nextGrid.getCell(
                        neighborRow,
                        neighborCol)
                        .ignite();
            }
        }
    }

    /**
     * Computes the probability that fire spreads to a target cell.
     * Factors include: humidity (reduces), heat (increases), wind alignment,
     * fuel content, and Canadair water effect.
     *
     * @param source    The burning source cell
     * @param target    The target cell at risk
     * @param direction The direction vector [dy, dx] from source to target
     * @param config    The simulation configuration with weights
     * @return A probability value between 0.0 and 1.0
     */
    private double computeSpreadProbability(
        Cell source,
        Cell target,
        int[] direction,
        SimulationConfig config) {

        double probability = config.getBaseSpreadProbability();

        // Humidity effect (higher humidity reduces spread probability)
        probability -= target.getHumidity() * config.getHumidityImpact();

        // Source heat effect (higher heat increases spread probability)
        probability += source.getHeat() * config.getHeatImpact();

        // Wind alignment effect
        Wind wind = config.getWind();

        int dx = direction[1];
        int dy = direction[0];

        double alignment = dx * wind.getWindX() - dy * wind.getWindY();

        // Clamp alignment to [-1, 1]
        alignment = Math.max(-1.0, Math.min(1.0, alignment));

        double windBonus = alignment * wind.getWindSpeed() * config.getWindImpact() * 0.1;
        probability += windBonus;

        // Fuel factor (more fuel increases spread probability)
        double fuelFactor = 0.5 + target.getFuel() / 200.0;
        probability *= fuelFactor;

        // Canadair water effect (greatly reduces probability if wet)
        if(target.getState() == State.WET){
            probability *= 0.05;
        }

        // Final clamping to [0.0, 1.0]
        probability = Math.max(0.0, Math.min(1.0, probability));

        return probability;
    }

    /**
     * Updates the state of a burning cell by consuming fuel.
     * When fuel is depleted, the cell turns to ash.
     *
     * @param cell   The burning cell to update
     * @param config The simulation configuration
     */
    private void updateBurningCell(
        Cell cell,
        SimulationConfig config) {

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