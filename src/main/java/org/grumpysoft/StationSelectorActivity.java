package org.grumpysoft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.grumpysoft.impl.Stations;

import java.util.List;

public class StationSelectorActivity extends Fragment {

    private Typeface tf;

    private static final List<CharSequence> allStations = ImmutableList.copyOf(Iterables.transform(Stations.allStations(), new Function<Station, CharSequence>() {
        public CharSequence apply(Station station) {
            return station.fullName();
        }
    }));
    private View fullView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("view created");
        tf = Typeface.createFromAsset(inflater.getContext().getAssets(), "britrln.ttf");        
        fullView = inflater.inflate(R.layout.select_station, container, false);

        return fullView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("attached");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupStationSelector((ListView) fullView.findViewById(R.id.station_list), getActivity().getBaseContext(), getActivity().getIntent());
    }

    private static class IndexResult {
        private final String indexKey;
        private final int index;

        private IndexResult(int index, String indexKey) {
            this.index = index;
            this.indexKey = indexKey;
        }
    }

    private List<IndexResult> index(List<CharSequence> toIndex, String[] toIndexBy) {
        int currentPosition = 0;
        int currentIndexIndex = 0;
        final List<IndexResult> results = Lists.newArrayList();

        while (currentIndexIndex < toIndexBy.length && currentPosition < toIndex.size()) {
            final String currentValue = toIndex.get(currentPosition).toString();
            final String currentIndexKey = toIndexBy[currentIndexIndex];
            if(currentValue.startsWith(currentIndexKey)) {
                results.add(new IndexResult(currentPosition, currentIndexKey));
                ++currentIndexIndex;
                ++currentPosition;
            } else if (currentValue.substring(0, currentIndexKey.length()).compareTo(currentIndexKey) > 0) {
                ++currentIndexIndex;
            } else {
                ++currentPosition;
            }
        }
        return results;
    }

    private class StationAdapter extends ArrayAdapter<CharSequence> implements SectionIndexer {

        private final List<String> existentLetters = ImmutableList.copyOf(new String[]{"A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "Y", "Z"});

        private final List<IndexResult> results = workoutIndices();

        private List<IndexResult> workoutIndices() {
            final String[] indexKeys = combine(existentLetters, lowercase(existentLetters));
            return index(allStations, indexKeys);                        
        }

        private List<String> lowercase(List<String> existentLetters) {
            return Lists.transform(existentLetters, new Function<String, String>() {
                public String apply(String s) {
                    return s.toLowerCase();
                }
            });
        }

        private String[] combine(List<String> existentLetters, List<String> lowercase) {
            final String[] result = new String[existentLetters.size() * lowercase.size()];
            int currentIndex = 0;
            for(String letter: existentLetters) {
                for (String vowel: lowercase) {
                    result[currentIndex] = letter + vowel;
                    ++currentIndex;
                }
            }
            return result;
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
            return Lists.transform(results, new Function<IndexResult, Object>() {
                public Object apply(IndexResult indexResult) {
                    return indexResult.indexKey;
                }
            }).toArray(new Object[results.size()]);
        }

        public int getPositionForSection(int i) {
            return results.get(i).index;
        }

        public int getSectionForPosition(int i) {
            int currentIndex = 0;
            for (IndexResult result: results) {
                if (i < result.index)
                    return currentIndex;
                ++currentIndex;
            }
            return currentIndex;
        }
    }


    private void setupStationSelector(ListView listView, final Context context, final Intent outerIntent) {
        final ArrayAdapter<CharSequence> adapter = new StationAdapter(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final CharSequence stationFullName = allStations.get(i);
                final Intent intent = new Intent(context, LiveTrainTimesActivity.class);
                intent.putExtras(outerIntent.getExtras());
                intent.putExtra("STATION", stationFullName);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
        listView.setSelection(getSelectedIndex(adapter, outerIntent));
    }

    private int getSelectedIndex(ArrayAdapter<CharSequence> adapter, final Intent intent) {
        final String currentlySelected = intent.getExtras().getString(LiveTrainTimesActivity.currentSelection);
        if (currentlySelected.equals(getResources().getString(R.string.anywhere)))
            return 0;
        else
            return adapter.getPosition(currentlySelected);
    }
}
