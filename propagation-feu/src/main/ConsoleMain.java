package main;

import model.Grid;
import model.State;
import simulation.NaiveFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;

/**
 * Console-based entry point for testing the fire simulation.
 * Runs a small simulation and prints the grid state to the console at each step.
 * Useful for debugging and testing without the GUI.
 */
public class ConsoleMain {

    /**
     * Main method to run console simulation.
     * Creates a 10x10 grid with fire at center and runs 5 simulation steps.
     *
     * @param args Command line arguments (unused)
     */
    public static void main(String[] args) {
        // Create a 10x10 forest grid
        Grid forest = new Grid(10, 10);

        // Set initial fire source at center
        forest.setCellState(5, 5, State.BURNING);

        // Configure simulation with increased spread probability
        SimulationConfig config = new SimulationConfig();
        config.setBaseSpreadProbability(0.50);

        // Initialize simulation engine with naive algorithm
        SimulationEngine engine = new SimulationEngine(
            forest,
            new NaiveFireAlgorithm(),
            config
        );

        // Display initial state
        System.out.println("Initial grid:");
        engine.getCurrentGrid().displayGrid();

        // Run 5 simulation steps and display results
        for (int step = 0; step < 5; step++) {
            engine.step();

            System.out.println();
            System.out.println("Step " + engine.getStepCounter() + ":");
            engine.getCurrentGrid().displayGrid();
        }
    }
}