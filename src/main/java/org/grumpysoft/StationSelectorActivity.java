package org.grumpysoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.grumpysoft.impl.Stations;

import java.util.List;

public class StationSelectorActivity extends Activity {

    private Typeface tf;

    private static final List<CharSequence> allStations = ImmutableList.copyOf(Iterables.transform(Stations.allStations(), new Function<Station, CharSequence>() {
        public CharSequence apply(Station station) {
            return station.fullName();
        }
    }));

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_station);

        tf = Typeface.createFromAsset(getAssets(), "britrln.ttf");

        setupStationSelector();
    }

    private class StationAdapter extends ArrayAdapter<CharSequence> implements SectionIndexer {

        private final String[] alphabet = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "Y"};
        private final int[] indices = workoutIndices();

        private int[] workoutIndices() {
            final int[] indices = new int[24];
            int currentCharIndex = 0;
            int currentStationPosition = 0;
            for (CharSequence stationName : allStations) {
                if (stationName.charAt(0) == alphabet[currentCharIndex].charAt(0)) {
                    indices[currentCharIndex] = currentStationPosition;
                    if (++currentCharIndex == 24) break;
                }
                ++currentStationPosition;
            }
            return indices;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            if(view instanceof TextView) {
                final TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.orange));
                textView.setTypeface(tf);
            }
            return view;
        }

        public StationAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, allStations);
        }

        public Object[] getSections() {
            return alphabet;
        }

        public int getPositionForSection(int i) {
            return indices[i];
        }

        public int getSectionForPosition(int i) {
            return allStations.get(i).charAt(0) - 'A';
        }
    }


    private void setupStationSelector() {
        final ListView listView = (ListView) findViewById(R.id.station_list);
        final ArrayAdapter<CharSequence> adapter = new StationAdapter(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CharSequence stationFullName = allStations.get(i);
                final Intent intent = new Intent(getBaseContext(), LiveTrainTimesActivity.class);
                intent.putExtras(getIntent().getExtras());
                intent.putExtra("STATION", stationFullName);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }
}
