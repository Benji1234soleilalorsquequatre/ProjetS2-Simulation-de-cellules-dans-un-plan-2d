package model;

/**
 * Represents the 2D matrix of the forest.
 * Contains all cells and manages random generation of vegetation
 * (Trees and Brushwood) during initialization.
 */
public class Grid {

    private Cell[][] forest;
    private int height;
    private int width;

    /**
     * Default constructor. Generates a forest with humidity and fuel values
     * randomly generated according to vegetation type.
     *
     * @param height The number of rows in the grid
     * @param width  The number of columns in the grid
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
     * Custom constructor allowing to force minimum and maximum statistics for large trees.
     * Brushwood statistics are reduced proportionally on average.
     * Used to restart the simulation through the GUI.
     *
     * @param height            The number of rows
     * @param width             The number of columns
     * @param minHumidityTree   Minimum humidity percentage for trees (0-100)
     * @param minFuelTree       Minimum fuel amount for trees (0-100)
     * @param maxHumidityTree   Maximum humidity percentage for trees (0-100)
     * @param maxFuelTree       Maximum fuel amount for trees (0-100)
     */
    public Grid(int height, int width, int minHumidityTree, int minFuelTree, int maxHumidityTree, int maxFuelTree) {

        // 1. VALIDATION: Stop if received data is illogical
        if (minFuelTree > maxFuelTree) {
            throw new IllegalArgumentException("Configuration error: minFuelTree ("
                + minFuelTree + ") cannot exceed maxFuelTree (" + maxFuelTree + ").");
        }
        if (minHumidityTree > maxHumidityTree) {
            throw new IllegalArgumentException("Configuration error: minHumidityTree ("
                + minHumidityTree + ") cannot exceed maxHumidityTree (" + maxHumidityTree + ").");
        }

        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Vegetation vegetation = (Math.random() < 0.3) ? Vegetation.BRUSHWOOD : Vegetation.TREE;
                int randomHumidity;
                int randomFuel;

                // Default bounds (applied directly for TREE)
                int currentMinFuel = minFuelTree;
                int currentMaxFuel = maxFuelTree;
                int currentMinHumidity = minHumidityTree;
                int currentMaxHumidity = maxHumidityTree;

                // 2. BRUSHWOOD ADAPTATION: Proportional reduction of averages
                if (vegetation == Vegetation.BRUSHWOOD) {
                    // Brushwood has on average 80% of a tree's fuel
                    if(maxFuelTree*0.80 < minFuelTree){
                        currentMaxFuel = minFuelTree;
                    }
                    else{
                        currentMaxFuel = (int) (maxFuelTree * 0.80);
                    }
                    currentMinFuel = minFuelTree;

                    // Brushwood is drier, retains 80% of a tree's humidity
                    if(maxHumidityTree*0.80 < minHumidityTree){
                        currentMaxHumidity = minHumidityTree;
                    }
                    else{
                        currentMaxHumidity = (int) (maxHumidityTree * 0.80);
                    }
                    currentMinHumidity = minHumidityTree;
                }

                // 3. RANDOM CALCULATION (Handles robustly when range is 0)
                int fuelRange = currentMaxFuel - currentMinFuel;
                randomFuel = currentMinFuel + (fuelRange > 0 ? (int)(Math.random() * fuelRange) : 0);

                int humidityRange = currentMaxHumidity - currentMinHumidity;
                randomHumidity = currentMinHumidity + (humidityRange > 0 ? (int)(Math.random() * humidityRange) : 0);

                // 4. SAFETY CLAMPING (Guarantees strict values between 0% and 100%)
                randomFuel = Math.max(0, Math.min(100, randomFuel));
                randomHumidity = Math.max(0, Math.min(100, randomHumidity));

                this.forest[row][col] = new Cell(
                    State.VEGETATION, randomHumidity, 0, randomFuel, vegetation
                );
            }
        }
    }

    /** @return The height of the grid */
    public int getHeight() { return height; }

    /** @return The width of the grid */
    public int getWidth() { return width; }

    /**
     * Gets a cell at a specific position.
     *
     * @param row The row index
     * @param col The column index
     * @return The cell at the specified position, or null if out of bounds
     */
    public Cell getCell(int row, int col) {
        if (isInside(row, col)) {
            return forest[row][col];
        }
        return null;
    }

    /**
     * Manually replaces an entire cell in the grid.
     *
     * @param row  The row index
     * @param col  The column index
     * @param cell The new cell to place
     */
    public void setCell(int row, int col, Cell cell) {
        if (isInside(row, col)) {
            forest[row][col] = cell;
        }
    }

    /**
     * Changes only the state of an existing cell.
     *
     * @param row   The row index
     * @param col   The column index
     * @param state The new state
     */
    public void setCellState(int row, int col, State state) {
        if (isInside(row, col)) {
            forest[row][col].setState(state);
        }
    }

    /**
     * Checks if coordinates are within the grid bounds.
     *
     * @param row The row index
     * @param col The column index
     * @return true if coordinates are inside, false otherwise
     */
    public boolean isInside(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    /**
     * Creates a perfect independent clone of the current grid.
     *
     * @return A new Grid with copied cells
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

    /**
     * Displays the grid on the console using ASCII characters.
     */
    public void displayGrid() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                State currentState = forest[row][col].getState();
                System.out.print(getSymbolForState(currentState));
            }
            System.out.println();
        }
    }

    /**
     * Gets the ASCII symbol representing a cell state.
     *
     * @param state The cell state
     * @return A single character representing the state
     */
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

