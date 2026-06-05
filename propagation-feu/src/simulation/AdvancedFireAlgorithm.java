package simulation;

import model.Cell;
import model.Grid;
import model.State;
import model.Wind;

public class AdvancedFireAlgorithm
        implements FirePropagationAlgorithm {

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    @Override
    public void apply(
            Grid currentGrid,
            Grid nextGrid,
            int row,
            int col,
            SimulationConfig config) {

        Cell current =
                currentGrid.getCell(row, col);

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

            Cell target =
                    currentGrid.getCell(
                            neighborRow,
                            neighborCol);

            if (!target.canBurn()) {
                continue;
            }

            double probability =
                    computeSpreadProbability(
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

    private double computeSpreadProbability(
        Cell source,
        Cell target,
        int[] direction,
        SimulationConfig config) {

    double probability =
            config.getBaseSpreadProbability();

    // Influence de l'humidité
    probability -=
            target.getHumidity()
            * config.getHumidityImpact();

    // Influence de la chaleur de la cellule source
    probability +=
            source.getHeat()
            * config.getHeatImpact();

    // Vent
    Wind wind = config.getWind();

    int dx = direction[1];
    int dy = direction[0];

    double alignment =
            dx * wind.getWindX()
          - dy * wind.getWindY();

    // Limite l'effet du vent à [-1 ; 1]
    alignment = Math.max(
            -1.0,
            Math.min(1.0, alignment)
    );

    double windBonus =
            alignment
            * wind.getWindSpeed()
            * config.getWindImpact()
            * 0.1;

    probability += windBonus;

    // Quantité de combustible
    double fuelFactor =
            0.5 + target.getFuel() / 200.0;

    probability *= fuelFactor;

    // Bornage final
    probability = Math.max(
            0.0,
            Math.min(1.0, probability)
    );

    return probability;
    }   

    private void updateBurningCell(
        Cell cell,
        SimulationConfig config) {

    int remainingFuel =
            (int) (cell.getFuel()
            - config.getFuelConsumption());

    cell.setFuel(
            Math.max(0, remainingFuel));

    if (cell.getFuel() <= 0) {

        cell.setState(State.ASH);
        cell.setHeat(0);

    } else {

        cell.setHeat(
            Math.max(
                    20,
                    Math.min(100, cell.getFuel())
            )
        );
        }
    }   
}