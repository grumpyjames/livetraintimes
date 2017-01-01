package net.digihippo.ltt.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.digihippo.ltt.FavouriteListener;
import net.digihippo.ltt.Station;
import net.digihippo.ltt.StationIndex;
import net.digihippo.ltt.Stations;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

public class StationSearchFragment extends Fragment implements FavouriteListener {
    private ListView resultView;
    private EditText editView;
    private Bundle state;
    private FavouriteListener favouriteListener;
    private StationIndex<Station> stationIndex;

    @Override
    public void setArguments(Bundle args) {
        state = args;
    }

    public void initialize(Context context) {
        final StationAdapter adapter =
                new StationAdapter(
                        context,
                        Lists.<Station>newArrayList(),
                        this.state,
                        this);
        resultView.setAdapter(adapter);
        stationIndex = buildStationIndex();
        attachEditListener(editView, resultView, context);
    }

    private StationIndex<Station> buildStationIndex() {
        try {
            //noinspection unchecked
            return StationIndex.parse(
                    Stations.allStations(),
                    new Function<Station, String>() {
                        @Override
                        public String apply(Station station) {
                            return station.fullName();
                        }
                    },
                    new Function<Station, String>() {
                        @Override
                        public String apply(Station station) {
                            return station.threeLetterCode();
                        }
                    }
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(net.digihippo.ltt.R.layout.search_stations, container, false);
        editView = (EditText) view.findViewById(net.digihippo.ltt.R.id.search_string);
        resultView = (ListView) view.findViewById(net.digihippo.ltt.R.id.result_list);

        initialize(getActivity());

        return view;
    }

    private void attachEditListener(final EditText editView, final ListView resultView, final Context context) {
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    final List<Station> results = copyOf(stationIndex.search(charSequence.toString()));
                    resultView.setAdapter(new StationAdapter(context, results, state, StationSearchFragment.this));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.favouriteListener = (FavouriteListener) activity;
    }

    @Override
    public void favouriteAdded(Station station) {
        if (favouriteListener != null) favouriteListener.favouriteAdded(station);
    }

    @Override
    public void favouriteRemoved(Station station) {
        if (favouriteListener != null) favouriteListener.favouriteRemoved(station);
    }
}
