package net.digihippo.ltt;

public interface DepartureBoardService {
    DepartureBoard boardFor(Station fromStation) throws Exception;

    DepartureBoard boardFor(Station fromStation, Station toStation) throws Exception;

    void httpsIsBroken();
}
