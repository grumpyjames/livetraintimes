package net.digihippo.ltt.android;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import net.digihippo.ltt.BadTrainState;
import net.digihippo.ltt.CallingPoint;
import net.digihippo.ltt.Consumer;
import net.digihippo.ltt.DepartingTrain;

import java.util.List;

public class ShowDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(net.digihippo.ltt.R.layout.show_details);

        final DepartingTrain departingTrain = (DepartingTrain) getIntent().getExtras().get("train");
        assert departingTrain != null;

        final LinearLayout header = (LinearLayout) findViewById(net.digihippo.ltt.R.id.detailsHeader);
        final TextView mainHeaderText = (TextView) header.findViewById(net.digihippo.ltt.R.id.headerTextMain);
        final TextView secondaryHeaderText = (TextView) header.findViewById(net.digihippo.ltt.R.id.headerTextSecondary);
        if (departingTrain.destinationList().size() > 1) {
            mainHeaderText.setText(
                    "This train splits at " + Joiner.on(", ").join(departingTrain.serviceDetails().splitPoints()));
            secondaryHeaderText.setText("Show the portion to: ");
            final LinearLayout linearLayout = (LinearLayout) header.findViewById(net.digihippo.ltt.R.id.portions);
            final List<List<CallingPoint>> portions = departingTrain.serviceDetails().allParts();
            final List<CallingPoint> masterCallingPoints = portions.get(0);
            final View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tv = (TextView) view;
                    if (tv.getTypeface() != null && tv.getTypeface().isBold()) {
                        // this is a horrible, horrible way to do this
                        // and you should feel very bad indeed.
                        return;
                    }

                    for (int i = 0; i < linearLayout.getChildCount(); i++) {
                        TextView child = (TextView) linearLayout.getChildAt(i);
                        child.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    }

                    tv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
                    String text = tv.getText().toString();
                    for (List<CallingPoint> branch : portions) {
                        if (last(branch).station.fullName().equals(text)) {
                            showCallingPoints(branch);
                        }
                    }
                }
            };
            final float weight = 1F / ((float) portions.size());

            addTextView(linearLayout, last(masterCallingPoints).station.fullName(), onClickListener, true, weight);

            for (List<CallingPoint> branchCallingPoints : portions.subList(1, portions.size())) {
                addTextView(linearLayout, last(branchCallingPoints).station.fullName(), onClickListener, false, weight);
            }

        } else {
            mainHeaderText.setText("This train calls at:");
            header.removeView(secondaryHeaderText);
        }

        showCallingPoints(departingTrain.serviceDetails());
    }

    private void addTextView(
        LinearLayout linearLayout,
        String text,
        View.OnClickListener onClickListener,
        boolean bold,
        float weight)
    {
        TextView textView = new TextView(this);
        textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        textView.setText(text);
        textView.setOnClickListener(onClickListener);
        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = weight;
        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        if (bold) {
            textView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
        }
        linearLayout.addView(textView);
    }

    private void showCallingPoints(Iterable<CallingPoint> callingPoints) {
        TableLayout table = (TableLayout) findViewById(net.digihippo.ltt.R.id.detailsTable);
        table.removeAllViews();
        for (CallingPoint callingPoint : callingPoints) {
            TableRow row = (TableRow) View.inflate(ShowDetailsActivity.this, net.digihippo.ltt.R.layout.detail_entry, null);

            final TextView scheduledAt = (TextView) row.findViewById(net.digihippo.ltt.R.id.scheduledAt);
            callingPoint.et
                    .consume(new Consumer<BadTrainState>() {
                        @Override
                        public void consume(BadTrainState badTrainState) {
                            scheduledAt.setText(badTrainState.name());
                        }
                    }, new Consumer<String>() {
                        @Override
                        public void consume(String t) {
                            scheduledAt.setText(t);
                        }
                    });
            TextView stationView = (TextView) row.findViewById(net.digihippo.ltt.R.id.station_name);
            stationView.setText(callingPoint.station.fullName());

            table.addView(row);
        }
    }

    private CallingPoint last(List<CallingPoint> callingPoints) {
        return callingPoints.get(callingPoints.size() - 1);
    }
}
