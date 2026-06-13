package model;

/**
 * Represents the possible states of a cell during the simulation.
 * <p>
 * A cell can be empty, contain vegetation, be burning, covered with water,
 * reduced to ash, protected by a firebreak, marked as preventive,
 * or temporarily wet after a water drop.
 * </p>
 */
public enum State {

    /** Cell containing vegetation that may catch fire. */
    VEGETATION,

    /** Cell currently burning. */
    BURNING,

    /** Cell whose vegetation has been completely consumed by fire. */
    ASH,

    /** Cell identified as at risk and scheduled to ignite. */
    PREVENTIVE,

    /** Cell temporarily wet after a water drop. */
    WET
}