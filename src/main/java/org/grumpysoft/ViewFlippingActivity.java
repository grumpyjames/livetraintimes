package org.grumpysoft;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

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

        final TextView textView = (TextView) findViewById(R.id.fromOrTo);
        final Typeface tf = Typeface.createFromAsset(getAssets(), "britrln.ttf");
        textView.setTypeface(tf);
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
