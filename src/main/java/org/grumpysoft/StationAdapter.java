package org.grumpysoft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

class StationAdapter extends BaseAdapter {
    private final Context context;
    private final List<Station> stations;
    private final TextView.OnClickListener clickListener;

    public StationAdapter(final Context context, final List<Station> stations, final State state) {
        this.context = context;
        this.stations = stations;
        this.clickListener = new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.selectStation(((TextView) view).getText().toString()))
                    state.launchShowTrainsActivity(context);
            }
        };
    }

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Object getItem(int position) {
        return stations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View uselessView, ViewGroup parent) {
        final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View convertView = layoutInflater.inflate(R.layout.station, null);
        final Station station = (Station) getItem(position);

        final TextView textView = (TextView) convertView.findViewById(R.id.station_name);
        Utility.changeFonts(textView, context.getAssets(), context.getResources());

        textView.setText(station.fullName());
        textView.setOnClickListener(clickListener);

        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.favourite_toggle);
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

        return convertView;
    }
}
