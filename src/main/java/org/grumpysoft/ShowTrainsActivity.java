package org.grumpysoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

public class ShowTrainsActivity extends Activity {

    private AlertDialog alertDialog;
    private NavigatorState navigatorState;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_board);

        final NavigatorState navigatorState =
                (NavigatorState) getIntent().getExtras().get(NavigatorActivity.NAVIGATOR_STATE);
        assert navigatorState != null;

        this.navigatorState = navigatorState;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message(navigatorState));
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        builder.setView(progressBar);

        alertDialog = builder.create();
        alertDialog.show();

        Tasks.fetchTrains(this, navigatorState);
    }

    private String message(NavigatorState navigatorState) {
        switch (navigatorState.type) {
            case Arriving:
                return "Fetching arrivals at " + navigatorState.stationOne.get().fullName() +
                            maybe(" from ", navigatorState.stationTwo);
            case FastestTrain:
            case Departing:
                return "Fetching departures from " + navigatorState.stationOne.get().fullName() +
                            maybe(" to ", navigatorState.stationTwo);
        }
        throw new RuntimeException("We added a new type and forgot to add the view for it");
    }

    private String maybe(String message, Optional<Station> stationTwo) {
        if (stationTwo.isPresent()) {
            return message + stationTwo.get().fullName();
        }
        return "";
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    public void onBoardOrError(Tasks.BoardOrError boardOrError) {
        if (alertDialog != null) {
            alertDialog.hide();
        }
        final TableLayout table = (TableLayout) findViewById(R.id.board);

        // FIXME: handle the error case
        if (boardOrError.hasBoard()) {
            if (navigatorState.type == NavigatorState.Type.FastestTrain) {
                showFastestTrainDialog(boardOrError.board());
            }

            populateBoard(table, boardOrError.board());
        }
    }

    private void showFastestTrainDialog(DepartureBoard board) {
        List<? extends DepartingTrain> departingTrains = ImmutableList.copyOf(board.departingTrains());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Fetching calling points to ascertain fastest train");
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(departingTrains.size());
        progressBar.setProgress(0);
        builder.setView(progressBar);

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void onDetails(TableRow rowToUpdate, ServiceDetailsOrError serviceDetailsOrError) {
        if (serviceDetailsOrError.hasDetails()) {
            for (CallingPoint point: serviceDetailsOrError.details()) {
                if (point.stationName().equals(navigatorState.stationTwo.get().fullName())) {
                    TextView arrivingAt = (TextView) rowToUpdate.findViewById(R.id.arrivingAt);
                    arrivingAt.setText(point.scheduledTime());
                }
            }

            if (navigatorState.type == NavigatorState.Type.FastestTrain)
            {
                progressBar.incrementProgressBy(1);
                if (progressBar.getProgress() == progressBar.getMax()) {
                    alertDialog.hide();
                }
            }
        }
    }

    private static class FetchDetailsTask extends AsyncTask<DepartingTrain, Integer, ServiceDetailsOrError> {
        private ShowTrainsActivity showTrainsActivity;
        private final TableRow rowToUpdate;
        private final Station targetStation;

        private FetchDetailsTask(ShowTrainsActivity showTrainsActivity, TableRow rowToUpdate, Station station) {
            this.showTrainsActivity = showTrainsActivity;
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
            showTrainsActivity.onDetails(rowToUpdate, serviceDetailsOrError);
        }
    }

    private void populateBoard(TableLayout table, DepartureBoard board) {
        final List<DepartingTrain> trains = ImmutableList.copyOf(board.departingTrains());
        table.setColumnShrinkable(0, false);
        table.setColumnStretchable(1, true);
        table.setColumnShrinkable(2, false);
        boolean stationTwoSpecified = navigatorState.stationTwo.isPresent();
        if (!stationTwoSpecified) {
            table.setColumnCollapsed(3, true);
        }
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
                if (stationTwoSpecified)
                    new FetchDetailsTask(this, row, navigatorState.stationTwo.get()).execute(train);
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
