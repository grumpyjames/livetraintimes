package org.grumpysoft;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ShowDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details);

        final DepartingTrain departingTrain = (DepartingTrain) getIntent().getExtras().get("train");
        assert departingTrain != null;
        MyCallingPointConsumer callingPointConsumer =
                new MyCallingPointConsumer(findViewById(R.id.detailsTable));
        for (CallingPoint callingPoint : departingTrain.serviceDetails()) {
            callingPointConsumer.onSinglePoint(callingPoint.locationName, callingPoint.scheduledAtTime);
        }
    }

    private class MyCallingPointConsumer {
        private final TableLayout table;

        public MyCallingPointConsumer(View viewById) {
            this.table = (TableLayout) viewById;
        }

        public void onSinglePoint(String stationName, String scheduledAtTime) {
            TableRow row = (TableRow) View.inflate(ShowDetailsActivity.this, R.layout.detail_entry, null);

            TextView scheduledAt = (TextView) row.findViewById(R.id.scheduledAt);
            scheduledAt.setText(scheduledAtTime);
            TextView stationView = (TextView) row.findViewById(R.id.station_name);
            stationView.setText(stationName);

            table.addView(row);
        }
    }
}
