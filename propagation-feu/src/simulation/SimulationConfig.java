package simulation;

import model.Wind;

/**
 * Contains all physical and mathematical variables for the simulation.
 * Acts as a mixer board to adjust fire behavior parameters.
 */
public class SimulationConfig {

    private double baseSpreadProbability;
    private double humidityImpact;
    private double heatImpact;
    private double windImpact;
    private double fuelConsumption;

    private Wind wind;

    /**
     * Initializes the configuration with default physical parameters.
     */
    public SimulationConfig() {
        this.baseSpreadProbability = 0.150;
        this.humidityImpact = 0.004;
        this.heatImpact = 0.002;
        this.windImpact = 0.1;
        this.fuelConsumption = 10.0;
        this.wind = new Wind(10, -1, -1); 
    }

    /** @return Base fire spread probability (0.0 to 1.0) */
    public double getBaseSpreadProbability() { return baseSpreadProbability; }
    /** Sets the base fire spread probability */
    public void setBaseSpreadProbability(double baseSpreadProbability) { this.baseSpreadProbability = baseSpreadProbability; }

    /** @return Humidity impact factor on fire spread */
    public double getHumidityImpact() { return humidityImpact; }
    /** Sets the humidity impact factor */
    public void setHumidityImpact(double humidityImpact) { this.humidityImpact = humidityImpact; }

    /** @return Heat impact factor on fire spread */
    public double getHeatImpact() { return heatImpact; }
    /** Sets the heat impact factor */
    public void setHeatImpact(double heatImpact) { this.heatImpact = heatImpact; }

    /** @return Wind impact factor on fire spread */
    public double getWindImpact() { return windImpact; }
    /** Sets the wind impact factor */
    public void setWindImpact(double windImpact) { this.windImpact = windImpact; }

    /** @return Fuel consumption rate per simulation step */
    public double getFuelConsumption() { return fuelConsumption; }
    /** Sets the fuel consumption rate */
    public void setFuelConsumption(double fuelConsumption) { this.fuelConsumption = fuelConsumption; }

    /** @return The current wind conditions */
    public Wind getWind() { return wind; }
    /** Sets the wind conditions */
    public void setWind(Wind wind) { this.wind = wind; }
}