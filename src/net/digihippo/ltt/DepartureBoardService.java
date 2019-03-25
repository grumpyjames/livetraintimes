package net.digihippo.ltt;

import java.io.IOException;

public interface DepartureBoardService {
    DepartureBoard boardFor(Station fromStation) throws IOException;

    DepartureBoard boardFor(Station fromStation, Station toStation) throws IOException;
}
