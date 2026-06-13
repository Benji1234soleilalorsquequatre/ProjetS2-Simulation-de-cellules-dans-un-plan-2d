package display;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Grid;
import model.Vegetation;
import model.State;
import simulation.SimulationEngine;
import model.Cell;

/**
 * Gère le rendu visuel de la simulation sur le Canvas JavaFX.
 * Transforme les données mathématiques de la grille en couleurs à l'écran.
 */
public class DisplayManager {

    private final SimulationEngine engine;
    private final Canvas canvas;
    private final int CELL_SIZE = 8;

    /**
     * Initialise le gestionnaire d'affichage.
     *
     * @param engine Le moteur de simulation contenant les données à afficher.
     * @param canvas La zone de dessin JavaFX.
     */
    public DisplayManager(SimulationEngine engine, Canvas canvas){
        this.engine = engine;
        this.canvas = canvas;
    }

    /**
     * Vérifie s'il reste au moins une case en feu sur toute la grille.
     * Permet au Contrôleur de savoir quand stopper l'animation automatique.
     *
     * @return true si un feu est en cours, false si tout est éteint.
     */
    public boolean containsFire() {
        Grid grid = engine.getCurrentGrid();
        for (int row = 0; row < grid.getHeight(); row++) {
            for (int col = 0; col < grid.getWidth(); col++) {
                if (grid.getCell(row, col).getState() == State.BURNING) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Parcourt l'intégralité de la grille et dessine chaque cellule avec
     * la couleur correspondante à son état et sa végétation.
     */
    public void updateDisplay() {
        Grid currentGrid = engine.getCurrentGrid();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // On efface la toile avant de redessiner
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int row = 0; row < currentGrid.getHeight(); row++) {
            for (int col = 0; col < currentGrid.getWidth(); col++) {
                State state = currentGrid.getCell(row, col).getState();
                
                switch (state) {
                    case VEGETATION:
                        if (currentGrid.getCell(row, col).getVegetation() == Vegetation.BRUSHWOOD) {
                            gc.setFill(Color.YELLOWGREEN); // Broussailles
                        } else {
                            gc.setFill(Color.DARKGREEN); // Arbres
                        }
                        break;
                    case BURNING: gc.setFill(Color.RED); break;
                    case ASH: gc.setFill(Color.DARKGRAY); break;
                    case FIREBREAK: gc.setFill(Color.BROWN); break;
                    case EMPTY: gc.setFill(Color.WHITE); break;
                    case WET: gc.setFill(Color.LIGHTBLUE); break;
                    case PREVENTIVE: gc.setFill(Color.ORANGE); break;
                }
               
                // Dessin du rectangle coloré
                gc.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                
                // Dessin de la bordure
                gc.setStroke(Color.web("#dcdcdc"));
                gc.setLineWidth(0.5);
                gc.strokeRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    /**
     * Simule le largage d'eau par un Canadair sur une zone circulaire.
     * Modifie l'état des cellules touchées en les rendant humides (WET).
     *
     * @param centerRow La ligne centrale du clic.
     * @param centerCol La colonne centrale du clic.
     */
    public void dropWater(int centerRow, int centerCol) {
        Grid grid = engine.getCurrentGrid();
        int radius = 2;

        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                if (dr * dr + dc * dc > radius * radius) {
                    continue; // Forme circulaire
                }

                int row = centerRow + dr;
                int col = centerCol + dc;

                if (!grid.isInside(row, col)) {
                    continue;
                }

                Cell cell = grid.getCell(row, col);
                if (cell.getState() == State.ASH) {
                    continue; // L'eau ne ressuscite pas les arbres brûlés
                }

                cell.setState(State.WET);
                cell.setWetTime(5);
                cell.setHumidity(100);
                cell.setHeat(20);
            }
        }
    }
}