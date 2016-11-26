package net.digihippo.ltt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.*;

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
        final View view = inflater.inflate(net.digihippo.ltt.R.layout.select_station, container, false);
        stationView = (ListView) view.findViewById(net.digihippo.ltt.R.id.station_list);
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
        final List<Station> stations = new ArrayList<Station>(favourites.size());
        Collections.sort(stations, new Comparator<Station>() {
            @Override
            public int compare(Station s1, Station s2) {
                return s1.fullName().compareTo(s2.fullName());
            }
        });

        stationView.setAdapter(
                new StationAdapter(
                        getActivity(),
                        copyOf(favourites),
                        this.state,
                        this)
        );
    }
}
