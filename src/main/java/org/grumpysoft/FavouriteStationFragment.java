package org.grumpysoft;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;

import java.util.Set;

public class FavouriteStationFragment extends Fragment implements MiniFragment {
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
        initialize(getActivity(), null);
    }

    @Override
    public void initialize(final Context context, final State state) {
        final Set<Station> favourites = Favourites.getFavourites();
        final StationAdapter adapter = new StationAdapter(context, tf, ImmutableList.copyOf(favourites), state);
        stationView.setAdapter(adapter);
    }
}
