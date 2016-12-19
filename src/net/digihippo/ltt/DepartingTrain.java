package net.digihippo.ltt;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

public class DepartingTrain implements Serializable {
    private final boolean isCircularRoute;
    private final ImmutableList<Station> destinations;
    private final ImmutableList<String> viaDestinations;
    private final String platform;
    private final ServiceDetails serviceDetails;
    private final Either<BadTrainState, String> departureTime;

    public DepartingTrain(
            boolean isCircularRoute,
            ImmutableList<Station> destinations,
            ImmutableList<String> viaDestinations,
            String platform,
            ServiceDetails serviceDetails,
            Either<BadTrainState, String> departureTime)
    {
        this.isCircularRoute = isCircularRoute;
        this.destinations = destinations;
        this.viaDestinations = viaDestinations;
        this.platform = platform;
        this.serviceDetails = serviceDetails;
        this.departureTime = departureTime;
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
}
