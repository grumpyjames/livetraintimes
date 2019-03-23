package net.digihippo.ltt;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class DepartingTrain implements Serializable {
    private final Date requestedAt;
    private final boolean isCircularRoute;
    private final List<Station> destinations;
    private final List<String> viaDestinations;
    private final String platform;
    private final ServiceDetails serviceDetails;
    private final Either<BadTrainState, String> departureTime;
    private final String scheduledTime;
    private final boolean onTime;

    public DepartingTrain(
        Date requestedAt,
        boolean isCircularRoute,
        List<Station> destinations,
        List<String> viaDestinations,
        String platform,
        ServiceDetails serviceDetails,
        Either<BadTrainState, String> departureTime,
        String scheduledTime,
        boolean onTime)
    {
        this.requestedAt = requestedAt;
        this.isCircularRoute = isCircularRoute;
        this.destinations = destinations;
        this.viaDestinations = viaDestinations;
        this.platform = platform;
        this.serviceDetails = serviceDetails;
        this.departureTime = departureTime;
        this.scheduledTime = scheduledTime;
        this.onTime = onTime;
    }

    public List<String> viaDestinations() {
        return viaDestinations;
    }

    public boolean isCircularRoute() {
        return isCircularRoute;
    }

    public List<Station> destinationList() {
        return destinations;
    }

    public String platform() {
        return platform;
    }

    public ServiceDetails serviceDetails() {
        return serviceDetails;
    }

    public Either<BadTrainState, String> getDepartureTime() {
        return departureTime;
    }

    public String getScheduledTime()
    {
        return scheduledTime;
    }

    public boolean onTime()
    {
        return onTime;
    }

    public Date getArrivalTime(Station atStation)
    {
        return serviceDetails.getArrivalTime(atStation, requestedAt);
    }
}
