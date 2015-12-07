package org.grumpysoft;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StationSelectorFragment extends Fragment {

    private View fullView;
    private Bundle state;

    @Override
    public void setArguments(Bundle args) {
        this.state = args;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fullView = inflater.inflate(R.layout.select_station, container, false);

        return fullView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStationSelector((ListView) fullView.findViewById(R.id.station_list), getActivity());
    }

    private void setupStationSelector(ListView listView, final Context context) {
        listView.setAdapter(new IndexedStationAdapter(context, state));
    }
}
