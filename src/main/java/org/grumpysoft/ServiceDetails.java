package org.grumpysoft;

import java.util.Collection;
import java.util.Iterator;

public class ServiceDetails implements Iterable<CallingPoint> {
    private Collection<Collection<CallingPoint>> callingPoints;

    public ServiceDetails(Collection<Collection<CallingPoint>> callingPoints) {
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
