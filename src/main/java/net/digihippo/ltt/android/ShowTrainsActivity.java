package net.digihippo.ltt.android;

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
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.digihippo.ltt.*;
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
        setContentView(net.digihippo.ltt.R.layout.show_board);

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
                .setIcon(net.digihippo.ltt.R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);


        return true;
    }

    public void onBoardOrError(Tasks.BoardOrError boardOrError) {
        if (alertDialog != null) {
            alertDialog.hide();
        }
        final TableLayout table = (TableLayout) findViewById(net.digihippo.ltt.R.id.board);

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

    private Optional<Station> filterAnywhere(Optional<Station> station) {
        if (station.isPresent() && station.get() == Anywhere.INSTANCE) {
            return Optional.absent();
        }
        return station;
    }

    private void onDetails(final View rowToUpdate, final DepartingTrain train) {
        final Station endpoint =
                filterAnywhere(navigatorState.stationTwo).or(train.destinationList().get(0));
        FindArrivalTime callingPointConsumer = new FindArrivalTime(endpoint);
        for (final CallingPoint point: train.serviceDetails()) {
            callingPointConsumer.onSinglePoint(point.locationName, point.et);
        }

        final LocalTime now = LocalTime.now();
        final String arrivalTime = callingPointConsumer.getArrivalTime();
        if (arrivalTime != null) {
            train.getDepartureTime()
                    .consume(
                            this.<BadTrainState>noOp(),
                            new Consumer<String>() {
                                @Override
                                public void consume(String etd) {
                                    TextView arrivingAt = (TextView) rowToUpdate.findViewById(net.digihippo.ltt.R.id.arrivingAt);
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
                    );
        }
    }

    private <T> Consumer<T> noOp() {
        return new Consumer<T>() {
            @Override
            public void consume(T t) {

            }
        };
    }

    private void showFastestTrain() {
        if (navigatorState.type == NavigatorState.Type.FastestTrain)
        {
            alertDialog.hide();
            alertDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fastest Train");
            TextView dueView = (TextView) currentBestTrain.rowToUpdate.findViewById(net.digihippo.ltt.R.id.due);
            TextView platformView = (TextView) currentBestTrain.rowToUpdate.findViewById(net.digihippo.ltt.R.id.platform);
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
        if (trains.size() > 0) {
            table.removeAllViews();
            table.addView(View.inflate(this, net.digihippo.ltt.R.layout.board_header, null));

            for (final DepartingTrain train: board.departingTrains()) {
                final View row = View.inflate(this, net.digihippo.ltt.R.layout.board_entry, null);

                final TextView due = (TextView) row.findViewById(net.digihippo.ltt.R.id.due);
                final TextView alert = (TextView) row.findViewById(net.digihippo.ltt.R.id.alert);
                train.getDepartureTime()
                        .consume(
                                new Consumer<BadTrainState>() {
                                    @Override
                                    public void consume(BadTrainState badTrainState) {
                                        alert.setText(badTrainState.name());
                                        due.setText("--:--");
                                    }
                                },
                                new Consumer<String>() {
                                    @Override
                                    public void consume(String expectedAt) {
                                        due.setText(expectedAt);
                                        alert.setText("");
                                        alert.setBackgroundColor(Color.TRANSPARENT);
                                    }
                                }
                        );

                TextView destinationView = (TextView) row.findViewById(net.digihippo.ltt.R.id.destination);
                destinationView.setText(destinationText(train));

                TextView platform = (TextView) row.findViewById(net.digihippo.ltt.R.id.platform);
                platform.setText(train.platform());

                table.addView(row);
                row.findViewById(net.digihippo.ltt.R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(ShowTrainsActivity.this, ShowDetailsActivity.class);
                        intent.putExtra(NavigatorActivity.NAVIGATOR_STATE, navigatorState);
                        intent.putExtra(TRAIN, train);
                        ShowTrainsActivity.this.startActivity(intent);
                    }
                });

                onDetails(row, train);
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

        public void onSinglePoint(String stationName, Either<BadTrainState, String> scheduledAtTime) {
            if (arrivalTime == null && stationName.equals(soughtStation.fullName())) {
                scheduledAtTime.consume(new Consumer<BadTrainState>() {
                    @Override
                    public void consume(BadTrainState badTrainState) {

                    }
                }, new Consumer<String>() {
                    @Override
                    public void consume(String arrival) {
                        arrivalTime = arrival;
                    }
                });
            }
        }

        public String getArrivalTime() {
            return arrivalTime;
        }
    }

    private String destinationText(final DepartingTrain departingTrain) {
        List<String> endPoints =
                Lists.transform(departingTrain.destinationList(), new Function<Station, String>() {
                    @Override
                    public String apply(Station station) {
                        return station.fullName();
                    }
                });
        List<String> via = departingTrain.viaDestinations();
        String destinationText = "";
        boolean addComma = false;
        for (int i = 0; i < endPoints.size(); i++) {
            if (addComma) {
                destinationText += ", ";
            } else {
                addComma = true;
            }

            if (i < via.size()) {
                destinationText += (endPoints.get(i) + " " + via.get(i).trim());
            } else {
                destinationText += endPoints.get(i);
            }
        }

        if (departingTrain.isCircularRoute()) {
            destinationText += " (circular route)";
        }

        return destinationText;
    }
}
