package net.digihippo.ltt;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ServiceDetails implements Iterable<CallingPoint>, Serializable {
    private final Set<String> splitLocations;
    private final List<List<CallingPoint>> callingPoints;

    public ServiceDetails(Set<String> splitLocations, List<List<CallingPoint>> callingPoints) {
        this.splitLocations = splitLocations;
        this.callingPoints = callingPoints;
    }

    @Override
    public Iterator<CallingPoint> iterator() {
        return callingPoints.iterator().next().iterator();
    }

    public List<List<CallingPoint>> allParts() {
        return callingPoints;
    }

    public Iterable<String> splitPoints() {
        return splitLocations;
    }
}
