package org.grumpysoft;

/**
 * A departure board at a given station
 */
public interface DepartureBoard extends StationBoard {
    /**
     * The services due to depart from this station in the near future.
     * May be filtered by calling point depending on the initial request
     *
     * @return the current departing services from this station
     */
    Iterable<? extends DepartingTrain> departingTrains();

    /**
     * Whether this board filtered services by calling point at generation time
     * @return true if so, false if not
     */
    boolean hasToStation();

    /**
     * If a 'to' station was specified at query time, this method will allow
     * you to retrieve that station.
     *
     * @return The station that was filtered for, should one have been provided
     * @throws IllegalStateException if no to station was specified - check hasToStation()
     * is true before calling this function
     */
    Station toStation();
}
