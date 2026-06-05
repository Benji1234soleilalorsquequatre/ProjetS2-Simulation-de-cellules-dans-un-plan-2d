package simulation;

import model.Wind;

public class SimulationConfig {

    private double baseSpreadProbability;
    private double humidityImpact;
    private double heatImpact;
    private double windImpact;
    private double fuelConsumption;

    private Wind wind;

    public SimulationConfig() {

        this.baseSpreadProbability = 0.150;
        this.humidityImpact = 0.004;
        this.heatImpact = 0.002;
        this.windImpact = 0.1;
        this.fuelConsumption = 10.0;

        this.wind = new Wind(10, 1, 1);
    }

    public double getBaseSpreadProbability() {
        return baseSpreadProbability;
    }

    public void setBaseSpreadProbability(double baseSpreadProbability) {
        this.baseSpreadProbability = baseSpreadProbability;
    }

    public double getHumidityImpact() {
        return humidityImpact;
    }

    public void setHumidityImpact(double humidityImpact) {
        this.humidityImpact = humidityImpact;
    }

    public double getHeatImpact() {
        return heatImpact;
    }

    public void setHeatImpact(double heatImpact) {
        this.heatImpact = heatImpact;
    }

    public double getWindImpact() {
        return windImpact;
    }

    public void setWindImpact(double windImpact) {
        this.windImpact = windImpact;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }
}