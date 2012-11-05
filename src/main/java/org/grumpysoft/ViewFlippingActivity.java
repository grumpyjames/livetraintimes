package org.grumpysoft;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

public class ViewFlippingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flipping_views);

        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final ViewFlipper viewflipper = (ViewFlipper) findViewById(R.id.flipper);

        attachAnimationStarter(viewflipper, R.id.dir, 0);
        attachAnimationStarter(viewflipper, R.id.fav, 1);
        attachAnimationStarter(viewflipper, R.id.search, 2);

        createSubView(savedInstanceState, inflater, viewflipper, StationSelectorFragment.class, 0);
        createSubView(savedInstanceState, inflater, viewflipper, FavouriteStationFragment.class, 1);
        createSubView(savedInstanceState, inflater, viewflipper, StationSearchFragment.class, 2);
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
                               ViewFlipper viewflipper, Class<? extends MiniFragment> klass, int index) {
        final MiniFragment fragment;
        try {
            fragment = klass.newInstance();
            viewflipper.addView(fragment.onCreateView(inflater, viewflipper, savedInstanceState), index);
            fragment.initialize(this);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
