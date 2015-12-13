package org.grumpysoft;

import com.google.common.collect.ImmutableList;
import org.grumpysoft.CallingPoint;
import org.grumpysoft.Location;

import java.util.Iterator;
import java.util.List;

public class ServiceDetails implements Iterable<CallingPoint> {
    private final String serviceId;
    private Iterable<CallingPoint> callingPoints;

    public ServiceDetails(String serviceId, Iterable<CallingPoint> callingPoints) {
        this.serviceId = serviceId;
        this.callingPoints = callingPoints;
    }

    public Location currentLocation() {
        return new Location() {
            @Override
            public LocationStatus status() {
                return LocationStatus.UNKNOWN;
            }

            @Override
            public List<String> stations() {
                return ImmutableList.of();
            }
        };
    }

    public String serviceId() {
        return serviceId;
    }

    @Override
    public Iterator<CallingPoint> iterator() {
        return callingPoints.iterator();
    }
}
