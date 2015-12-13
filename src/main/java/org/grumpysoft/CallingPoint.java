package org.grumpysoft;

public interface CallingPoint {
    String stationName();

    String scheduledTime();

    /** The possible statuses of a given calling point **/
    enum PointStatus {
        /** The service has departed from this point **/
        DEPARTED,
        /** The service is on its way to this point **/
        IN_MOTION,
        /** The service is at this station **/
        AT_STATION,
        /** The service has been cancelled and will no longer call at this point **/
        CANCELLED,
        /** The status is yet to have been reported **/
        NO_REPORT
    }
}
