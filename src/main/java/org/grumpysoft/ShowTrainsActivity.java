package org.grumpysoft;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

        final NavigatorState navigatorState =
                (NavigatorState) getIntent().getExtras().get(NavigatorActivity.NAVIGATOR_STATE);
        assert navigatorState != null;
        final Tasks.BoardOrError board = Tasks.fetchTrains(this, navigatorState);
        final TableLayout table = (TableLayout) findViewById(R.id.board);

        if (board.hasBoard())
            populateBoard(table, board.board());
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    private class FetchDetailsTask extends AsyncTask<DepartingTrain, Integer, ServiceDetailsOrError> {
        private final TableRow rowToUpdate;
        private final Station targetStation;

        private FetchDetailsTask(TableRow rowToUpdate, Station station) {
            this.rowToUpdate = rowToUpdate;
            this.targetStation = station;
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
                    if (point.stationName().equals(targetStation.fullName())) {
                        TextView arrivingAt = (TextView) rowToUpdate.findViewById(R.id.arrivingAt);
                        arrivingAt.setText(point.scheduledTime());
                    }
                }
            }
        }

    }

    private void populateBoard(TableLayout table, DepartureBoard board) {
        Utility.changeFonts(table, getAssets(), getResources());
        final List<DepartingTrain> trains = ImmutableList.copyOf(board.departingTrains());
        table.setColumnShrinkable(0, false);
        table.setColumnStretchable(1, true);
        table.setColumnShrinkable(2, false);
        if (trains.size() > 0) {
            table.removeAllViews();
            for (DepartingTrain train: board.departingTrains()) {
                TableRow row = (TableRow) View.inflate(this, R.layout.board_entry, null);

                TextView due = (TextView) row.findViewById(R.id.due);
                due.setText(train.expectedAt());
                // FIXME: via
                TextView destination = (TextView) row.findViewById(R.id.destination);
                destination.setText(Joiner.on(" & ").join(train.destinationList()));

                TextView platform = (TextView) row.findViewById(R.id.platform);
                platform.setText(train.platform());

                table.addView(row);
                if (board.hasToStation())
                    new FetchDetailsTask(row, board.toStation()).execute(train);
                else
                    table.setColumnCollapsed(3, true);
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
