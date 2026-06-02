package model;

/**
 * The Cell class represents one cell of the forest grid.
 */
public class Cell {

    private State state;
    private int humidity;
    private int heat;
    private int fuel;

    public Cell(State state, int humidity, int heat, int fuel) {
        this.state = state;
        this.humidity = humidity;
        this.heat = heat;
        this.fuel = fuel;
    }

    public Cell copy() {
        return new Cell(this.state, this.humidity, this.heat, this.fuel);
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getHumidity() {
        return this.humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getHeat() {
        return this.heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getFuel() {
        return this.fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public boolean canBurn() {
        return this.state == State.TREE && this.fuel > 0 && this.humidity < 100;
    }

    public void ignite() {
        if (canBurn()) {
            this.state = State.BURNING;
            this.heat = 100;
        }
    }
}