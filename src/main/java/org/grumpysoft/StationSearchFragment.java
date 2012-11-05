package org.grumpysoft;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.collect.Lists;

import java.util.List;

public class StationSearchFragment extends Fragment implements MiniFragment {
    private Typeface tf;
    private ListView resultView;
    private EditText editView;

    @Override
    public void initialize(Context context, State state) {
        final StationAdapter adapter = new StationAdapter(context, tf, Lists.<Station>newArrayList(), state);
        resultView.setAdapter(adapter);
        attachEditListener(editView, resultView, context, tf, state);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_stations, container, false);
        editView = (EditText) view.findViewById(R.id.search_string);
        resultView = (ListView) view.findViewById(R.id.result_list);
        tf = Typeface.createFromAsset(inflater.getContext().getAssets(), "britrln.ttf");

        return view;
    }

    private void attachEditListener(final EditText editView, final ListView resultView,
                                    final Context context, final Typeface typeface, final State state) {
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    final List<Station> results = StationService.findStations(charSequence.toString());
                    resultView.setAdapter(new StationAdapter(context, typeface, results, state));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


}
