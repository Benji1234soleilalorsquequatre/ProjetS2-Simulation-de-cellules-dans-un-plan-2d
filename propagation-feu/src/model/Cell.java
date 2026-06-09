package model;

public class Cell {

    private State state;

    private int humidity;
    private int heat;
    private int fuel;
    private int wetTime = 0;

    public Cell(State state, int humidity, int heat, int fuel) {

        this.state = state;
        this.humidity = humidity;
        this.heat = heat;
        this.fuel = fuel;
    }

    public Cell copy() {
        Cell copy = new Cell(state, humidity, heat, fuel);
        copy.wetTime = this.wetTime;
        return copy;
    }

    // ===== STATE =====

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    public boolean isWet(){
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

    public int getWetTime(){
        return wetTime;
    }

    public void setWetTime(int wetTime){
        this.wetTime = wetTime;
    }

    // ===== FIRE =====

    public boolean canBurn() {

        return state == State.TREE
                && fuel > 0
                && humidity < 100;
    }

    public void ignite() {

        if (canBurn()) {

            state = State.BURNING;

            heat = 100;
        }
    }

    @Override
    public String toString() {

        return state
                + " H:" + humidity
                + " T:" + heat
                + " F:" + fuel;
    }
}