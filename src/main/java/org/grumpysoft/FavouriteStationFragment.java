package org.grumpysoft;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;

public class FavouriteStationFragment extends Fragment implements MiniFragment {
    private ListView stationView;
    private final State state;

    public FavouriteStationFragment(State state) {
        this.state = state;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_station, container, false);
        stationView = (ListView) view.findViewById(R.id.station_list);
        return view;
    }

    @Override
    public void onShow(final Context context, final State state) {
        initialize(context, this.state);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize(getActivity(), null);
    }

    @Override
    public void initialize(final Context context, final State state) {
        final StationAdapter adapter =
                new StationAdapter(context, ImmutableList.copyOf(Favourites.getFavourites()), this.state);
        stationView.setAdapter(adapter);
    }
}
