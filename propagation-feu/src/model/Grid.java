package model;

/**
 * Représente la matrice 2D de la forêt.
 * Contient toutes les cellules et gère la génération aléatoire de la végétation
 * (Grands arbres et Broussailles) lors de son initialisation.
 */
public class Grid {

    private Cell[][] forest;
    private int height;
    private int width;

    /**
     * Constructeur par défaut. Génère une forêt avec des valeurs d'humidité
     * et de combustible générées aléatoirement selon le type de végétation.
     *
     * @param height Le nombre de lignes de la grille.
     * @param width Le nombre de colonnes de la grille.
     */
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                Vegetation vegetation = (Math.random() < 0.3) ? Vegetation.BRUSHWOOD : Vegetation.TREE;
                int randomHumidity;
                int randomFuel;

                if (vegetation == Vegetation.TREE) {
                    randomFuel = 50 + (int)(Math.random() * 51); 
                    randomHumidity = 10 + (int)(Math.random() * 21); 
                } else {
                    randomFuel = 10 + (int)(Math.random() * 11); 
                    randomHumidity = 0 + (int)(Math.random() * 10); 
                }

                this.forest[row][col] = new Cell(
                    State.TREE, randomHumidity, 0, randomFuel, vegetation
                );
            }
        }
    }

    /**
     * Constructeur personnalisé permettant de forcer les statistiques minimales des grands arbres.
     * Utilisé pour redémarrer la simulation via l'interface graphique.
     *
     * @param height Le nombre de lignes.
     * @param width Le nombre de colonnes.
     * @param minHumidityTree Le pourcentage d'humidité minimum pour les arbres (TREE).
     * @param minFuelTree La quantité de bois minimale pour les arbres (TREE).
     */
    public Grid(int height, int width, int minHumidityTree, int minFuelTree) {
        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Vegetation vegetation = (Math.random() < 0.3) ? Vegetation.BRUSHWOOD : Vegetation.TREE;
                int randomHumidity;
                int randomFuel;

                if (vegetation == Vegetation.TREE) {
                    randomFuel = minFuelTree + (int)(Math.random() * 51); 
                    if(randomFuel < 0) {
                        randomFuel = 0;
                    }
                    if(randomFuel > 100) {
                        randomFuel = 100;
                    }
                    randomHumidity = minHumidityTree + (int)(Math.random() * 21);
                    if(randomHumidity < 0) {
                        randomHumidity = 0;
                    }
                    if(randomHumidity > 100) {
                        randomHumidity = 100;
                    } 
                } else {
                    randomFuel = minFuelTree - 40 + (int)(Math.random() * 11);
                    if(randomFuel < 0) {
                        randomFuel = 0;
                    }
                    if(randomFuel > 100) {
                        randomFuel = 100;
                    }  
                    randomHumidity = minHumidityTree - 10 + (int)(Math.random() * 10);
                    if(randomHumidity < 0) {
                        randomHumidity = 0;
                    }
                    if(randomHumidity > 100) {
                        randomHumidity = 100;
                    }
                }

                this.forest[row][col] = new Cell(
                    State.TREE, randomHumidity, 0, randomFuel, vegetation
                );
            }
        }
    }

    /** @return La hauteur de la grille. */
    public int getHeight() { return height; }

    /** @return La largeur de la grille. */
    public int getWidth() { return width; }

    /**
     * Récupère une cellule à une position spécifique.
     *
     * @param row L'index de la ligne.
     * @param col L'index de la colonne.
     * @return La cellule ciblée, ou null si les coordonnées sont hors limites.
     */
    public Cell getCell(int row, int col) {
        if (isInside(row, col)) {
            return forest[row][col];
        }
        return null;
    }

    /**
     * Modifie manuellement une cellule entière dans la grille.
     */
    public void setCell(int row, int col, Cell cell) {
        if (isInside(row, col)) {
            forest[row][col] = cell;
        }
    }

    /**
     * Modifie uniquement l'état (State) d'une cellule existante.
     * Utilisé pour allumer le premier feu par exemple.
     */
    public void setCellState(int row, int col, State state) {
        if (isInside(row, col)) {
            forest[row][col].setState(state);
        }
    }

    /**
     * Vérifie si des coordonnées sont bien à l'intérieur des limites de la matrice.
     *
     * @param row La ligne à vérifier.
     * @param col La colonne à vérifier.
     * @return true si la case existe, false si elle déborde de la carte.
     */
    public boolean isInside(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Crée un clone parfait et indépendant de la grille actuelle.
     * Indispensable pour calculer le tour de simulation suivant sans altérer le tour en cours.
     *
     * @return Une nouvelle instance de Grid contenant les copies de chaque Cellule.
     */
    public Grid copy() {
        Grid copiedGrid = new Grid(height, width);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                copiedGrid.setCell(row, col, forest[row][col].copy());
            }
        }
        return copiedGrid;
    }

    // ===== DISPLAY =====
    // Les méthodes d'affichage console sont omises de la Javadoc car elles servent principalement au debug.
    public void displayGrid() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                State currentState = forest[row][col].getState();
                System.out.print(getSymbolForState(currentState));
            }
            System.out.println();
        }
    }

    private String getSymbolForState(State state) {
        switch (state) {
            case EMPTY: return ".";
            case TREE: return "T";
            case BURNING: return "F";
            case ASH: return "A";
            case WATER: return "W";
            case FIREBREAK: return "#";
            default: return "?";
        }
    }
}
