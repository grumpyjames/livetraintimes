package org.grumpysoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class ShowTrainsActivity extends Activity {

    private AlertDialog alertDialog;
    private NavigatorState navigatorState;
    private ProgressBar progressBar;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    private CurrentBestTrain currentBestTrain;

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

    public void onDetails(TableRow rowToUpdate, Tasks.ServiceDetailsOrError serviceDetailsOrError) {
        if (serviceDetailsOrError.hasDetails()) {
            String arrivalTime = null;
            ServiceDetails details = serviceDetailsOrError.details();
            for (CallingPoint point: details) {
                if (point.stationName().equals(navigatorState.stationTwo.get().fullName())) {
                    arrivalTime = point.scheduledTime();
                    TextView arrivingAt = (TextView) rowToUpdate.findViewById(R.id.arrivingAt);
                    arrivingAt.setText(arrivalTime);
                }
            }

            LocalTime now = LocalTime.now();
            if (arrivalTime != null) {
                LocalTime localTime = DATE_TIME_FORMATTER.parseLocalTime(arrivalTime);
                final DateTime arrivalDateTime;
                if (localTime.isBefore(now)) {
                    arrivalDateTime = localTime.toDateTimeToday().plusDays(1);
                } else {
                    arrivalDateTime = localTime.toDateTimeToday();
                }

                if (currentBestTrain == null || currentBestTrain.arrivesAfter(arrivalDateTime)) {
                    currentBestTrain = new CurrentBestTrain(rowToUpdate, details, arrivalDateTime);
                }
            }

            if (navigatorState.type == NavigatorState.Type.FastestTrain)
            {
                progressBar.incrementProgressBy(1);
                if (progressBar.getProgress() == progressBar.getMax()) {
                    alertDialog.hide();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Fastest Train");
                    TextView textView = new TextView(this);
                    TextView dueView = (TextView) currentBestTrain.rowToUpdate.getVirtualChildAt(0);
                    TextView platformView = (TextView) currentBestTrain.rowToUpdate.getVirtualChildAt(2);
                    String platformText = (platformView.getText().length() > 0)
                            ? " from platform " + platformView.getText()
                            : "";
                    builder.setMessage("The fastest train from " + navigatorState.stationOne.get().fullName()
                            + " to " + navigatorState.stationTwo.get().fullName()
                            + " leaves at " + dueView.getText()
                            + platformText
                            + ". It is expected to arrive at "
                            + DATE_TIME_FORMATTER.print(currentBestTrain.arrivalDateTime.toLocalTime()) + ".");
                    builder.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.hide();
                        }
                    });
                    this.alertDialog = builder.create();
                    alertDialog.show();
                }
            }
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
            table.addView(View.inflate(this, R.layout.board_header, null));

            for (DepartingTrain train: board.departingTrains()) {
                TableRow row = (TableRow) View.inflate(this, R.layout.board_entry, null);

                TextView due = (TextView) row.findViewById(R.id.due);
                due.setText(train.expectedAt());
                final String circularPart = train.isCircularRoute() ? " (circular route)" : "";
                final String viaPart = train.viaDestinations().isEmpty()
                                ? "" : " " + Joiner.on(" & ").join(train.viaDestinations());

                final String destination =
                        Joiner.on(" & ").join(train.destinationList()) + viaPart + circularPart;

                TextView destinationView = (TextView) row.findViewById(R.id.destination);
                destinationView.setText(destination);


                TextView platform = (TextView) row.findViewById(R.id.platform);
                platform.setText(train.platform());

                table.addView(row);
                if (stationTwoSpecified)
                    new Tasks.FetchDetailsTask(this, row).execute(train);
            }
        }
    }

    private class CurrentBestTrain {
        private final TableRow rowToUpdate;
        private final ServiceDetails details;
        private final DateTime arrivalDateTime;

        public CurrentBestTrain(TableRow rowToUpdate, ServiceDetails details, DateTime arrivalDateTime) {
            this.rowToUpdate = rowToUpdate;
            this.details = details;
            this.arrivalDateTime = arrivalDateTime;
        }

        public boolean arrivesAfter(DateTime arrivalDateTime) {
            return arrivalDateTime.isBefore(this.arrivalDateTime);
        }
    }
}
