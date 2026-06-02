package simulation;

public class SimulationConfig {

    private double baseSpreadProbability;
    private double humidityImpact;
    private double heatImpact;
    private double windImpact;
    private double fuelConsumption;

    public SimulationConfig() {
        this.baseSpreadProbability = 0.30;
        this.humidityImpact = 0.004;
        this.heatImpact = 0.002;
        this.windImpact = 0.10;
        this.fuelConsumption = 10.0;
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
}