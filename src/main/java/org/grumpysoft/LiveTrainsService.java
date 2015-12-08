package org.grumpysoft;

/**
 * A complete service, providing the ability to query for both arrival and departure boards
 */
public interface LiveTrainsService extends ArrivalBoardService, DepartureBoardService {
}
