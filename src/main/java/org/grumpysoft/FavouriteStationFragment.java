package org.grumpysoft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;

public class FavouriteStationFragment extends Fragment {
    private ListView stationView;
    private Bundle state;

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
        final StationAdapter adapter =
                new StationAdapter(getActivity(), ImmutableList.copyOf(Favourites.getFavourites()), this.state);
        stationView.setAdapter(adapter);
    }

}
