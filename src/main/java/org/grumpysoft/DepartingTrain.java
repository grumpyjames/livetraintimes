package org.grumpysoft;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.List;

public class DepartingTrain implements Serializable {
    private final boolean isCircularRoute;
    private final ImmutableList<String> destinations;
    private final ImmutableList<String> viaDestinations;
    private final String platform;
    private final String expectedAt;
    private final Iterable<CallingPoint> callingPoints;

    public DepartingTrain(
            boolean isCircularRoute,
            ImmutableList<String> destinations,
            ImmutableList<String> viaDestinations,
            String platform,
            String expectedDepartureTime,
            Iterable<CallingPoint> callingPoints) {
        this.isCircularRoute = isCircularRoute;
        this.destinations = destinations;
        this.viaDestinations = viaDestinations;
        this.platform = platform;
        this.expectedAt = expectedDepartureTime;
        this.callingPoints = callingPoints;
    }

    // FIXME: this is probably littered with 'via' already, parse it.
    public List<String> viaDestinations() {
        return viaDestinations;
    }

    public boolean isCircularRoute() {
        return isCircularRoute;
    }

    public List<String> destinationList() {
        return destinations;
    }

    public String platform() {
        return platform;
    }

    public String expectedAt() {
        return expectedAt;
    }

    public ServiceDetails serviceDetails() {
        return new ServiceDetails(callingPoints);
    }
}
