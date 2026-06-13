package model;

/**
 * Enumeration representing the types of vegetation in the forest.
 * Different vegetation types have different physical properties:
 * <ul>
 *   <li>TREE: Large trees with higher fuel and humidity content (50-100 fuel, 10-30% humidity)</li>
 *   <li>BRUSHWOOD: Dry brushwood with lower fuel and humidity content (10-20 fuel, 0-10% humidity)</li>
 * </ul>
 */
public enum Vegetation {
    TREE, BRUSHWOOD;
}
