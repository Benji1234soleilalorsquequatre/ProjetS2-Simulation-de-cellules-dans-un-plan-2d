package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import model.Grid;
import model.State;

import simulation.NaiveFireAlgorithm;
import simulation.SimulationConfig;
import simulation.SimulationEngine;

public class Controller {

    @FXML
    private GridPane grille;

    // grille graphique
    private Button[][] cellules;

    // simulation
    private Grid forest;

    private SimulationEngine engine;

    @FXML
    public void initialize() {

        System.out.println("INITIALIZE");

        // création de la grille logique
        forest = new Grid(20, 20);

        // cellule en feu au centre
        forest.setCellState(10, 10, State.BURNING);

        // configuration simulation
        SimulationConfig config =
                new SimulationConfig();

        // algorithme
        NaiveFireAlgorithm algorithm =
                new NaiveFireAlgorithm();

        // moteur de simulation
        engine = new SimulationEngine(
                forest,
                algorithm,
                config
        );

        // création grille graphique
        cellules = new Button[20][20];

        for (int row = 0; row < 20; row++) {

            for (int col = 0; col < 20; col++) {

                Button cellule = new Button();

                cellule.setPrefSize(30, 30);

                cellules[row][col] = cellule;

                grille.add(cellule, col, row);
            }
        }

        updateDisplay();
    }

    /**
     * Met à jour l'affichage graphique.
     */
    private void updateDisplay() {

        Grid currentGrid =
                engine.getCurrentGrid();

        for (int row = 0; row < 20; row++) {

            for (int col = 0; col < 20; col++) {

                State state =
                        currentGrid
                                .getCell(row, col)
                                .getState();

                switch (state) {

                    case TREE:

                        cellules[row][col].setStyle(
                                "-fx-background-color: green;"
                        );

                        break;

                    case BURNING:

                        cellules[row][col].setStyle(
                                "-fx-background-color: red;"
                        );

                        break;

                    case ASH:

                        cellules[row][col].setStyle(
                                "-fx-background-color: black;"
                        );

                        break;

                    case WATER:

                        cellules[row][col].setStyle(
                                "-fx-background-color: blue;"
                        );

                        break;

                    case FIREBREAK:

                        cellules[row][col].setStyle(
                                "-fx-background-color: brown;"
                        );

                        break;

                    case EMPTY:

                        cellules[row][col].setStyle(
                                "-fx-background-color: white;"
                        );

                        break;
                }
            }
        }
    }

    /**
     * Lance une étape de simulation.
     */
    @FXML
    private void startSimulation() {

        engine.step();

        updateDisplay();
    }
}