package org.grumpysoft;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

public final class Favourites implements Serializable {
    private static final Set<Station> favouriteStations = Sets.newHashSet();

    static {
        favouriteStations.add(Anywhere.INSTANCE);
    }

    public static void toggleFavourite(Station station, boolean state) {
        if (state) {
            favouriteStations.add(station);
        } else {
            favouriteStations.remove(station);
        }
    }

    public static Set<Station> getFavourites() {
        return favouriteStations;
    }

    public static boolean isFavourite(Station station) {
        return favouriteStations.contains(station);
    }
}
