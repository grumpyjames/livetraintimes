package net.digihippo.ltt;

import java.util.ArrayList;
import java.util.List;

public final class CallingPointsBuilder
{
    private final Station station;
    private final DepartingTrainTestBuilder trainTestBuilder;
    private final List<CallingPoint> callingPoints = new ArrayList<>();

    CallingPointsBuilder(
        Station station,
        DepartingTrainTestBuilder trainTestBuilder)
    {
        this.station = station;
        this.trainTestBuilder = trainTestBuilder;
    }

    public CallingPointsBuilder onTime(final String stationFullName, final String scheduledAt)
    {
        callingPoints.add(new CallingPoint(
            Stations.reverseLookup(stationFullName),
            scheduledAt,
            Either.<BadTrainState, String>right("On time")
        ));

        return this;
    }

    public DepartingTrainTestBuilder build()
    {
        trainTestBuilder.addDestination(station, callingPoints);
        return trainTestBuilder;
    }
}
