package org.grumpysoft;

/**
 * The full details of a given service, including a list of all the calling points,
 * the current location of the train, and the service id.
 */
public interface ServiceDetails extends Iterable<CallingPoint> {
    /**
     * The current location of this service (may be unknown)
     * @return the current location
     */
    Location currentLocation();

    /**
     * The unique id of this service
     * @return the service's id
     */
    String serviceId();
}
