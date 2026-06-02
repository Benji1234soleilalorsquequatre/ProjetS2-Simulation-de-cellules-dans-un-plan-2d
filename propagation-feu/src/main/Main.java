package main;

import model.Grid;
import model.State;
import simulation.FirePropagationAlgorithm;
import simulation.NaiveFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;

public class Main {

    public static void main(String[] args) {
        Grid grid = new Grid(10, 10);
        grid.setCellState(5, 5, State.BURNING);

        SimulationConfig config = new SimulationConfig();
        /**On peut modifier les paramètres directement dans le main ici si besoin :
         * Exemple :
         * config.setBaseSpreadProbability(0.60);
         */
        FirePropagationAlgorithm algorithm = new NaiveFireAlgorithm();
        /**On peut changer en cours l'algorithme ici avec :
         * engine.setAlgorithm(new NouvelAlgorithme());
        */

        SimulationEngine engine = new SimulationEngine(grid, algorithm, config);

        System.out.println("Initial grid:");
        engine.getCurrentGrid().displayGrid();

        for (int i = 0; i < 7; i++) {
            engine.step();

            System.out.println();
            System.out.println("Step " + engine.getStepCounter() + ":");
            engine.getCurrentGrid().displayGrid();
        }
    }
} 