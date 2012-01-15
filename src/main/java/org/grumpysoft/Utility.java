package org.grumpysoft;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Utility {

    /**
     * Precondition: must be in an initialized activity
     */
    static void changeFonts(ViewGroup root, AssetManager assets) {
        final Typeface tf = Typeface.createFromAsset(assets, "britrln.ttf");

        for(int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if(v instanceof TextView) {
                ((TextView)v).setTypeface(tf);
            } else if(v instanceof ViewGroup) {
                changeFonts((ViewGroup)v, assets);
            }
        }
    }
}
