package simulation;
import model.Grid;
import model.Cell;
import model.State;

/**
 * Moteur principal de la simulation. 
 * Orchestre la boucle temporelle, applique les algorithmes de propagation
 * et gère les états globaux de la grille (comme l'évaporation de l'eau du Canadair).
 */
public class SimulationEngine {

    private Grid currentGrid;
    private FirePropagationAlgorithm algorithm;
    private SimulationConfig config;
    private int stepCounter;

    /**
     * Construit un nouveau moteur de simulation.
     *
     * @param initialGrid La grille de départ contenant la forêt.
     * @param algorithm L'algorithme de propagation du feu à utiliser.
     * @param config La configuration physique (vent, probabilités).
     */
    public SimulationEngine(Grid initialGrid, FirePropagationAlgorithm algorithm, SimulationConfig config) {
        this.currentGrid = initialGrid;
        this.algorithm = algorithm;
        this.config = config;
        this.stepCounter = 0;
    }

    /**
     * Exécute un "tour" complet de simulation (un pas de temps).
     * Duplique la grille, gère l'évaporation, applique le feu sur chaque cellule,
     * puis remplace l'ancienne grille par la nouvelle.
     */
    public void step() {
        Grid nextGrid = currentGrid.copy();

        updateWetCells(nextGrid);

        for (int row = 0; row < currentGrid.getHeight(); row++) {
            for (int col = 0; col < currentGrid.getWidth(); col++) {
                algorithm.apply(currentGrid, nextGrid, row, col, config);
            }
        }   

        currentGrid = nextGrid;
        stepCounter++;
    }

    /**
     * Gère l'évaporation de l'eau larguée par le Canadair.
     * Diminue l'humidité des cellules mouillées à chaque tour jusqu'à ce qu'elles redeviennent des arbres normaux.
     *
     * @param grid La grille sur laquelle appliquer l'évaporation.
     */
    private void updateWetCells(Grid grid) {
        for (int row = 0; row < grid.getHeight(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {

                Cell cell = grid.getCell(row, col);

                if (cell.getState() == State.WET) {
                    cell.setHumidity(Math.max(20, cell.getHumidity() - 4));

                    if (cell.getHumidity() <= 40) {
                        cell.setState(State.VEGETATION);
                    }
                }
            }
        }
    }

    /**
     * @return La grille dans son état actuel.
     */
    public Grid getCurrentGrid() {
        return currentGrid;
    }

    /**
     * @return Le nombre de tours (steps) écoulés depuis le début de la simulation.
     */
    public int getStepCounter() {
        return stepCounter;
    }
}