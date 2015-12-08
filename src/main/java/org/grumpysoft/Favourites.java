package org.grumpysoft;

import android.content.SharedPreferences;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

public final class Favourites implements Serializable {
    private static final String FAVOURITES_KEY = "favourites";
    private static final String SEPARATOR = ",";
    private static final Set<Station> favouriteStations = Sets.newTreeSet(new Comparator<Station>() {
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
        return copyOf(transform(filter(favourites, new Predicate<Station>() {
            @Override
            public boolean apply(@Nullable Station station) {
                return station != Anywhere.INSTANCE;
            }
        }), new Function<Station, String>() {
            @Override
            public String apply(Station station) {
                return station.threeLetterCode();
            }
        }));
    }

    public static boolean isFavourite(Station station) {
        return favouriteStations.contains(station);
    }

    public static void deserializeFrom(SharedPreferences preferences) {
        addFavourites(deserializeFavouritesFrom(Splitter.on(",").omitEmptyStrings().split(preferences.getString(FAVOURITES_KEY, ""))));
    }

    private static void addFavourites(List<Station> stations) {
        for (Station station : stations)
            Favourites.toggleFavourite(station, true);
    }

    private static List<Station> deserializeFavouritesFrom(Iterable<String> favourites) {
        return Lists.transform(ImmutableList.copyOf(favourites), new Function<CharSequence, Station>() {
            @Override
            public Station apply(CharSequence charSequence) {
                return Stations.lookup(charSequence.toString());
            }
        });
    }

    public static void save(SharedPreferences preferences) {

        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FAVOURITES_KEY, Joiner.on(SEPARATOR).join(currentFavouritesAsStrings()));

        editor.commit();
    }
}
