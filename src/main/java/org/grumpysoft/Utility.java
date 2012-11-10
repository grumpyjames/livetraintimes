package org.grumpysoft;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Utility {

    private static final boolean customFontEnabled = false;

    /**
     * Precondition: must be in an initialized activity
     */
    static void changeFonts(ViewGroup root, AssetManager assets, Resources resources) {
        for(int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if(v instanceof TextView) {
                changeFonts((TextView) v, assets, resources);
            } else if(v instanceof ViewGroup) {
                changeFonts((ViewGroup)v, assets, resources);
            }
        }
    }

    static void changeFonts(TextView view, AssetManager assets, Resources resources) {
        final Typeface tf = Typeface.createFromAsset(assets, "britrln.ttf");
        if (customFontEnabled) {
            view.setTypeface(tf);
            view.setTextColor(resources.getColor(R.color.orange));
        }
    }
}
