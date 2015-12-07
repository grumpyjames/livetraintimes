package org.grumpysoft;

import com.google.common.base.Optional;

import java.io.Serializable;

final class NavigatorState implements Serializable {
    enum Type {
        Arriving,
        Departing,
        FastestTrain
    }

    public Type type = Type.Departing;
    public Optional<Station> stationOne = Optional.absent();
    public Optional<Station> stationTwo = Optional.absent();
}
