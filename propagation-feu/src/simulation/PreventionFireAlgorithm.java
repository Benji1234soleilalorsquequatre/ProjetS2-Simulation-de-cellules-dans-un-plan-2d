package simulation;

import model.Cell;
import model.Grid;
import model.State;
import model.Wind;

public class PreventionFireAlgorithm implements FirePropagationAlgorithm {

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

        Cell current = currentGrid.getCell(row, col);

        // RÈGLE 1 : Si la case était orange (PREVENTIVE), elle prend feu (BURNING) au tour suivant
        if (current.getState() == State.PREVENTIVE) {
            nextGrid.getCell(row, col).setState(State.BURNING);
            nextGrid.getCell(row, col).setHeat(100);
            return;
        }

        // Si la case n'est pas en feu, on s'arrête là
        if (current.getState() != State.BURNING) {
            return;
        }

        // RÈGLE 2 : Si la case est en feu (BURNING), elle calcule la propagation vers ses voisins
        spreadFire(
                currentGrid,
                nextGrid,
                row,
                col,
                current,
                config
        );

        // Et elle consomme son propre combustible (le bois de l'arbre)
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

            if (!currentGrid.isInside(neighborRow, neighborCol)) {
                continue;
            }

            Cell target = currentGrid.getCell(neighborRow, neighborCol);

            // On ne cherche à prévenir QUE les arbres encore vivants et sains
            if (target.getState() != State.TREE || !target.canBurn()) {
                continue;
            }

            // Reprise du calcul physique (Humidité, Vent, Chaleur, Bois) de l'algorithme avancé
            double probability = computeSpreadProbability(source, target, dir, config);

            // Si la probabilité réussit, on met l'arbre en alerte orange (PREVENTIVE) au lieu de rouge
            if (Math.random() < probability) {
                nextGrid.getCell(neighborRow, neighborCol).setState(State.PREVENTIVE);
            }
        }
    }

    private double computeSpreadProbability(
            Cell source,
            Cell target,
            int[] direction,
            SimulationConfig config) {

        double probability = config.getBaseSpreadProbability();

        // Influence de l'humidité
        probability -= target.getHumidity() * config.getHumidityImpact();

        // Influence de la chaleur accumulée par la source
        probability += source.getHeat() * config.getHeatImpact();

        // Influence du vent
        Wind wind = config.getWind();
        int dx = direction[1];
        int dy = direction[0];

        double alignment = dx * wind.getWindX() - dy * wind.getWindY();
        alignment = Math.max(-1.0, Math.min(1.0, alignment));

        double windBonus = alignment * wind.getWindSpeed() * config.getWindImpact() * 0.1;
        probability += windBonus;

        // Quantité de combustible disponible
        double fuelFactor = 0.5 + target.getFuel() / 200.0;
        probability *= fuelFactor;

        // Bornage de la probabilité finale entre 0.0 et 1.0
        probability = Math.max(0.0, Math.min(1.0, probability));

        return probability;
    }   

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
