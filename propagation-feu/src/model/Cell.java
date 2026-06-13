package model;

/**
 * Represents a single cell (grid square) in the forest simulation.
 * Each cell has a state (Tree, Fire, Ash, etc.), physical characteristics
 * (humidity, heat, fuel), and a vegetation type.
 */
public class Cell {

    private State state;
    private Vegetation vegetation;

    private int humidity;
    private int heat;
    private int fuel;
    private int wetTime = 0;

    /**
     * Creates a new cell with its basic properties.
     *
     * @param state      The initial cell state (e.g., State.VEGETATION)
     * @param humidity   The humidity percentage (0-100, inhibits fire spread)
     * @param heat       The accumulated heat (0-100, accelerates fire)
     * @param fuel       The amount of wood available to burn (0-100)
     * @param vegetation The vegetation type (TREE or BRUSHWOOD)
     */
    public Cell(State state, int humidity, int heat, int fuel, Vegetation vegetation) {
        this.state = state;
        this.humidity = humidity;
        this.heat = heat;
        this.fuel = fuel;
        this.vegetation = vegetation;
    }

    /**
     * Creates an exact independent copy of the cell.
     *
     * @return A new Cell instance with the same values
     */
    public Cell copy() {
        Cell copy = new Cell(state, humidity, heat, fuel, vegetation);
        copy.wetTime = this.wetTime;
        return copy;
    }

   
    // ===== VEGETATION =====
    public Vegetation getVegetation() {
        return this.vegetation;
    }

    public void setVegetation(Vegetation vegetation) {
        this.vegetation = vegetation;
    }

    
    // ===== STATE =====
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isWet() {
        return state == State.WET;
    }

   
    // ===== DATA =====
    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getWetTime() {
        return wetTime;
    }

    public void setWetTime(int wetTime) {
        this.wetTime = wetTime;
    }

    // ===== FIRE =====

    /**
     * Checks if the cell meets physical conditions to catch fire.
     *
     * @return true if it has vegetation with fuel and is not completely saturated
     */
    public boolean canBurn() {
        return state == State.VEGETATION && fuel > 0 && humidity < 100;
    }

    /**
     * Ignites the cell if conditions allow, setting it to BURNING state
     * and maximizing its heat.
     */
    public void ignite() {
        if (canBurn()) {
            state = State.BURNING;
            heat = 100;
        }
    }

    @Override
    public String toString() {
        return state + " V:" + vegetation + " H:" + humidity + " T:" + heat + " F:" + fuel;
    }
}