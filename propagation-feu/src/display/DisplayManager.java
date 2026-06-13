package display;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Cell;
import model.Grid;
import model.State;
import model.Vegetation;
import simulation.SimulationEngine;

/**
 * Manages the visual rendering of the simulation on a JavaFX canvas.
 * <p>
 * This class reads the current simulation grid and draws each cell
 * according to its state and vegetation type. It also owns the camera
 * used to zoom and move through the map.
 * </p>
 */
public class DisplayManager {

    private final SimulationEngine engine;
    private final Canvas canvas;
    private final Camera camera;

    private final int cellSize = 8;

    /**
     * Creates a display manager for a simulation engine and a JavaFX canvas.
     *
     * @param engine The simulation engine containing the grid to display.
     * @param canvas The JavaFX canvas used for rendering.
     */
    public DisplayManager(SimulationEngine engine, Canvas canvas) {
        this.engine = engine;
        this.canvas = canvas;
        this.camera = new Camera();
    }

    /**
     * Redraws the current simulation grid on the canvas using the camera.
     */
    public void updateDisplay() {
        Grid currentGrid = engine.getCurrentGrid();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double visibleCellSize = cellSize * camera.getZoom();

        for (int row = 0; row < currentGrid.getHeight(); row++) {
            for (int col = 0; col < currentGrid.getWidth(); col++) {
                double x = camera.worldToScreenX(col * cellSize);
                double y = camera.worldToScreenY(row * cellSize);

                if (isOutsideCanvas(x, y, visibleCellSize)) {
                    continue;
                }

                Cell cell = currentGrid.getCell(row, col);

                gc.setFill(getColorForCell(cell));
                gc.fillRect(x, y, visibleCellSize, visibleCellSize);

                if (visibleCellSize >= 4) {
                    gc.setStroke(Color.web("#dcdcdc"));
                    gc.setLineWidth(0.5);
                    gc.strokeRect(x, y, visibleCellSize, visibleCellSize);
                }
            }
        }
    }

    /**
     * Converts a screen X coordinate into a grid column.
     *
     * @param screenX The X coordinate on the canvas.
     * @return The corresponding grid column.
     */
    public int getColFromScreenX(double screenX) {
        double worldX = camera.screenToWorldX(screenX);
        return (int) (worldX / cellSize);
    }

    /**
     * Converts a screen Y coordinate into a grid row.
     *
     * @param screenY The Y coordinate on the canvas.
     * @return The corresponding grid row.
     */
    public int getRowFromScreenY(double screenY) {
        double worldY = camera.screenToWorldY(screenY);
        return (int) (worldY / cellSize);
    }

    /**
     * Moves the camera by the given screen-space offset.
     *
     * @param deltaX The horizontal movement in pixels.
     * @param deltaY The vertical movement in pixels.
     */
    public void moveCamera(double deltaX, double deltaY) {
        camera.move(deltaX, deltaY);
    }

    /**
     * Zooms the camera around the given screen position.
     *
     * @param zoomFactor The multiplicative zoom factor.
     * @param pivotX The X coordinate of the zoom pivot.
     * @param pivotY The Y coordinate of the zoom pivot.
     */
    public void zoomCameraAt(double zoomFactor, double pivotX, double pivotY) {
        camera.zoomAt(zoomFactor, pivotX, pivotY);
    }
    
    public Camera getCamera() {
        return camera;
    }

    /**
     * Resets the camera and redraws the map.
     */
    public void resetCamera() {
        camera.reset();
        updateDisplay();
    }

    /**
     * Checks whether there is at least one burning cell in the grid.
     *
     * @return true if at least one cell is burning, false otherwise.
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
     * Simulates a water drop on a circular area of the grid.
     *
     * @param centerRow The row of the water drop center.
     * @param centerCol The column of the water drop center.
     */
    public void dropWater(int centerRow, int centerCol, int radius) {
        Grid grid = engine.getCurrentGrid();

        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                if (dr * dr + dc * dc > radius * radius) {
                    continue;
                }

                int row = centerRow + dr;
                int col = centerCol + dc;

                if (!grid.isInside(row, col)) {
                    continue;
                }

                Cell cell = grid.getCell(row, col);

                if (cell.getState() == State.ASH) {
                    continue;
                }

                cell.setState(State.WET);
                cell.setWetTime(5);
                cell.setHumidity(100);
                cell.setHeat(20);
            }
        }
    }

    /**
     * Checks whether a cell rectangle is outside the visible canvas.
     *
     * @param x The X coordinate of the cell on the canvas.
     * @param y The Y coordinate of the cell on the canvas.
     * @param size The displayed cell size.
     * @return true if the cell is outside the canvas, false otherwise.
     */
    private boolean isOutsideCanvas(double x, double y, double size) {
        return x + size < 0
                || y + size < 0
                || x > canvas.getWidth()
                || y > canvas.getHeight();
    }

    /**
     * Returns the color used to draw a cell.
     *
     * @param cell The cell to draw.
     * @return The JavaFX color associated with the cell.
     */
    private Color getColorForCell(Cell cell) {
        State state = cell.getState();

        switch (state) {
            case VEGETATION:
                if (cell.getVegetation() == Vegetation.BRUSHWOOD) {
                    return Color.YELLOWGREEN;
                }

                return Color.DARKGREEN;

            case BURNING:
                return Color.RED;

            case ASH:
                return Color.DARKGRAY;

            case FIREBREAK:
                return Color.BROWN;

            case EMPTY:
                return Color.WHITE;

            case WET:
                return Color.LIGHTBLUE;

            case PREVENTIVE:
                return Color.ORANGE;

            default:
                return Color.BLACK;
        }
    }
}