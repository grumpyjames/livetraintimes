package org.grumpysoft;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import java.util.List;

public class ShowDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details);

        final DepartingTrain departingTrain = (DepartingTrain) getIntent().getExtras().get("train");
        assert departingTrain != null;

        final LinearLayout header = (LinearLayout) findViewById(R.id.detailsHeader);
        final TextView mainHeaderText = (TextView) header.findViewById(R.id.headerTextMain);
        if (departingTrain.destinationList().size() > 1) {
            mainHeaderText.setText(
                    "This train splits at " + Joiner.on(", ").join(departingTrain.serviceDetails().splitPoints()) + "\n" +
                    "Show the portion to: ");
            final LinearLayout linearLayout = (LinearLayout) header.findViewById(R.id.portions);
            final List<List<CallingPoint>> portions = departingTrain.serviceDetails().allParts();
            final List<CallingPoint> masterCallingPoints = portions.get(0);
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
            addTextView(linearLayout, last(masterCallingPoints).locationName, onClickListener, true);
            for (List<CallingPoint> branchCallingPoints : Iterables.skip(portions, 1)) {
                addTextView(linearLayout, " | ");
                addTextView(linearLayout, last(branchCallingPoints).locationName, onClickListener, false);
            }
        } else {
            mainHeaderText.setText("This train calls at:");
        }

        showCallingPoints(departingTrain.serviceDetails());
    }

    private void addTextView(LinearLayout linearLayout,
                             String text,
                             View.OnClickListener onClickListener,
                             boolean bold) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
