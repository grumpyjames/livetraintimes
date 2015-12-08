package org.grumpysoft;

import java.io.IOException;

/**
 * Provides access to arrival boards at a given station
 */
public interface ArrivalBoardService {
    /**
     * Query the service for the impending arrivals at a given station.
     * Behaviour is undefined should you pass a nonexistent station.
     *
     * @param station The full name or three letter code of a station.
     *
     * @return the current arrival board for the specified station
     * @throws IOException if network issues prevent the request from completion
     */
    ArrivalBoard arrivalsAt(String station) throws IOException;
}
