package model;

/**
 * Enumeration representing the possible states of a cell in the forest grid.
 * <ul>
 *   <li>EMPTY: An empty cell with no vegetation</li>
 *   <li>VEGETATION: A cell with living vegetation (tree or brushwood)</li>
 *   <li>BURNING: A cell currently on fire</li>
 *   <li>ASH: A cell that has been burned and turned to ash</li>
 *   <li>WATER: A cell filled with water (unused)</li>
 *   <li>FIREBREAK: A cell with a firebreak barrier (unused)</li>
 *   <li>PREVENTIVE: A cell in alert state (about to catch fire in PreventionAlgorithm)</li>
 *   <li>WET: A cell doused with water from the Canadair water bomber</li>
 * </ul>
 */
public enum State {
    EMPTY,
    VEGETATION,
    BURNING,
    ASH,
    WATER,
    FIREBREAK,
    PREVENTIVE,
    WET
}
