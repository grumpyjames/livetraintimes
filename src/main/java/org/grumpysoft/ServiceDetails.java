package org.grumpysoft;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public class ServiceDetails implements Iterable<CallingPoint>, Serializable {
    private final ImmutableSet<String> splitLocations;
    private final Collection<Collection<CallingPoint>> callingPoints;

    public ServiceDetails(ImmutableSet<String> splitLocations, Collection<Collection<CallingPoint>> callingPoints) {
        this.splitLocations = splitLocations;
        this.callingPoints = callingPoints;
    }

    @Override
    public Iterator<CallingPoint> iterator() {
        return callingPoints.iterator().next().iterator();
    }

    public Collection<Collection<CallingPoint>> allParts() {
        return callingPoints;
    }
}
