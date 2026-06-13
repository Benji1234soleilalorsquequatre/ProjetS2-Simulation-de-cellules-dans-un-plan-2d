package model;

/**
 * Représente une cellule (une case) unique dans la grille de la forêt.
 * Chaque cellule possède un état (Arbre, Feu, Cendre...), des caractéristiques
 * physiques (humidité, chaleur, combustible) et un type de végétation.
 */
public class Cell {

    private State state;
    private Vegetation vegetation;

    private int humidity;
    private int heat;
    private int fuel;
    private int wetTime = 0;

    /**
     * Crée une nouvelle cellule avec ses propriétés de base.
     *
     * @param state L'état initial de la cellule (ex: State.TREE).
     * @param humidity Le pourcentage d'humidité (freine le feu).
     * @param heat La chaleur accumulée (accélère le feu).
     * @param fuel La quantité de bois disponible pour brûler.
     * @param vegetation Le type de plante (Arbre ou Broussaille).
     */
    public Cell(State state, int humidity, int heat, int fuel, Vegetation vegetation) {
        this.state = state;
        this.humidity = humidity;
        this.heat = heat;
        this.fuel = fuel;
        this.vegetation = vegetation;
    }

    /**
     * Crée une copie exacte et indépendante de la cellule.
     * @return Une nouvelle instance de Cell avec les mêmes valeurs.
     */
    public Cell copy() {
        Cell copy = new Cell(state, humidity, heat, fuel, vegetation);
        copy.wetTime = this.wetTime;
        return copy;
    }

    // ===== VEGETATION =====
    public Vegetation getVegetation() { return this.vegetation; }
    public void setVegetation(Vegetation vegetation) { this.vegetation = vegetation; }

    // ===== STATE =====
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public boolean isWet() { return state == State.WET; }

    // ===== DATA =====
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getHeat() { return heat; }
    public void setHeat(int heat) { this.heat = heat; }

    public int getFuel() { return fuel; }
    public void setFuel(int fuel) { this.fuel = fuel; }

    public int getWetTime() { return wetTime; }
    public void setWetTime(int wetTime) { this.wetTime = wetTime; }

    // ===== FIRE =====

    /**
     * Vérifie si la cellule réunit les conditions physiques pour prendre feu.
     * @return true si c'est un arbre vivant, avec du bois et pas totalement trempé.
     */
    public boolean canBurn() {
        return state == State.VEGETATION && fuel > 0 && humidity < 100;
    }

    /**
     * Déclenche l'incendie sur cette cellule en modifiant son état et en
     * propulsant sa chaleur au maximum.
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