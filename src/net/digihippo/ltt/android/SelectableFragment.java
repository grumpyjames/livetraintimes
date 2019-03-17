package net.digihippo.ltt.android;

import android.support.v4.app.Fragment;

public abstract class SelectableFragment extends Fragment
{
    public abstract void onSelected(boolean selected);
}
