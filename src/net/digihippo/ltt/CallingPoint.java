package net.digihippo.ltt;

import java.io.Serializable;

public class CallingPoint implements Serializable {
    public static CallingPoint singlePoint(
        final Station station,
        final String scheduledAtTime,
        final Either<BadTrainState, String> et) {
        return new CallingPoint(station, scheduledAtTime, et);
    }

    public final Station station;
    public final String scheduledAtTime;
    public final Either<BadTrainState, String> et;

    private CallingPoint(Station station, String scheduledAtTime, Either<BadTrainState, String> et) {
        this.station = station;
        this.scheduledAtTime = scheduledAtTime;
        this.et = et;
    }

    @Override
    public String toString() {
        return station.fullName() + "@" + scheduledAtTime;
    }
}
