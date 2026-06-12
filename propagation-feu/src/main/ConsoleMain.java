package main;

import model.Grid;
import model.State;
import simulation.NaiveFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;

public class ConsoleMain {

    /** 
     * @param args
     */
    public static void main(String[] args) {
        Grid forest = new Grid(10, 10);
        forest.setCellState(5, 5, State.BURNING);

        SimulationConfig config = new SimulationConfig();
        config.setBaseSpreadProbability(0.50);

        SimulationEngine engine = new SimulationEngine(
            forest,
            new NaiveFireAlgorithm(),
            config
        );

        System.out.println("Initial grid:");
        engine.getCurrentGrid().displayGrid();

        for (int step = 0; step < 5; step++) {
            engine.step();

            System.out.println();
            System.out.println("Step " + engine.getStepCounter() + ":");
            engine.getCurrentGrid().displayGrid();
        }
    }
}