package org.grumpysoft;

/**
 * A train listed within an ArrivalBoard
 */
public interface ArrivingTrain extends Train {
    /**
     * The originating station of this train, i.e where it entered service
     *
     * @return The full name of the station this service originated from
     */
    String origin();
}
