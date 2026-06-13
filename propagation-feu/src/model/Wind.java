package model;

/**
 * Represents the wind conditions affecting fire propagation.
 * Wind has both magnitude (speed) and direction (x, y components).
 */
public class Wind {
    private int windSpeed;
    private int windX;
    private int windY;

    /**
     * Constructs a wind object with specified speed and direction.
     *
     * @param windSpeed The magnitude of the wind (typically 0-20)
     * @param windX     The X component of wind direction (typically -1 to 1)
     * @param windY     The Y component of wind direction (typically -1 to 1)
     */
    public Wind(int windSpeed, int windX, int windY) {
        this.windSpeed = windSpeed;
        this.windX = windX;
        this.windY = windY;
    }

    /**
     * Returns the wind speed magnitude.
     *
     * @return The wind speed value
     */
    public int getWindSpeed() {
        return this.windSpeed;
    }

    /**
     * Returns the X component of wind direction.
     *
     * @return The X direction value
     */
    public int getWindX() {
        return this.windX;
    }

    /**
     * Returns the Y component of wind direction.
     *
     * @return The Y direction value
     */
    public int getWindY() {
        return this.windY;
    }
}
