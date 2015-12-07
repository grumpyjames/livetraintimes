package org.grumpysoft;

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

public class StationSearchFragment extends Fragment {
    private ListView resultView;
    private EditText editView;
    private Bundle state;

    @Override
    public void setArguments(Bundle args) {
        state = args;
    }

    public void initialize(Context context) {
        final StationAdapter adapter = new StationAdapter(context, Lists.<Station>newArrayList(), this.state);
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
                    final List<Station> results = StationService.findStations(charSequence.toString());
                    resultView.setAdapter(new StationAdapter(context, results, state));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


}
