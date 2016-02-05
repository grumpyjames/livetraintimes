package net.digihippo;

import java.io.Serializable;

public class CallingPoint implements Serializable {
    public static CallingPoint singlePoint(
            final String stationName,
            final String scheduledAtTime,
            final Either<BadTrainState, String> et) {
        return new CallingPoint(stationName, scheduledAtTime, et);
    }

    public final String locationName;
    public final String scheduledAtTime;
    public final Either<BadTrainState, String> et;

    private CallingPoint(String locationName, String scheduledAtTime, Either<BadTrainState, String> et) {
        this.locationName = locationName;
        this.scheduledAtTime = scheduledAtTime;
        this.et = et;
    }

    @Override
    public String toString() {
        return locationName + "@" + scheduledAtTime;
    }
}
