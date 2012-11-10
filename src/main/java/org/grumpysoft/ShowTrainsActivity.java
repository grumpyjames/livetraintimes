package org.grumpysoft;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

public class ShowTrainsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_board);

        final StationState stationState = (StationState) getIntent().getExtras().get("state");
        final State.BoardOrError board = State.fetchTrains(this, stationState);

        final TableLayout table = (TableLayout) findViewById(R.id.board);

        if (board.hasBoard())
            populateBoard(table, board.board());
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    private static class TrainTableRow extends TableRow {

        private TextView destinationArrivalTimeView;

        public TrainTableRow(Context context, DepartingTrain train) {
            super(context);
            setId(train.serviceId().hashCode()); //risky business!
            setPadding(1, 1, 1, 1);
            addStationNameView(train);
            addPlatformView(train);
            addScheduledAtView(train);
            addExpectedAtView(train);
            this.destinationArrivalTimeView = rightAlignedText();
            addView(destinationArrivalTimeView);
        }
        
        private void updateArrivalTime(String time) {
            destinationArrivalTimeView.setText(time);
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
            final TextView view = rightAlignedText();
            view.setText(content);
            addView(view);
        }

        private TextView rightAlignedText() {
            final TextView view = textViewWithGravity(Gravity.RIGHT);
            view.setPadding(0, 0, 3, 0);
            return view;
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
            Utility.changeFonts(view, getContext().getAssets(), getContext().getResources());
            return view;
        }
    }

    private class FetchDetailsTask extends AsyncTask<DepartingTrain, Integer, ServiceDetailsOrError> {
        private final TrainTableRow rowToUpdate;
        private final Station targetStation;

        private FetchDetailsTask(TrainTableRow rowToUpdate, Station station) {
            this.rowToUpdate = rowToUpdate;
            targetStation = station;
        }

        @Override
        protected ServiceDetailsOrError doInBackground(DepartingTrain... departingTrains) {
            final DepartingTrain train = departingTrains[0];
            try {
                return new ServiceDetailsOrError(train.serviceDetails());
            } catch (IOException ioe) {
                return new ServiceDetailsOrError(ioe.getMessage());
            }
        }
        @Override
        protected void onPostExecute(ServiceDetailsOrError serviceDetailsOrError) {
            if (serviceDetailsOrError.hasDetails()) {
                for (CallingPoint point: serviceDetailsOrError.details()) {
                    if (point.stationName().equals(targetStation.fullName()))
                        rowToUpdate.updateArrivalTime(point.scheduledTime());
                }
            }
        }

    }

    private void populateBoard(TableLayout table, DepartureBoard board) {
        Utility.changeFonts(table, getAssets(), getResources());
        final List<DepartingTrain> trains = ImmutableList.copyOf(board.departingTrains());
        if (trains.size() > 0) {
            table.removeAllViews();
            for (DepartingTrain train: board.departingTrains()) {
                final TrainTableRow row = new TrainTableRow(getBaseContext(), train);
                table.addView(row);
                if (board.hasToStation())
                    new FetchDetailsTask(row, board.toStation()).execute(train);
            }
        }
    }

    private static class ServiceDetailsOrError {
        private ServiceDetails serviceDetails;
        private String exceptionText;

        private ServiceDetailsOrError(ServiceDetails serviceDetails) {
            this.serviceDetails = serviceDetails;
        }
        
        private ServiceDetailsOrError(String exceptionText) {
            this.exceptionText = exceptionText;
            System.out.println(exceptionText);
        }
        
        private boolean hasDetails() {
            return serviceDetails != null;
        }
        
        private ServiceDetails details() {
            return serviceDetails;
        }
        
        @SuppressWarnings("UnusedDeclaration")
        private String errorMsg() {
            return exceptionText;
        }
    }
}
