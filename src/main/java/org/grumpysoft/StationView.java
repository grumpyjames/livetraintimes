package org.grumpysoft;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public final class StationView {

    private StationView() {}

    public static TextView initialiseStationView(
            final View stationView,
            final Station station,
            final View.OnClickListener clickListener) {
        final TextView textView = (TextView) stationView.findViewById(R.id.station_name);

        textView.setText(station.fullName());
        textView.setOnClickListener(clickListener);

        final CheckBox checkBox = (CheckBox) stationView.findViewById(R.id.favourite_toggle);
        checkBox.setEnabled(!station.equals(Anywhere.INSTANCE));
        checkBox.setChecked(Favourites.isFavourite(station));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean newValue) {
                if (station.equals(Anywhere.INSTANCE))
                    compoundButton.setChecked(true);
                else
                    Favourites.toggleFavourite(station, newValue);
            }
        });
        return textView;
    }
}