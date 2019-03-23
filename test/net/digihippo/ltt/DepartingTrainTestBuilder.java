package net.digihippo.ltt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class DepartingTrainTestBuilder
{
    private boolean isCircularRoute = false;
    private final List<Station> destinations = new ArrayList<>();
    private final List<String> viaDestinations = new ArrayList<>();
    private final List<List<CallingPoint>> callingPointList = new ArrayList<>();
    private Either<BadTrainState, String> status = Either.left(BadTrainState.Delayed);
    private boolean onTime = false;
    private String platform;
    private String scheduledTime;
    private Date requestedAt;

    public CallingPointsBuilder withDestination(final String stationFullName)
    {
        return new CallingPointsBuilder(
            Stations.reverseLookup(stationFullName),
            this);
    }

    public DepartingTrainTestBuilder withPlatform(String platform)
    {
        this.platform = platform;
        return this;
    }

    public DepartingTrainTestBuilder onSchedule(String scheduledTime)
    {
        this.onTime = true;
        this.scheduledTime = scheduledTime;
        this.status = Either.right("On time");
        return this;
    }

    public DepartingTrain build()
    {
        return new DepartingTrain(
            requestedAt,
            isCircularRoute,
            destinations,
            viaDestinations,
            platform,
            new ServiceDetails(
                Collections.<String>emptySet(),
                callingPointList
            ),
            status,
            scheduledTime,
            onTime
        );
    }

    DepartingTrainTestBuilder requestedAt(String requestedAt)
    {
        SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try
        {
            this.requestedAt = format.parse(requestedAt);
        } catch (ParseException e)
        {
            throw new RuntimeException(e);
        }

        return this;
    }

    public void addDestination(Station station, List<CallingPoint> callingPoints)
    {
        destinations.add(station);
        callingPointList.add(callingPoints);
    }
}
