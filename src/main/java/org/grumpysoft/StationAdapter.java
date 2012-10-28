package org.grumpysoft;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Collection;
import java.util.List;

class StationAdapter extends ArrayAdapter<Station> {
    private final Typeface tf;
    private final Fragment fragment;
    private final TextView.OnClickListener clickListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            fragment.startActivity(State.selectStation(((TextView) view).getText().toString(), getContext()));
        }
    };

    public StationAdapter(Fragment fragment, Context context, Typeface tf, List<Station> stations) {
        super(context, android.R.layout.simple_list_item_1, stations);
        this.fragment = fragment;
        this.tf = tf;
    }

    @Override
    public void addAll(Collection<? extends Station> stations) {
        super.addAll(stations);
    }

    @Override
    public View getView(final int position, View uselessView, ViewGroup parent) {
        final View convertView = LayoutInflater.from(getContext()).inflate(R.layout.station, parent, false);

        final Station station = getItem(position);

        final TextView textView = (TextView) convertView.findViewById(R.id.station_name);
        textView.setTextColor(fragment.getResources().getColor(R.color.orange));
        textView.setTypeface(tf);
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
