package org.grumpysoft;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class StationSelectorFragment extends Fragment implements MiniFragment {

    private View fullView;
    private final State state;

    public StationSelectorFragment(final State state) {
        this.state = state;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fullView = inflater.inflate(R.layout.select_station, container, false);

        return fullView;
    }

    @Override
    public void onShow(Context context, State state) {
        // nothing required
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize(getActivity(), null);
    }

    @Override
    public void initialize(Context baseContext, State state) {
        setupStationSelector((ListView) fullView.findViewById(R.id.station_list), baseContext);
    }

    private void setupStationSelector(ListView listView, final Context context) {
        listView.setAdapter(new IndexedStationAdapter(context, state));
    }
}
