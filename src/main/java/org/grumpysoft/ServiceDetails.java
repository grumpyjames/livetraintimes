package org.grumpysoft;

import java.util.Iterator;

public class ServiceDetails implements Iterable<CallingPoint> {
    private Iterable<CallingPoint> callingPoints;

    public ServiceDetails(Iterable<CallingPoint> callingPoints) {
        this.callingPoints = callingPoints;
    }

    @Override
    public Iterator<CallingPoint> iterator() {
        return callingPoints.iterator();
    }
}
