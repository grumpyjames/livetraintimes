package net.digihippo.ltt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DepartingTrain implements Serializable {
    public static final String ON_TIME = "On time";
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

    private List<String> viaDestinations() {
        return viaDestinations;
    }

    private boolean isCircularRoute() {
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

    public Date findArrivalTimeAt(Station atStation)
    {
        return serviceDetails.getArrivalTime(atStation, requestedAt);
    }

    public String findArrivalTimeStrAt(Station station)
    {
        return serviceDetails.findArrivalTimeStrAt(station);
    }

    public String getActualDepartureTime()
    {
        String et = getDepartureTime().unwrap();
        if (et.equals(ON_TIME))
        {
            return scheduledTime;
        }
        return et;
    }

    public String destinationText() {
        final List<String> endPoints = new ArrayList<>();
        for (Station station : destinationList())
        {
            endPoints.add(station.fullName());
        }
        List<String> via = viaDestinations();
        StringBuilder destinationText = new StringBuilder();
        boolean addComma = false;
        for (int i = 0; i < endPoints.size(); i++) {
            if (addComma) {
                destinationText.append(", ");
            } else {
                addComma = true;
            }

            if (i < via.size()) {
                destinationText.append(endPoints.get(i)).append(" ").append(via.get(i).trim());
            } else {
                destinationText.append(endPoints.get(i));
            }
        }

        if (isCircularRoute()) {
            destinationText.append(" (circular route)");
        }

        return destinationText.toString();
    }
}
