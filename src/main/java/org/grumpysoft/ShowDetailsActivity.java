package org.grumpysoft;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

public class ShowDetailsActivity extends Activity {
    private DepartingTrain departingTrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details);

        final DepartingTrain departingTrain = (DepartingTrain) getIntent().getExtras().get("train");
        assert departingTrain != null;
        this.departingTrain = departingTrain;

        LinearLayout header = (LinearLayout) findViewById(R.id.detailsHeader);
        if (departingTrain.destinationList().size() > 1) {
            addTextView(header, "This train splits at " + Joiner.on(", ").join(departingTrain.serviceDetails().splitPoints()));
            addTextView(header, "Show the portion to: ");
            final LinearLayout linearLayout =
                    (LinearLayout) getLayoutInflater().inflate(R.layout.choose_portion, null);
            final List<List<CallingPoint>> things =
                    departingTrain.serviceDetails().allParts();
            final List<CallingPoint> callingPoints = copyOf(things.get(0));
            addTextView(linearLayout, callingPoints.get(callingPoints.size() - 1).locationName);
            for (List<CallingPoint> callingPoint : Iterables.skip(things, 1)) {
                addTextView(linearLayout, " | ");
                addTextView(linearLayout, callingPoint.get(callingPoint.size() - 1).locationName);
            }
            header.addView(linearLayout);
        } else {
            TextView textView = new TextView(this);
            textView.setText("This train calls at: ");
        }

        MyCallingPointConsumer callingPointConsumer =
                new MyCallingPointConsumer(findViewById(R.id.detailsTable));
        for (CallingPoint callingPoint : departingTrain.serviceDetails()) {
            callingPointConsumer.onSinglePoint(callingPoint.locationName, callingPoint.scheduledAtTime);
        }
    }

    private void addTextView(LinearLayout header, String text) {
        TextView one = new TextView(this);
        one.setText(text);
        header.addView(one);
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
