package net.digihippo;

import java.util.List;

/** The current location of a given service **/
public interface Location {
    /**
     * A train can either be 'AT' a given station,
     * 'BETWEEN' two stations, or 'UNKNOWN'
     */
    public enum LocationStatus {
        /** At a particular station **/
        AT,
        /** Between two stations **/
        BETWEEN,
        /** Not known **/
        UNKNOWN
    }

    /**
     * The current location status
     * @return the current status
     */
    LocationStatus status();

    /**
     * The names of that stations this train is either AT or BETWEEN.
     *
     * @return An immutable list containing the requisite stations, i.e:
     *
     * The full name of station the train is 'AT', if status() == AT.
     * The full names of the stations the train is 'BETWEEN', if status() == BETWEEN.
     * An empty list if status() == UNKNOWN.
     */
    List<String> stations();

}
