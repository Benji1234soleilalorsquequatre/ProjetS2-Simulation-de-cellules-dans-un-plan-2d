package display;

/**
 * Represents a 2D camera used to navigate through the simulation map.
 * <p>
 * The camera stores a zoom factor and two offsets. It converts world
 * coordinates, expressed in pixels before camera transformation, into
 * screen coordinates displayed on the JavaFX canvas.
 * </p>
 */
public class Camera {

    private double zoom;
    private double offsetX;
    private double offsetY;

    private final double minZoom;
    private final double maxZoom;

    /**
     * Creates a camera with default position and zoom values.
     */
    public Camera() {
        this.zoom = 1.0;
        this.offsetX = 0.0;
        this.offsetY = 0.0;
        this.minZoom = 0.3;
        this.maxZoom = 5.0;
    }

    /**
     * Converts a world X coordinate into a screen X coordinate.
     *
     * @param worldX The X coordinate in the simulation world.
     * @return The corresponding X coordinate on the canvas.
     */
    public double worldToScreenX(double worldX) {
        return worldX * zoom + offsetX;
    }

    /**
     * Converts a world Y coordinate into a screen Y coordinate.
     *
     * @param worldY The Y coordinate in the simulation world.
     * @return The corresponding Y coordinate on the canvas.
     */
    public double worldToScreenY(double worldY) {
        return worldY * zoom + offsetY;
    }

    /**
     * Converts a screen X coordinate into a world X coordinate.
     *
     * @param screenX The X coordinate on the canvas.
     * @return The corresponding X coordinate in the simulation world.
     */
    public double screenToWorldX(double screenX) {
        return (screenX - offsetX) / zoom;
    }

    /**
     * Converts a screen Y coordinate into a world Y coordinate.
     *
     * @param screenY The Y coordinate on the canvas.
     * @return The corresponding Y coordinate in the simulation world.
     */
    public double screenToWorldY(double screenY) {
        return (screenY - offsetY) / zoom;
    }

    /**
     * Moves the camera by a given amount in screen coordinates.
     *
     * @param deltaX The horizontal movement in pixels.
     * @param deltaY The vertical movement in pixels.
     */
    public void move(double deltaX, double deltaY) {
        this.offsetX += deltaX;
        this.offsetY += deltaY;
    }

    /**
     * Zooms the camera around a specific point on the canvas.
     * <p>
     * The point under the mouse cursor stays stable while zooming, which
     * makes navigation more natural for the user.
     * </p>
     *
     * @param zoomFactor The multiplicative zoom factor.
     * @param pivotScreenX The X coordinate of the zoom pivot on the canvas.
     * @param pivotScreenY The Y coordinate of the zoom pivot on the canvas.
     */
    public void zoomAt(double zoomFactor, double pivotScreenX, double pivotScreenY) {
        double worldXBeforeZoom = screenToWorldX(pivotScreenX);
        double worldYBeforeZoom = screenToWorldY(pivotScreenY);

        zoom *= zoomFactor;

        if (zoom < minZoom) {
            zoom = minZoom;
        }

        if (zoom > maxZoom) {
            zoom = maxZoom;
        }

        offsetX = pivotScreenX - worldXBeforeZoom * zoom;
        offsetY = pivotScreenY - worldYBeforeZoom * zoom;
    }

    /**
     * Resets the camera position and zoom level.
     */
    public void reset() {
        this.zoom = 1.0;
        this.offsetX = 0.0;
        this.offsetY = 0.0;
    }

    public void setPosition(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Returns the current zoom level.
     *
     * @return The current zoom factor.
     */
    public double getZoom() {
        return zoom;
    }
}