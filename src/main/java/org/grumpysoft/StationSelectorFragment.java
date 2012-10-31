package org.grumpysoft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class StationSelectorFragment extends Fragment {

    private Typeface tf;
    private View fullView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(inflater.getContext().getAssets(), "britrln.ttf");
        fullView = inflater.inflate(R.layout.select_station, container, false);

        return fullView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStationSelector((ListView) fullView.findViewById(R.id.station_list), getActivity().getBaseContext());
    }

    private void setupStationSelector(ListView listView, final Context context) {
        listView.setAdapter(new IndexedStationAdapter(this, context, tf));
    }
}
