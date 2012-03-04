package org.grumpysoft;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;

public class FavouriteStationFragment extends Fragment {
    private ListView stationView;
    private Typeface tf;

    private final class SimpleAdapter extends ArrayAdapter<CharSequence> {

        public SimpleAdapter(Context context, int textViewResourceId, ImmutableList<CharSequence> favourites) {
            super(context, textViewResourceId, favourites);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(getResources().getColor(R.color.orange));
            view.setTypeface(tf);
            return view;
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.select_station, container, false);
        stationView = (ListView) view.findViewById(R.id.station_list);
        tf = Typeface.createFromAsset(inflater.getContext().getAssets(), "britrln.ttf");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStationSelector(stationView, getActivity().getBaseContext());
    }

    private void setupStationSelector(ListView listView, final Context context) {
        final ImmutableList<CharSequence> favourites = ImmutableList.<CharSequence>of("Denmark Hill", "Lewisham", "London Victoria");
        final SimpleAdapter adapter =
                new SimpleAdapter(context, android.R.layout.simple_list_item_1, favourites);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CharSequence stationFullName = favourites.get(i);
                final Intent intent = State.selectStation(stationFullName.toString(), context);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }
}
