package net.digihippo;

public class DepartureBoard {
    private final Iterable<DepartingTrain> departingTrains;

    public DepartureBoard(Iterable<DepartingTrain> departingTrains) {
        this.departingTrains = departingTrains;
    }

    public Iterable<? extends DepartingTrain> departingTrains() {
        return departingTrains;
    }
}
