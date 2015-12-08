package org.grumpysoft;

import java.util.List;

/**
 * A train due to depart from a given station
 */
public interface DepartingTrain extends Train {
    /**
     * Notable stations that this train will visit en route to its terminating point(s)
     *
     * @return An immutable List of the full names of the stations. May be empty
     */
    List<String> viaDestinations();

    /**
     * Whether this train is traversing a circular routes
     *
     * @return true if so, false if not
     */
    boolean isCircularRoute();

    /**
     * The full names of all the terminating points of this service.
     * There may be more than one (services that split will have this feature)
     *
     * @return An immutable java.util.List of the full names of the stations this services terminates at
     *
     */
    List<String> destinationList();
}
