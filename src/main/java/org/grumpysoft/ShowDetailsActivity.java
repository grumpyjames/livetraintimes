package org.grumpysoft;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;

public class ShowDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details);

        final DepartingTrain departingTrain = (DepartingTrain) getIntent().getExtras().get("train");
        assert departingTrain != null;

        LinearLayout header = (LinearLayout) findViewById(R.id.detailsHeader);
        if (departingTrain.destinationList().size() > 1) {
            addTextView(header, "This train splits at " + Joiner.on(", ").join(departingTrain.serviceDetails().splitPoints()));
            addTextView(header, "Show the portion to: ");
            final LinearLayout linearLayout =
                    (LinearLayout) View.inflate(this, R.layout.choose_portion, null);
            final List<List<CallingPoint>> portions =
                    departingTrain.serviceDetails().allParts();
            final List<CallingPoint> callingPoints = copyOf(portions.get(0));
            final View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        TextView child = (TextView) linearLayout.getChildAt(i);
                        child.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    }

                    TextView tv = (TextView) view;
                    tv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                    String text = tv.getText().toString();
                    for (List<CallingPoint> branch : portions) {
                        if (last(branch).locationName.equals(text)) {
                            showCallingPoints(branch);
                        }
                    }
                }
            };
            addTextView(linearLayout, last(callingPoints).locationName, onClickListener, true);
            for (List<CallingPoint> callingPoint : Iterables.skip(portions, 1)) {
                addTextView(linearLayout, " | ");
                addTextView(linearLayout, last(callingPoint).locationName, onClickListener, false);
            }
            header.addView(linearLayout);
        } else {
            addTextView(header, "This train calls at: ");
        }

        showCallingPoints(departingTrain.serviceDetails());
    }

    private void addTextView(LinearLayout linearLayout,
                             String text,
                             View.OnClickListener onClickListener,
                             boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setOnClickListener(onClickListener);
        if (bold) {
            textView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        }
        linearLayout.addView(textView);
    }

    private void showCallingPoints(Iterable<CallingPoint> callingPoints) {
        TableLayout table = (TableLayout) findViewById(R.id.detailsTable);
        table.removeAllViews();
        for (CallingPoint callingPoint : callingPoints) {
            TableRow row = (TableRow) View.inflate(ShowDetailsActivity.this, R.layout.detail_entry, null);

            TextView scheduledAt = (TextView) row.findViewById(R.id.scheduledAt);
            scheduledAt.setText(callingPoint.scheduledAtTime);
            TextView stationView = (TextView) row.findViewById(R.id.station_name);
            stationView.setText(callingPoint.locationName);

            table.addView(row);
        }
    }

    private CallingPoint last(List<CallingPoint> callingPoints) {
        return callingPoints.get(callingPoints.size() - 1);
    }

    private void addTextView(LinearLayout header, String text) {
        TextView one = new TextView(this);
        one.setText(text);
        header.addView(one);
    }
}
