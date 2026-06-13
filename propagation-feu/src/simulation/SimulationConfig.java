package simulation;

import model.Wind;

/**
 * Stores all physical and mathematical parameters used by the simulation.
 * <p>
 * This class acts as a central configuration object that controls
 * the behavior of fire propagation, including the effects of humidity,
 * heat, fuel consumption, and wind.
 * </p>
 */
public class SimulationConfig {

    /** Base probability for a fire to spread to a neighboring cell. */
    private double baseSpreadProbability;

    /** Influence of humidity on fire propagation. */
    private double humidityImpact;

    /** Influence of heat on fire propagation. */
    private double heatImpact;

    /** Influence of wind on fire propagation. */
    private double windImpact;

    /** Amount of fuel consumed by burning cells at each step. */
    private double fuelConsumption;

    /** Wind configuration used by the simulation. */
    private Wind wind;

    /**
     * Creates a configuration object with default physical parameters.
     */
    public SimulationConfig() {
        this.baseSpreadProbability = 0.150;
        this.humidityImpact = 0.004;
        this.heatImpact = 0.002;
        this.windImpact = 0.1;
        this.fuelConsumption = 10.0;
        this.wind = new Wind(10, 0, 1); // Default northward wind
    }

    /**
     * Returns the base fire spread probability.
     *
     * @return The base spread probability.
     */
    public double getBaseSpreadProbability() {
        return baseSpreadProbability;
    }

    /**
     * Sets the base fire spread probability.
     *
     * @param baseSpreadProbability The new spread probability.
     */
    public void setBaseSpreadProbability(double baseSpreadProbability) {
        this.baseSpreadProbability = baseSpreadProbability;
    }

    /**
     * Returns the humidity impact coefficient.
     *
     * @return The humidity impact coefficient.
     */
    public double getHumidityImpact() {
        return humidityImpact;
    }

    /**
     * Sets the humidity impact coefficient.
     *
     * @param humidityImpact The new humidity impact coefficient.
     */
    public void setHumidityImpact(double humidityImpact) {
        this.humidityImpact = humidityImpact;
    }

    /**
     * Returns the heat impact coefficient.
     *
     * @return The heat impact coefficient.
     */
    public double getHeatImpact() {
        return heatImpact;
    }

    /**
     * Sets the heat impact coefficient.
     *
     * @param heatImpact The new heat impact coefficient.
     */
    public void setHeatImpact(double heatImpact) {
        this.heatImpact = heatImpact;
    }

    /**
     * Returns the wind impact coefficient.
     *
     * @return The wind impact coefficient.
     */
    public double getWindImpact() {
        return windImpact;
    }

    /**
     * Sets the wind impact coefficient.
     *
     * @param windImpact The new wind impact coefficient.
     */
    public void setWindImpact(double windImpact) {
        this.windImpact = windImpact;
    }

    /**
     * Returns the fuel consumption rate.
     *
     * @return The fuel consumption rate.
     */
    public double getFuelConsumption() {
        return fuelConsumption;
    }

    /**
     * Sets the fuel consumption rate.
     *
     * @param fuelConsumption The new fuel consumption rate.
     */
    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    /**
     * Returns the wind configuration.
     *
     * @return The current wind.
     */
    public Wind getWind() {
        return wind;
    }

    /**
     * Sets the wind configuration.
     *
     * @param wind The new wind.
     */
    public void setWind(Wind wind) {
        this.wind = wind;
    }
}