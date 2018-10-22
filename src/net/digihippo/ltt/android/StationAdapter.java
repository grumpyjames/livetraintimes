package net.digihippo.ltt.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.digihippo.ltt.FavouriteListener;
import net.digihippo.ltt.Station;

import java.util.List;

class StationAdapter extends BaseAdapter {
    private final Context context;
    private final List<Station> stations;
    private final FavouriteListener favouriteListener;
    private final TextView.OnClickListener clickListener;

    public StationAdapter(
            final Context context,
            final List<Station> stations,
            final Bundle state,
            final FavouriteListener favouriteListener) {
        this.context = context;
        this.stations = stations;
        this.favouriteListener = favouriteListener;
        this.clickListener = new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(context, NavigatorActivity.class);
                // Android 9 is less permissive of passing null to putExtras
                if (state != null)
                {
                    intent.putExtras(state);
                }
                else
                {
                    intent.putExtras(new Bundle());
                }
                intent.putExtra("station", ((TextView) view).getText().toString());
                context.startActivity(intent);
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
        final View stationView = layoutInflater.inflate(net.digihippo.ltt.R.layout.station, null);
        final Station station = (Station) getItem(position);

        StationView.initialiseStationView(
                stationView,
                station,
                clickListener,
                favouriteListener);

        return stationView;
    }

}
