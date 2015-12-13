package org.grumpysoft;

public class CallingPoint {
    private final String locationName;
    private final String scheduledAtTime;

    public CallingPoint(String locationName, String scheduledAtTime) {
        this.locationName = locationName;
        this.scheduledAtTime = scheduledAtTime;
    }

    public String stationName() {
        return locationName;
    }

    public String scheduledTime() {
        return scheduledAtTime;
    }
}
