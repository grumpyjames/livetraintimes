package net.digihippo.ltt.android;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import net.digihippo.ltt.Anywhere;
import net.digihippo.ltt.FavouriteListener;
import net.digihippo.ltt.Station;

public final class StationView {

    private StationView() {}

    public static TextView initialiseStationView(
            final View stationView,
            final Station station,
            final View.OnClickListener clickListener,
            final FavouriteListener favouriteListener) {
        final TextView textView = (TextView) stationView.findViewById(net.digihippo.ltt.R.id.station_name);

        textView.setText(station.fullName());
        textView.setOnClickListener(clickListener);

        final CheckBox checkBox = (CheckBox) stationView.findViewById(net.digihippo.ltt.R.id.favourite_toggle);
        checkBox.setEnabled(!station.equals(Anywhere.INSTANCE));
        checkBox.setChecked(Favourites.isFavourite(station));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean newValue) {
                if (station.equals(Anywhere.INSTANCE))
                    compoundButton.setChecked(true);
                else {
                    if (newValue) {
                        favouriteListener.favouriteAdded(station);
                    } else {
                        favouriteListener.favouriteRemoved(station);
                    }

                }
            }
        });
        return textView;
    }
}
