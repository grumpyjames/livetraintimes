package net.digihippo;

import java.io.Serializable;

public class CallingPoint implements Serializable {
    public static CallingPoint singlePoint(
            final String stationName,
            final String scheduledAtTime) {
        return new CallingPoint(stationName, scheduledAtTime);
    }

    public final String locationName;
    public final String scheduledAtTime;

    private CallingPoint(String locationName, String scheduledAtTime) {
        this.locationName = locationName;
        this.scheduledAtTime = scheduledAtTime;
    }

    @Override
    public String toString() {
        return locationName + "@" + scheduledAtTime;
    }
}
