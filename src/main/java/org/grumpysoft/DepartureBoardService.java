package org.grumpysoft;

import java.io.IOException;

/**
 * Provides departure boards from and between given station(s)
 */
public interface DepartureBoardService {
    /**
     * Retrieve the departure board from the given station
     * Behaviour is *undefined* should you pass a nonexistent station!
     *
     * @param station The full name or three letter code of a station.
     *
     * @return The live departure board for this station
     * @throws IOException if network issues prevent the request from completing
     */
    DepartureBoard boardFor(String station) throws IOException;

    /**
     * Retrieve the departure board for all services travelling to 'toStation' from 'fromStation'.
     * Behaviour is *undefined* should you pass a nonexistent station!
     *
     * @param fromStation The full name or three letter code of the station you wish to see departures from
     * @param toStation The full name or three letter code of the station you with to see departures to
     *
     * @return the live departures from fromStation that call at toStation.
     * @throws IOException if network issues prevent the request from completing
     */
    DepartureBoard boardForJourney(String fromStation, String toStation) throws IOException;
}
