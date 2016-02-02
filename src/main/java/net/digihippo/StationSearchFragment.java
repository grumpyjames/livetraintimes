package net.digihippo;

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
import com.google.common.collect.Lists;

import java.util.List;

public class StationSearchFragment extends Fragment implements FavouriteListener {
    private ListView resultView;
    private EditText editView;
    private Bundle state;
    private FavouriteListener favouriteListener;

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
        attachEditListener(editView, resultView, context);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_stations, container, false);
        editView = (EditText) view.findViewById(R.id.search_string);
        resultView = (ListView) view.findViewById(R.id.result_list);

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
                    final List<Station> results = Stations.find(charSequence.toString());
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