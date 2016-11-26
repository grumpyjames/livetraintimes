package net.digihippo.ltt;

import java.io.IOException;

/**
 * Common features of arriving and departing train services
 */
public interface Train {
    /**
     * The platform this train will visit
     *
     * Platforms aren't necessarily numeric (some stations have A,B,...)
     * or single character (some stations have 1a, etc).
     *
     * @return The platform of the train
     */
    String platform();

    /**
     * The time this train was scheduled at
     * @return hh:mm formatted String representation of the scheduled at time
     */
    String scheduledAt();

    /**
     * The current status of this train
     * @return the current status
     */
    TrainStatus status();

    /**
     * The time this train is expected at. Differs from scheduled at time only when the train is late.
     * @return hh:mm formatted String representation of the expected at time
     */
    String expectedAt();

    /**
     * The service id of this train
     * @return the service id
     */
    String serviceId();

    /**
     * The full details for this service (@see ServiceDetails)
     * @return the service details
     * @throws IOException if network issues prevent the details from being retrieved
     */
    ServiceDetails serviceDetails();

    /** The possible statuses of a given train **/
    enum TrainStatus {
        /** Running on time **/
        ON_TIME,
        /** Yet to depart the originating station **/
        STARTS_HERE,
        /** Cancelled **/
        CANCELLED,
        /** Behind schedule **/
        LATE
    }
}
