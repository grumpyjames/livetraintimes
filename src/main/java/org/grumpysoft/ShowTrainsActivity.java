package org.grumpysoft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class ShowTrainsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_board);               

        final TableLayout table = (TableLayout) findViewById(R.id.board);
        
        populateBoard(table, State.board);
    }
    
    private static class TrainTableRow extends TableRow {

        private final Typeface tf;

        public TrainTableRow(Context context, Typeface tf, DepartingTrain train) {
            super(context);
            this.tf = tf;
            addStationNameView(train);
            addPlatformView(train);
            addScheduledAtView(train);
            addExpectedAtView(train);
        }

        private void addExpectedAtView(DepartingTrain train) {
            addRightGravityTextView(train.expectedAt());
        }

        private void addScheduledAtView(DepartingTrain train) {
            addRightGravityTextView(train.scheduledAt());
        }

        private void addPlatformView(DepartingTrain train) {
            addRightGravityTextView(train.platform());
        }

        private void addRightGravityTextView(String content) {
            final TextView view = textViewWithGravity(Gravity.RIGHT);
            view.setPadding(0, 0, 3, 0);
            view.setText(content);
            addView(view);
        }

        private void addStationNameView(DepartingTrain train) {
            final TextView view = textViewWithGravity(Gravity.LEFT);
            view.setGravity(Gravity.LEFT);
            view.setPadding(3, 0, 3, 0);
            view.setText(Joiner.on(", ").join(train.destinationList()));
            addView(view);
        }

        private TextView textViewWithGravity(int gravity) {
            final TextView view = new TextView(getContext());
            view.setGravity(gravity);
            view.setTypeface(tf);
            view.setTextColor(getResources().getColor(R.color.orange));
            return view;
        }
    }

    private void populateBoard(TableLayout table, DepartureBoard board) {
        Utility.changeFonts(table, getAssets());
        final Typeface tf = Typeface.createFromAsset(getAssets(), "britrln.ttf");
        final List<DepartingTrain> trains = ImmutableList.copyOf(board.departingTrains());
        if (trains.size() > 0) {
            table.removeAllViews();
            for (DepartingTrain train: board.departingTrains()) {
                 table.addView(new TrainTableRow(getBaseContext(), tf, train));
            }
        }
    }
}
