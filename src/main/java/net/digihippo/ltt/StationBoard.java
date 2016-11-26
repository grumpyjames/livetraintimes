package net.digihippo.ltt;

/**
 * Common features of both departure and arrival boards.
 */
public interface StationBoard {
    /**
     * The station this board was generated for
     * @return the station
     */
    Station station();

    /**
     * The time at which this board was generated
     * @return the generated time, in hh:mm formatted String
     */
    String generatedTime();

    /**
     * Supplementary information available about services from this station.
     * May contain useful data about service interruptions.
     * @return any update text relating to this board
     */
    String updateText();
}
