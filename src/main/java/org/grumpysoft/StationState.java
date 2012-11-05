package org.grumpysoft;

import android.widget.TextView;

import java.io.Serializable;

class StationState implements Serializable {
    String fromStation;
    String toStation;
    private final transient TextView view;

    StationState(TextView view) {
        this.view = view;
        this.fromStation = null;
        this.toStation = null;
        updateView();
    }

    void withFromStation(String station) {
        this.fromStation = station;
        updateView();
    }

    void withToStation(String station) {
        this.toStation = station;
        updateView();
    }

    private void updateView() {
        view.setText(fromOrTo());
    }

    private CharSequence fromOrTo() {
        if (fromStation == null)
            return "From..";
        else if (toStation == null)
            return "From " + fromStation + " To...";
        else
            return "From " + fromStation + " To " + toStation;
    }

    public boolean unwind() {
        final boolean result;
        if (toStation != null) {
            toStation = null;
            result = false;
        } else if (fromStation != null) {
            fromStation = null;
            result = false;
        } else {
            result = true;
        }
        updateView();
        return result;
    }
}
