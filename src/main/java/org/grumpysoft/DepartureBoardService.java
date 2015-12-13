package org.grumpysoft;

import com.google.common.base.Optional;

public interface DepartureBoardService {
    DepartureBoard boardFor(Station fromStation, Optional<Station> toStation) throws Exception;
}
