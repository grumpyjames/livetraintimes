package org.grumpysoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
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

    private static final String TRAIN = "train";
    private AlertDialog alertDialog;
    private NavigatorState navigatorState;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm");
    private CurrentBestTrain currentBestTrain;
    private boolean fetchingTrains = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_board);

        final NavigatorState navigatorState =
                (NavigatorState) getIntent().getExtras().get(NavigatorActivity.NAVIGATOR_STATE);
        assert navigatorState != null;

        this.navigatorState = navigatorState;

        attemptToFetchTrains();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        attemptToFetchTrains();
                        return true;
                    }
                })
                .setIcon(R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        return true;
    }

    public void onBoardOrError(Tasks.BoardOrError boardOrError) {
        if (alertDialog != null) {
            alertDialog.hide();
        }
        final TableLayout table = (TableLayout) findViewById(R.id.board);

        if (boardOrError.hasBoard()) {
            populateBoard(table, boardOrError.board());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unable to retrieve trains - are you connected to the internet?");
            builder.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    attemptToFetchTrains();
                }
            });
            builder.setCancelable(true);
            alertDialog = builder.create();
            alertDialog.show();
        }

        fetchingTrains = false;
    }

    private void attemptToFetchTrains() {
        if (fetchingTrains) {
            return;
        }

        fetchingTrains = true;
        if (alertDialog != null)
        {
            alertDialog.hide();
        }

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

    private void onDetails(final View rowToUpdate, ServiceDetails details) {
        FindArrivalTime callingPointConsumer =
                new FindArrivalTime(navigatorState.stationTwo.get());
        for (final CallingPoint point: details) {
            callingPointConsumer.onSinglePoint(point.locationName, point.scheduledAtTime);
        }

        LocalTime now = LocalTime.now();
        String arrivalTime = callingPointConsumer.getArrivalTime();
        if (arrivalTime != null) {
            TextView arrivingAt = (TextView) rowToUpdate.findViewById(R.id.arrivingAt);
            arrivingAt.setText(arrivalTime);

            LocalTime localTime = DATE_TIME_FORMATTER.parseLocalTime(arrivalTime);
            final DateTime arrivalDateTime;
            if (localTime.isBefore(now)) {
                arrivalDateTime = localTime.toDateTimeToday().plusDays(1);
            } else {
                arrivalDateTime = localTime.toDateTimeToday();
            }

            if (currentBestTrain == null || currentBestTrain.arrivesAfter(arrivalDateTime)) {
                currentBestTrain = new CurrentBestTrain(rowToUpdate, arrivalDateTime);
            }
        }
    }

    private void showFastestTrain() {
        if (navigatorState.type == NavigatorState.Type.FastestTrain)
        {
            alertDialog.hide();
            alertDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fastest Train");
            TextView dueView = (TextView) currentBestTrain.rowToUpdate.findViewById(R.id.due);
            TextView platformView = (TextView) currentBestTrain.rowToUpdate.findViewById(R.id.platform);
            String platformText = (platformView.getText().length() > 0)
                    ? " from platform " + platformView.getText()
                    : "";
            builder.setMessage("The fastest train from " + navigatorState.stationOne.get().fullName()
                    + " to " + navigatorState.stationTwo.get().fullName()
                    + " leaves at " + dueView.getText()
                    + platformText
                    + ". It is expected to arrive at "
                    + DATE_TIME_FORMATTER.print(currentBestTrain.arrivalDateTime.toLocalTime()) + ".");
            builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    alertDialog.hide();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
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

            for (final DepartingTrain train: board.departingTrains()) {
                View row = View.inflate(this, R.layout.board_entry, null);


                String text = train.expectedAt();
                TextView due = (TextView) row.findViewById(R.id.due);
                TextView alert = (TextView) row.findViewById(R.id.alert);
                if (text.equals("Cancelled") || text.equals("Delayed")) {
                    alert.setText(text);
                    due.setText("--:--");
                }
                else {
                    due.setText(text);
                    alert.setText("");
                    alert.setBackgroundColor(Color.TRANSPARENT);
                }

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
                row.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(ShowTrainsActivity.this, ShowDetailsActivity.class);
                        intent.putExtra(NavigatorActivity.NAVIGATOR_STATE, navigatorState);
                        intent.putExtra(TRAIN, train);
                        ShowTrainsActivity.this.startActivity(intent);
                    }
                });
                if (stationTwoSpecified)
                    onDetails(row, train.serviceDetails());
            }
            showFastestTrain();
        }
    }

    private class CurrentBestTrain {
        private final View rowToUpdate;
        private final DateTime arrivalDateTime;

        public CurrentBestTrain(View rowToUpdate, DateTime arrivalDateTime) {
            this.rowToUpdate = rowToUpdate;
            this.arrivalDateTime = arrivalDateTime;
        }

        public boolean arrivesAfter(DateTime arrivalDateTime) {
            return arrivalDateTime.isBefore(this.arrivalDateTime);
        }
    }

    private static class FindArrivalTime {
        private final Station soughtStation;

        private String arrivalTime;

        public FindArrivalTime(Station soughtStation) {
            this.soughtStation = soughtStation;
        }

        public void onSinglePoint(String stationName, String scheduledAtTime) {
            if (arrivalTime == null && stationName.equals(soughtStation.fullName())) {
                arrivalTime = scheduledAtTime;
            }
        }

        public String getArrivalTime() {
            return arrivalTime;
        }
    }
}
