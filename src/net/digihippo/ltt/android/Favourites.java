package net.digihippo.ltt.android;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import net.digihippo.ltt.Anywhere;
import net.digihippo.ltt.Station;
import net.digihippo.ltt.Stations;

import java.io.Serializable;
import java.util.*;

public final class Favourites implements Serializable {
    private static final String FAVOURITES_KEY = "favourites";
    private static final String SEPARATOR = ",";
    private static final Set<Station> favouriteStations = new TreeSet<>(new Comparator<Station>() {
        @Override
        public int compare(Station lhs, Station rhs) {
            return lhs.fullName().compareTo(rhs.fullName());
        }
    });

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

    private static Set<String> currentFavouritesAsStrings() {
        final Set<Station> favourites = Favourites.getFavourites();

        final Set<String> crsFavourites = new HashSet<>();
        for (Station favourite : favourites)
        {
            if (favourite != Anywhere.INSTANCE && favourite.fullName() != null)
            {
                crsFavourites.add(favourite.threeLetterCode());
            }
        }

        return crsFavourites;
    }

    public static boolean isFavourite(Station station) {
        return favouriteStations.contains(station);
    }

    public static void deserializeFrom(SharedPreferences preferences) {
        String favouritesString = preferences.getString(FAVOURITES_KEY, "");
        Log.w("Favourites", favouritesString);
        addFavourites(
            deserializeFavouritesFrom(
                TextUtils.split(SEPARATOR, favouritesString)));
    }

    private static void addFavourites(List<Station> stations) {
        for (Station station : stations)
            Favourites.toggleFavourite(station, true);
    }

    private static List<Station> deserializeFavouritesFrom(String[] favourites) {
        final List<Station> result = new ArrayList<>();
        for (String favourite : favourites)
        {
            Station station = Stations.lookup(favourite);
            if (station.fullName() != null)
            {
                result.add(station);
            }
        }

        return result;
    }

    public static void save(SharedPreferences preferences) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FAVOURITES_KEY, TextUtils.join(SEPARATOR, currentFavouritesAsStrings()));

        editor.commit();
    }
}
