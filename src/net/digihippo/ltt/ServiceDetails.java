package net.digihippo.ltt;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServiceDetails implements Iterable<CallingPoint>, Serializable {
    private final Set<String> splitLocations;
    private final List<List<CallingPoint>> callingPointsList;

    public ServiceDetails(Set<String> splitLocations, List<List<CallingPoint>> callingPointsList) {
        this.splitLocations = splitLocations;
        this.callingPointsList = callingPointsList;
    }

    @Override
    public Iterator<CallingPoint> iterator() {
        return callingPointsList.iterator().next().iterator();
    }

    public List<List<CallingPoint>> allParts() {
        return callingPointsList;
    }

    public Iterable<String> splitPoints() {
        return splitLocations;
    }

    public String findArrivalTimeStrAt(Station station)
    {
        for (List<CallingPoint> callingPoints : callingPointsList)
        {
            for (CallingPoint callingPoint : callingPoints)
            {
                if (station.equals(callingPoint.station))
                {
                    if (callingPoint.hasArrivalTime())
                    {
                        return callingPoint.arrivalTimeStr();
                    }
                }
            }
        }
        return null;
    }

    public Date getArrivalTime(Station station, Date requestTime)
    {
        for (List<CallingPoint> callingPoints : callingPointsList)
        {
            for (CallingPoint callingPoint : callingPoints)
            {
                if (station.equals(callingPoint.station))
                {
                    if (callingPoint.hasArrivalTime())
                    {
                        return callingPoint.arrivalTime(requestTime);
                    }
                }
            }
        }
        return null;
    }
}
