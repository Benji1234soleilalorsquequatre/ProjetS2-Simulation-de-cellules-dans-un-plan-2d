package model;

/**
 * The Grid class represents the forest grid used by the simulation.
 */
public class Grid {

    private Cell[][] forest;
    private int height;
    private int width;

    public Grid(int height, int width) {
        this.height = height;
        this.width = width;
        this.forest = new Cell[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.forest[row][col] = new Cell(State.TREE, 40, 0, 100);
            }
        }
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public Cell getCell(int row, int col) {
        if (isInside(row, col)) {
            return this.forest[row][col];
        }

        return null;
    }

    public void setCell(int row, int col, Cell cell) {
        if (isInside(row, col)) {
            this.forest[row][col] = cell;
        }
    }

    public void setCellState(int row, int col, State newState) {
        if (isInside(row, col)) {
            this.forest[row][col].setState(newState);
        }
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < height && col >= 0 && col < width;
    }

    public Grid copy() {
        Grid copiedGrid = new Grid(this.height, this.width);

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                copiedGrid.setCell(row, col, this.forest[row][col].copy());
            }
        }

        return copiedGrid;
    }

    public void displayGrid() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                State currentState = this.forest[row][col].getState();
                System.out.print(getSymbolForState(currentState));
            }

            System.out.println();
        }
    }

    private String getSymbolForState(State state) {
        switch (state) {
            case EMPTY:
                return ".";
            case TREE:
                return "T";
            case BURNING:
                return "F";
            case ASH:
                return "A";
            case WATER:
                return "W";
            case FIREBREAK:
                return "#";
            default:
                return "?";
        }
    }
}