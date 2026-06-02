package model;

/**
 * Represents the forest grid.
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

                this.forest[row][col] =
                        new Cell(
                                State.TREE,
                                40,
                                0,
                                100
                        );
            }
        }
    }

    // ===== DIMENSIONS =====

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    // ===== CELLS =====

    public Cell getCell(int row, int col) {

        if (isInside(row, col)) {

            return forest[row][col];
        }

        return null;
    }

    public void setCell(int row,
                        int col,
                        Cell cell) {

        if (isInside(row, col)) {

            forest[row][col] = cell;
        }
    }

    public void setCellState(int row,
                             int col,
                             State state) {

        if (isInside(row, col)) {

            forest[row][col]
                    .setState(state);
        }
    }

    // ===== UTILITIES =====

    public boolean isInside(int row,
                            int col) {

        return row >= 0
                && row < height
                && col >= 0
                && col < width;
    }

    public Grid copy() {

        Grid copiedGrid =
                new Grid(height, width);

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

    // ===== DISPLAY =====

    public void displayGrid() {

        for (int row = 0; row < height; row++) {

            for (int col = 0; col < width; col++) {

                State currentState =
                        forest[row][col]
                                .getState();

                System.out.print(
                        getSymbolForState(currentState)
                );
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