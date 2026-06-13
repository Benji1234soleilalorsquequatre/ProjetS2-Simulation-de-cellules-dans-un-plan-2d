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
                    State.VEGETATION, randomHumidity, 0, randomFuel, vegetation
                );
            }
        }
    }

    /**
     * Constructeur personnalisé permettant de forcer les statistiques minimales et maximales des grands arbres.
     * Les broussailles (BRUSHWOOD) verront leurs statistiques réduites proportionnellement (en moyenne).
     * Utilisé pour redémarrer la simulation via l'interface graphique.
     *
     * @param height Le nombre de lignes.
     * @param width Le nombre de colonnes.
     * @param minHumidityTree Le pourcentage d'humidité minimum pour les arbres (TREE).
     * @param minFuelTree La quantité de bois minimale pour les arbres (TREE).
     * @param maxHumidityTree Le pourcentage d'humidité maximum pour les arbres (TREE).
     * @param maxFuelTree La quantité de bois maximale pour les arbres (TREE).
     */
    public Grid(int height, int width, int minHumidityTree, int minFuelTree, int maxHumidityTree, int maxFuelTree) {
        
        // 1. VALIDATION : Arrête le programme si les données reçues de l'IHM sont illogiques
        if (minFuelTree > maxFuelTree) {
            throw new IllegalArgumentException("Erreur de configuration : minFuelTree (" 
                + minFuelTree + ") ne peut pas être supérieur à maxFuelTree (" + maxFuelTree + ").");
        }
        if (minHumidityTree > maxHumidityTree) {
            throw new IllegalArgumentException("Erreur de configuration : minHumidityTree (" 
                + minHumidityTree + ") ne peut pas être supérieur à maxHumidityTree (" + maxHumidityTree + ").");
        }

        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Vegetation vegetation = (Math.random() < 0.3) ? Vegetation.BRUSHWOOD : Vegetation.TREE;
                int randomHumidity;
                int randomFuel;

                // Bornes par défaut (appliquées directement si c'est un TREE)
                int currentMinFuel = minFuelTree;
                int currentMaxFuel = maxFuelTree;
                int currentMinHumidity = minHumidityTree;
                int currentMaxHumidity = maxHumidityTree;

                // 2. ADAPTATION POUR LA BROUSSAILLE : Réduction proportionnelle de la moyenne
                if (vegetation == Vegetation.BRUSHWOOD) {
                    // La broussaille possède en moyenne 40% du combustible d'un arbre
                    if(maxFuelTree*0.80 < minFuelTree){
                        currentMaxFuel = minFuelTree;
                    }
                    else{
                    currentMaxFuel = (int) (maxFuelTree * 0.80);
                    }
                    currentMinFuel = minFuelTree;

                    // La broussaille est plus sèche, elle conserve 60% de l'humidité d'un arbre
                    if(maxHumidityTree*0.80 < minHumidityTree){
                        currentMaxHumidity = minHumidityTree;
                    }
                    else{
                    currentMaxHumidity = (int) (maxHumidityTree * 0.80);
                    }
                    currentMinHumidity = minHumidityTree;
                }

                // 3. CALCUL DE L'ALÉATOIRE (Gère de manière robuste le cas où la plage vaut 0)
                int fuelRange = currentMaxFuel - currentMinFuel;
                randomFuel = currentMinFuel + (fuelRange > 0 ? (int)(Math.random() * fuelRange) : 0);
                
                int humidityRange = currentMaxHumidity - currentMinHumidity;
                randomHumidity = currentMinHumidity + (humidityRange > 0 ? (int)(Math.random() * humidityRange) : 0);

                // 4. BRIDAGE DE SÉCURITÉ (Garantit des valeurs strictes entre 0% et 100%)
                randomFuel = Math.max(0, Math.min(100, randomFuel));
                randomHumidity = Math.max(0, Math.min(100, randomHumidity));

                this.forest[row][col] = new Cell(
                    State.VEGETATION, randomHumidity, 0, randomFuel, vegetation
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
     */
    public void setCellState(int row, int col, State state) {
        if (isInside(row, col)) {
            forest[row][col].setState(state);
        }
    }

    /**
     * Vérifie si des coordonnées sont bien à l'intérieur des limites de la matrice.
     */
    public boolean isInside(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Crée un clone parfait et indépendant de la grille actuelle.
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
            case VEGETATION: return "T";
            case BURNING: return "F";
            case ASH: return "A";
            case WATER: return "W";
            case FIREBREAK: return "#";
            default: return "?";
        }
    }
}

