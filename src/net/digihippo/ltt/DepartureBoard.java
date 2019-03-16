package net.digihippo.ltt;

import java.util.List;

public class DepartureBoard {
    private final List<DepartingTrain> departingTrains;

    DepartureBoard(List<DepartingTrain> departingTrains) {
        this.departingTrains = departingTrains;
    }

    public List<DepartingTrain> departingTrains() {
        return departingTrains;
    }
}
