package org.grumpysoft;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

import static org.grumpysoft.Favourites.currentFavouritesAsArray;

public class ViewFlippingActivity extends Activity {
    private State state;

    @Override
    public void onBackPressed() {
        if (state.unwind()) super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flipping_views);

        if (savedInstanceState != null) {
            for (Station station : deserializeFavouritesFrom(savedInstanceState.getCharSequenceArray("favourites")))
                Favourites.toggleFavourite(station, true);
        }

        final TextView textView = (TextView) findViewById(R.id.fromOrTo);
        Utility.changeFonts(textView, getAssets(), getResources());
        state = new State(textView);

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final ViewFlipper viewflipper = (ViewFlipper) findViewById(R.id.flipper);

        attachAnimationStarter(viewflipper, R.id.dir, 0);
        attachAnimationStarter(viewflipper, R.id.fav, 1);
        attachAnimationStarter(viewflipper, R.id.search, 2);

        createSubView(savedInstanceState, inflater, viewflipper, StationSelectorFragment.class, 0, state);
        createSubView(savedInstanceState, inflater, viewflipper, FavouriteStationFragment.class, 1, state);
        createSubView(savedInstanceState, inflater, viewflipper, StationSearchFragment.class, 2, state);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequenceArray("favourites", currentFavouritesAsArray());
    }

    private List<Station> deserializeFavouritesFrom(CharSequence[] favourites) {
        return Lists.transform(ImmutableList.copyOf(favourites), new Function<CharSequence, Station>() {
            @Override
            public Station apply(CharSequence charSequence) {
                return Iterables.getOnlyElement(StationService.findStations(charSequence.toString()));
            }
        });
    }

    private void attachAnimationStarter(final ViewFlipper viewflipper, final int buttonId,
                                        final int whichChild) {
        final Button button = (Button) findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewflipper.setDisplayedChild(whichChild);
            }
        });
    }

    private void createSubView(Bundle savedInstanceState, LayoutInflater inflater,
                               ViewFlipper viewflipper, Class<? extends MiniFragment> klass,
                               int index, State state) {
        final MiniFragment fragment;
        try {
            fragment = klass.newInstance();
            viewflipper.addView(fragment.onCreateView(inflater, viewflipper, savedInstanceState), index);
            fragment.initialize(this, state);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
