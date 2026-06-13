package simulation;

import model.Cell;
import model.Grid;
import model.State;
import model.Vegetation;
import model.Wind;

/**
 * Algorithme de propagation du feu incluant un système de prévention.
 * Contrairement à un algorithme classique, le feu ne se propage pas instantanément.
 * Les cellules cibles passent d'abord par un état d'alerte (PREVENTIVE) avant de brûler,
 * laissant le temps d'intervenir.
 */
public class PreventionFireAlgorithm implements FirePropagationAlgorithm {

    /**
     * Les 8 directions possibles pour la propagation (Voisinage de Moore).
     */
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            { 0, -1},          { 0, 1},
            { 1, -1}, { 1, 0}, { 1, 1}
    };

    /**
     * Applique l'algorithme de propagation sur une cellule spécifique de la grille.
     *
     * @param currentGrid La grille dans son état actuel (lecture seule).
     * @param nextGrid La grille du prochain tour (pour appliquer les modifications).
     * @param row La ligne de la cellule à traiter.
     * @param col La colonne de la cellule à traiter.
     * @param config Les paramètres de la simulation (vent, consommation, etc.).
     */
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
        spreadFire(currentGrid, nextGrid, row, col, current, config);

        // Et elle consomme son propre combustible (le bois de l'arbre)
        updateBurningCell(nextGrid.getCell(row, col), config);
    }

    /**
     * Tente de propager le feu de la cellule source vers toutes ses cellules voisines valides.
     *
     * @param currentGrid La grille actuelle.
     * @param nextGrid La future grille.
     * @param row L'index de ligne de la cellule source.
     * @param col L'index de colonne de la cellule source.
     * @param source L'objet Cell représentant la source du feu.
     * @param config Les paramètres de simulation.
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
     * Calcule la probabilité mathématique que le feu se propage à une cellule cible.
     * Prend en compte l'humidité, la chaleur, l'alignement du vent et le type de végétation.
     *
     * @param source La cellule en feu.
     * @param target La cellule cible qui risque de prendre feu.
     * @param direction Le vecteur de direction [y, x].
     * @param config Les poids d'impact (vent, humidité, etc.).
     * @return La probabilité finale comprise entre 0.0 et 1.0.
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

        // Avec la végétation
        if(target.getVegetation() == Vegetation.BRUSHWOOD){
            probability += 0.35; // Les broussailles ont plus de chances de prendre feu
        } else {
            probability -= 0.05;
        }

        return Math.max(0.0, Math.min(1.0, probability));
    }   

    /**
     * Met à jour l'état d'une cellule en train de brûler (consommation du combustible).
     * Si le combustible tombe à zéro, la cellule devient de la cendre (ASH).
     *
     * @param cell La cellule à mettre à jour.
     * @param config La configuration indiquant la vitesse de consommation du bois.
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