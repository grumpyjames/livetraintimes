package org.grumpysoft;

/**
 * An arrival board at a given station
 */
public interface ArrivalBoard extends StationBoard {
    /**
     * The trains due to arrive at this station in the near future
     * @return an immutable Iterable of the trains
     */
    public Iterable<? extends ArrivingTrain> arrivingTrains();
}
