package model;

/**
 * Represents a single cell in the forest grid.
 * <p>
 * Each cell stores its current state, vegetation type, and physical
 * properties such as humidity, heat, and fuel. These properties are
 * used by fire propagation algorithms to determine how the fire evolves
 * throughout the simulation.
 * </p>
 */
public class Cell {

    /** Current state of the cell. */
    private State state;

    /** Type of vegetation contained in the cell. */
    private Vegetation vegetation;

    /** Humidity level of the cell. */
    private int humidity;

    /** Heat level of the cell. */
    private int heat;

    /** Amount of fuel available for burning. */
    private int fuel;

    /** Remaining duration of the wet state. */
    private int wetTime = 0;

    /**
     * Creates a new cell with the specified properties.
     *
     * @param state The initial state of the cell.
     * @param humidity The humidity level.
     * @param heat The heat level.
     * @param fuel The amount of available fuel.
     * @param vegetation The vegetation type.
     */
    public Cell(State state, int humidity, int heat, int fuel, Vegetation vegetation) {
        this.state = state;
        this.humidity = humidity;
        this.heat = heat;
        this.fuel = fuel;
        this.vegetation = vegetation;
    }

    /**
     * Creates a deep copy of this cell.
     *
     * @return A new cell containing the same values.
     */
    public Cell copy() {
        Cell copy = new Cell(state, humidity, heat, fuel, vegetation);
        copy.wetTime = this.wetTime;
        return copy;
    }

   
    // ===== VEGETATION =====

    /**
     * Returns the vegetation type of the cell.
     *
     * @return The vegetation type.
     */
    public Vegetation getVegetation() {
        return this.vegetation;
    }

    /**
     * Updates the vegetation type of the cell.
     *
     * @param vegetation The new vegetation type.
     */
    public void setVegetation(Vegetation vegetation) {
        this.vegetation = vegetation;
    }
    
    // ===== STATE =====

    /**
     * Returns the current state of the cell.
     *
     * @return The cell state.
     */
    public State getState() {
        return state;
    }

    /**
     * Updates the state of the cell.
     *
     * @param state The new state.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Checks whether the cell is currently wet.
     *
     * @return true if the cell is in the WET state, false otherwise.
     */
    public boolean isWet() {
        return state == State.WET;
    }

   
    // ===== DATA =====

    /**
     * Returns the humidity level.
     *
     * @return The humidity value.
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     * Updates the humidity level.
     *
     * @param humidity The new humidity value.
     */
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    /**
     * Returns the heat level.
     *
     * @return The heat value.
     */
    public int getHeat() {
        return heat;
    }

    /**
     * Updates the heat level.
     *
     * @param heat The new heat value.
     */
    public void setHeat(int heat) {
        this.heat = heat;
    }

    /**
     * Returns the amount of remaining fuel.
     *
     * @return The fuel quantity.
     */
    public int getFuel() {
        return fuel;
    }

    /**
     * Updates the fuel quantity.
     *
     * @param fuel The new fuel quantity.
     */
    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    /**
     * Returns the remaining wet duration.
     *
     * @return The wet time counter.
     */
    public int getWetTime() {
        return wetTime;
    }

    /**
     * Updates the wet duration counter.
     *
     * @param wetTime The new wet time value.
     */
    public void setWetTime(int wetTime) {
        this.wetTime = wetTime;
    }

    // ===== FIRE =====

    /**
     * Checks whether the cell can catch fire.
     * <p>
     * A cell can burn if it contains vegetation, still has fuel,
     * and is not fully saturated with water.
     * </p>
     *
     * @return true if the cell can burn, false otherwise.
     */
    public boolean canBurn() {
        return state == State.VEGETATION
                && fuel > 0
                && humidity < 100;
    }

    /**
     * Ignites the cell.
     * <p>
     * If the cell can burn, its state becomes {@code BURNING}
     * and its heat is set to the maximum value.
     * </p>
     */
    public void ignite() {
        if (canBurn()) {
            state = State.BURNING;
            heat = 100;
        }
    }

    /**
     * Returns a textual representation of the cell.
     *
     * @return A string containing the state, vegetation type,
     * humidity, heat, and fuel values.
     */
    @Override
    public String toString() {
        return state
                + " V:" + vegetation
                + " H:" + humidity
                + " T:" + heat
                + " F:" + fuel;
    }
}