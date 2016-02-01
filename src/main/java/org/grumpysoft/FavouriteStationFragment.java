package org.grumpysoft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Set;

import static com.google.common.collect.ImmutableList.copyOf;

public class FavouriteStationFragment extends Fragment implements FavouriteListener {
    private ListView stationView;
    private Bundle state;
    private Set<Station> favourites;

    @Override
    public void setArguments(Bundle args) {
        this.state = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_station, container, false);
        stationView = (ListView) view.findViewById(R.id.station_list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        favourites = Favourites.getFavourites();
        resetAdapter();
    }

    @Override
    public void favouriteAdded(Station station) {
        favourites.add(station);
        resetAdapter();
    }

    @Override
    public void favouriteRemoved(Station station) {
        favourites.remove(station);
        resetAdapter();
    }

    private void resetAdapter() {
        stationView.setAdapter(
                new StationAdapter(
                        getActivity(),
                        copyOf(favourites),
                        this.state,
                        this)
        );
    }
}
