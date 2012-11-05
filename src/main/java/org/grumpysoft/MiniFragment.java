package org.grumpysoft;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface MiniFragment {
    void initialize(Context baseContext);

    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
