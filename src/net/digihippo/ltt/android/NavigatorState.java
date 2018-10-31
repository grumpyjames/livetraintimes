package net.digihippo.ltt.android;

import com.google.common.base.Optional;
import net.digihippo.ltt.Station;

import java.io.Serializable;

final class NavigatorState implements Serializable {
    public void switchDirection() {
        Optional<Station> tmp = stationOne;
        stationOne = stationTwo;
        stationTwo = tmp;
    }

    enum Type {
        Departing,
        FastestTrain
    }

    public Type type = Type.Departing;
    public Optional<Station> stationOne = Optional.absent();
    public Optional<Station> stationTwo = Optional.absent();

    @Override
    public String toString()
    {
        return type.toString() + " from " + stationOne + " to " + stationTwo;
    }
}
