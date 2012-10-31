package org.grumpysoft;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.collect.ImmutableList;

import java.util.Set;

public class FavouriteStationFragment extends Fragment {
    private ListView stationView;
    private Typeface tf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_station, container, false);
        stationView = (ListView) view.findViewById(R.id.station_list);
        tf = Typeface.createFromAsset(inflater.getContext().getAssets(), "britrln.ttf");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStationSelector(stationView, getActivity());
    }

    private void setupStationSelector(ListView listView, final Context context) {
        final Set<Station> favourites = Favourites.getFavourites();
        final StationAdapter adapter = new StationAdapter(this, context, tf, ImmutableList.copyOf(favourites));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Station station = (Station) adapter.getItem(i);
                final Intent intent = State.selectStation(station.fullName(), context);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }
}
