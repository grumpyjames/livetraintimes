package net.digihippo.ltt.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import net.digihippo.ltt.*;

import java.util.ArrayList;
import java.util.List;

public class StationSearchFragment extends SelectableFragment implements FavouriteListener {
    private ListView resultView;
    private EditText editView;
    private Bundle state;
    private FavouriteListener favouriteListener;
    private StationIndex<Station> stationIndex;

    @Override
    public void setArguments(Bundle args) {
        state = args;
    }

    private void initialize(Context context) {
        final StationAdapter adapter =
                new StationAdapter(
                        context,
                        new ArrayList<Station>(),
                        this.state,
                        this);
        resultView.setAdapter(adapter);
        stationIndex = buildStationIndex();
        attachEditListener(editView, resultView, context);
    }

    private StationIndex<Station> buildStationIndex() {
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
                    final List<Station> results = stationIndex.search(charSequence.toString());
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

    @Override
    public void onSelected(boolean selected)
    {
        if (selected)
        {
            InputMethodManager imm =
                (InputMethodManager) editView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            editView.requestFocus();

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
