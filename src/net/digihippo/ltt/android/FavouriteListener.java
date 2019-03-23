package net.digihippo.ltt.android;


import net.digihippo.ltt.Station;

public interface FavouriteListener {
    void favouriteAdded(final Station station);
    void favouriteRemoved(final Station station);
}
