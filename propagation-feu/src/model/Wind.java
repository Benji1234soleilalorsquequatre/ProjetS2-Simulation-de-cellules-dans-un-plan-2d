package model;

/**
 * Represents the wind conditions used in the simulation.
 * <p>
 * A wind is defined by its speed and its direction components
 * on the horizontal and vertical axes. These values are used
 * by fire propagation algorithms to influence the spread of fire.
 * </p>
 */
public class Wind {

    /** Wind speed intensity. */
    private int windSpeed;

    /** Horizontal component of the wind direction. */
    private int windX;

    /** Vertical component of the wind direction. */
    private int windY;

    /**
     * Creates a new wind configuration.
     *
     * @param windSpeed The wind speed.
     * @param windX The horizontal direction component.
     * @param windY The vertical direction component.
     */
    public Wind(int windSpeed, int windX, int windY) {
        this.windSpeed = windSpeed;
        this.windX = windX;
        this.windY = windY;
    }

    /**
     * Returns the wind speed.
     *
     * @return The wind speed.
     */
    public int getWindSpeed() {
        return this.windSpeed;
    }

    /**
     * Returns the horizontal component of the wind direction.
     *
     * @return The horizontal direction component.
     */
    public int getWindX() {
        return this.windX;
    }

    /**
     * Returns the vertical component of the wind direction.
     *
     * @return The vertical direction component.
     */
    public int getWindY() {
        return this.windY;
    }
}
