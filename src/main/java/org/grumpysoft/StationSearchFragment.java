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

import java.util.Collection;
import java.util.List;

public class StationSearchFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.search_stations, container, false);
        final EditText editView = (EditText) view.findViewById(R.id.search_string);
        final ListView resultView = (ListView) view.findViewById(R.id.result_list);
        final Typeface tf = Typeface.createFromAsset(inflater.getContext().getAssets(), "britrln.ttf");
        final StationAdapter adapter = new StationAdapter(inflater.getContext(), tf);
        resultView.setAdapter(adapter);
        attachEditListener(editView, adapter);
        attachOnClickListener(resultView, adapter, inflater);
        return view;
    }

    private void attachOnClickListener(final ListView resultView,
                                       final StationAdapter adapter,
                                       final LayoutInflater inflater) {
        resultView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Station station = adapter.getItem(i);
                startActivity(State.selectStation(station.fullName(), inflater.getContext()));
            }
        });
    }

    private class StationAdapter extends ArrayAdapter<Station> {
        private final Typeface tf;

        public StationAdapter(Context context, Typeface tf) {
            super(context, android.R.layout.simple_list_item_1, Lists.<Station>newArrayList());
            this.tf = tf;
        }

        @Override
        public void addAll(Collection<? extends Station> stations) {
            super.addAll(stations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            if(view instanceof TextView) {
                final TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.orange));
                textView.setTypeface(tf);
                textView.setText(getItem(position).fullName());
            }
            return view;
        }
    }

    private void attachEditListener(EditText editView, final StationAdapter adapter) {
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {
                    final List<Station> results = StationService.findStations(charSequence.toString());
                    // addAll throws NoSuchMethodError on a Desire Z. Wtf?
                    for (Station s: results)
                        adapter.add(s);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


}
