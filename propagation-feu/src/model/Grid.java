package model;

/**
 * Represents the 2D forest grid used by the simulation.
 * <p>
 * This class stores all cells of the forest and is responsible for
 * generating vegetation during initialization. Each cell is assigned
 * a vegetation type, humidity level, fuel quantity, and heat value.
 * </p>
 * <p>
 * The grid also provides utility methods for accessing, modifying,
 * copying, and displaying cells, as well as checking whether active
 * fires are still present.
 * </p>
 */
public class Grid {

    /** Two-dimensional array containing all forest cells. */
    private Cell[][] forest;

    /** Number of rows in the grid. */
    private int height;

    /** Number of columns in the grid. */
    private int width;

    /**
     * Creates a forest grid with randomly generated vegetation,
     * humidity, and fuel values.
     *
     * @param height The number of rows in the grid.
     * @param width The number of columns in the grid.
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
                    randomFuel = 50 + (int) (Math.random() * 51);
                    randomHumidity = 10 + (int) (Math.random() * 21);
                } else {
                    randomFuel = 10 + (int) (Math.random() * 11);
                    randomHumidity = (int) (Math.random() * 10);
                }

                this.forest[row][col] = new Cell(State.VEGETATION, randomHumidity, 20, randomFuel, vegetation);
            }
        }
    }

    /**
     * Creates a forest grid using custom vegetation parameters.
     * <p>
     * Tree humidity and fuel values are generated within the specified
     * ranges. Brushwood cells receive proportionally reduced values.
     * </p>
     *
     * @param height The number of rows.
     * @param width The number of columns.
     * @param minHumidityTree The minimum tree humidity.
     * @param minFuelTree The minimum tree fuel quantity.
     * @param maxHumidityTree The maximum tree humidity.
     * @param maxFuelTree The maximum tree fuel quantity.
     * @param heat The initial heat value assigned to cells.
     */
    public Grid(int height, int width, int minHumidityTree, int minFuelTree, int maxHumidityTree, int maxFuelTree, int heat) {

        if (minFuelTree > maxFuelTree) {
            throw new IllegalArgumentException("Configuration error: minFuelTree (" + minFuelTree + ") cannot be greater than maxFuelTree (" + maxFuelTree + ").");
        }

        if (minHumidityTree > maxHumidityTree) {
            throw new IllegalArgumentException("Configuration error: minHumidityTree (" + minHumidityTree + ") cannot be greater than maxHumidityTree (" + maxHumidityTree + ").");
        }

        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                Vegetation vegetation = (Math.random() < 0.3) ? Vegetation.BRUSHWOOD : Vegetation.TREE;

                int randomHumidity;
                int randomFuel;

                int currentMinFuel = minFuelTree;
                int currentMaxFuel = maxFuelTree;
                int currentMinHumidity = minHumidityTree;
                int currentMaxHumidity = maxHumidityTree;

                if (vegetation == Vegetation.BRUSHWOOD) {

                    if (maxFuelTree * 0.80 < minFuelTree) {
                        currentMaxFuel = minFuelTree;
                    } else {
                        currentMaxFuel = (int) (maxFuelTree * 0.80);
                    }

                    currentMinFuel = minFuelTree;

                    if (maxHumidityTree * 0.80 < minHumidityTree) {
                        currentMaxHumidity = minHumidityTree;
                    } else {
                        currentMaxHumidity = (int) (maxHumidityTree * 0.80);
                    }
                    currentMinHumidity = minHumidityTree;
                }

                int fuelRange = currentMaxFuel - currentMinFuel;
                randomFuel = currentMinFuel + (fuelRange > 0 ? (int) (Math.random() * fuelRange) : 0);

                int humidityRange = currentMaxHumidity - currentMinHumidity;

                randomHumidity = currentMinHumidity + (humidityRange > 0 ? (int) (Math.random() * humidityRange) : 0);

                randomFuel = Math.max(0, Math.min(100, randomFuel));
                randomHumidity = Math.max(0, Math.min(100, randomHumidity));

                this.forest[row][col] = new Cell(State.VEGETATION, randomHumidity, heat, randomFuel, vegetation);
            }
        }
    }

    /**
     * Checks whether there is at least one burning cell in the grid.
     *
     * @return true if at least one cell is burning, false otherwise.
     */
    public boolean containsFire() {
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                if (getCell(row, col).getState() == State.BURNING) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the grid height.
     *
     * @return The number of rows.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the grid width.
     *
     * @return The number of columns.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the cell located at the specified position.
     *
     * @param row The row index.
     * @param col The column index.
     * @return The requested cell, or null if the coordinates are invalid.
     */
    public Cell getCell(int row, int col) {
        if (isInside(row, col)) {
            return forest[row][col];
        }
        return null;
    }

    /**
     * Replaces a cell at the specified position.
     *
     * @param row The row index.
     * @param col The column index.
     * @param cell The new cell.
     */
    public void setCell(int row, int col, Cell cell) {
        if (isInside(row, col)) {
            forest[row][col] = cell;
        }
    }

    /**
     * Changes the state of an existing cell.
     *
     * @param row The row index.
     * @param col The column index.
     * @param state The new cell state.
     */
    public void setCellState(int row, int col, State state) {
        if (isInside(row, col)) {
            forest[row][col].setState(state);
        }
    }

    /**
     * Checks whether the specified coordinates are inside the grid bounds.
     *
     * @param row The row index.
     * @param col The column index.
     * @return true if the coordinates are valid, false otherwise.
     */
    public boolean isInside(int row, int col) {
        return row >= 0 && row < height
                && col >= 0 && col < width;
    }

    /**
     * Creates a deep copy of the grid.
     *
     * @return An independent copy of the current grid.
     */
    public Grid copy() {
        Grid copiedGrid = new Grid(height, width);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                copiedGrid.setCell(
                        row,
                        col,
                        forest[row][col].copy()
                );
            }
        }

        return copiedGrid;
    }

    /**
     * Displays the grid in the console using text symbols.
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
     * Returns the character used to represent a given cell state.
     *
     * @param state The cell state.
     * @return The display symbol associated with the state.
     */
    private String getSymbolForState(State state) {
        switch (state) {
            case VEGETATION:
                return "T";
            case BURNING:
                return "F";
            case ASH:
                return "A";
            default:
                return "?";
        }
    }
}