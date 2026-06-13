package model;

/**
 * Represents the different types of vegetation that can occupy a cell.
 * <p>
 * Different vegetation types may influence the behavior of the simulation,
 * such as the probability of catching fire or the speed of fire propagation.
 * </p>
 */
public enum Vegetation {

    /** A tree with standard fire propagation properties. */
    TREE,

    /** Dense brushwood that is more likely to catch fire. */
    BRUSHWOOD

}