package org.grumpysoft;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.widget.SectionIndexer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.grumpysoft.impl.Stations;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.transform;

class IndexedStationAdapter extends StationAdapter implements SectionIndexer {
    private static final List<CharSequence> allStations = copyOf(transform(Stations.allStations(),
        new Function<Station, CharSequence>() {
                public CharSequence apply(Station station) {
                    return station.fullName();
                }
        }));

    private static final List<String> existentLetters = copyOf(new String[]{"A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "Y", "Z"});
    private static final List<IndexResult> results = workoutIndices();


    public IndexedStationAdapter(Context context, Typeface typeface, State state) {
        super(context, typeface, copyOf(Stations.allStations()), state);
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

    private static List<IndexResult> workoutIndices() {
        final String[] indexKeys = combine(existentLetters, lowercase(existentLetters));
        return index(allStations, indexKeys);
    }

    private static List<String> lowercase(List<String> existentLetters) {
        return Lists.transform(existentLetters, new Function<String, String>() {
            public String apply(String s) {
                return s.toLowerCase();
            }
        });
    }

    private static String[] combine(List<String> existentLetters, List<String> lowercase) {
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

    private static List<IndexResult> index(List<CharSequence> toIndex, String[] toIndexBy) {
        int currentPosition = 1; // skip "Anywhere!"
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

    private static class IndexResult {
        private final String indexKey;
        private final int index;

        private IndexResult(int index, String indexKey) {
            this.index = index;
            this.indexKey = indexKey;
        }
    }

}
