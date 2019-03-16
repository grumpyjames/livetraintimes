package net.digihippo.ltt.android;

import net.digihippo.ltt.Station;

import java.io.Serializable;

final class NavigatorState implements Serializable {
    public void switchDirection() {
        Station tmp = stationOne;
        stationOne = stationTwo;
        stationTwo = tmp;
    }

    enum Type {
        Departing,
        FastestTrain
    }

    public Type type = Type.Departing;
    public Station stationOne = null;
    public Station stationTwo = null;

    @Override
    public String toString()
    {
        return type.toString() + " from " + stationOne + " to " + stationTwo;
    }
}
