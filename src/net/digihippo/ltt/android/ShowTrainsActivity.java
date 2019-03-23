package net.digihippo.ltt.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import net.digihippo.ltt.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import static net.digihippo.ltt.FastestTrain.fastestTrainIndex;

public class ShowTrainsActivity extends Activity {
    private static final String TRAIN = "train";
    private AlertDialog alertDialog;
    private NavigatorState navigatorState;
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

    void onBoardOrError(final Tasks.BoardOrError boardOrError) {
        if (alertDialog != null) {
            alertDialog.hide();
        }
        final TableLayout table = (TableLayout) findViewById(net.digihippo.ltt.R.id.board);

        if (boardOrError.hasBoard()) {
            populateBoard(table, boardOrError.board());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (boardOrError.isNetworkConnectivityPresent())
            {
                builder.setMessage(
                    "Failed to retrieve trains, even though this device has internet connectivity. " +
                        "Please send a bug report so this can be fixed :-)");
                builder.setNeutralButton("Send bug report", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Writer writer = new StringWriter();
                        //noinspection ThrowableResultOfMethodCallIgnored
                        boardOrError.error().printStackTrace(new PrintWriter(writer));
                        String exceptionText = writer.toString();

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "A live train times bug");
                        intent.putExtra(Intent.EXTRA_TEXT,
                            "Please investigate the below exception: \n" + exceptionText);
                        intent.setData(Uri.parse("mailto:james.byatt@digihippo.net"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ShowTrainsActivity.this.startActivity(intent);
                    }
                });
            }
            else
            {
                builder.setMessage("Unable to retrieve trains - are you connected to the internet?");
                builder.setNeutralButton("Retry", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        attemptToFetchTrains();
                    }
                });
            }
            builder.setCancelable(true);
            alertDialog = builder.create();
            // Avoid android.view.WindowManager$BadTokenException, according to
            // https://stackoverflow.com/questions/18662239
            if (!isFinishing())
            {
                alertDialog.show();
            }
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
                return "Fetching departures from " + navigatorState.stationOne.fullName() +
                        maybeTo(navigatorState.stationTwo);
        }
        throw new RuntimeException("We added a new type and forgot to add the view for it");
    }

    private String maybeTo(Station stationTwo) {
        if (stationTwo != null) {
            return " to " + stationTwo.fullName();
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    private void displayArrivalTime(final View rowToUpdate, final DepartingTrain train) {
        final Station endpoint =
                navigatorState.stationTwo == null || navigatorState.stationTwo == Anywhere.INSTANCE ?
                    train.destinationList().get(0) : navigatorState.stationTwo;
        final Date arrivalTime = train.findArrivalTimeAt(endpoint);
        final String arrivalTimeStr = simpleDateFormat.format(arrivalTime);

        if (arrivalTime != null) {
            train.getDepartureTime()
                    .consume(
                            this.<BadTrainState>noOp(),
                            new Consumer<String>() {
                                @Override
                                public void consume(String etd) {
                                    TextView arrivingAt =
                                        (TextView) rowToUpdate.findViewById(net.digihippo.ltt.R.id.arrivingAt);
                                    arrivingAt.setText(arrivalTimeStr);
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

    private void showFastestTrain(DepartureBoard board) {
        if (navigatorState.type == NavigatorState.Type.FastestTrain)
        {
            int index = fastestTrainIndex(navigatorState.stationTwo, board.departingTrains());
            final DepartingTrain best = board.departingTrains().get(index);
            alertDialog.hide();
            alertDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Fastest Train");
            String platformText = best.platform();
            builder.setMessage("The fastest train from " + navigatorState.stationOne.fullName()
                    + " to " + navigatorState.stationTwo.fullName()
                    + " leaves at " + best.getActualDepartureTime()
                    + platformText
                    + ". It is expected to arrive at "
                    + simpleDateFormat.format(best.findArrivalTimeAt(navigatorState.stationTwo)) + ".");
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
        table.setColumnShrinkable(0, false);
        table.setColumnStretchable(1, true);
        table.setColumnShrinkable(2, false);
        if (board.departingTrains().size() > 0) {
            table.removeAllViews();
            table.addView(View.inflate(this, net.digihippo.ltt.R.layout.board_header, null));

            findViewById(R.id.platform).setSelected(true);
            findViewById(R.id.expected_header).setSelected(true);
            findViewById(R.id.details).setSelected(true);


            for (final DepartingTrain train: board.departingTrains()) {
                showOneTrain(table, train);
            }
            showFastestTrain(board);
        }
    }

    private void showOneTrain(TableLayout table, final DepartingTrain train)
    {
        final View row = View.inflate(this, R.layout.board_entry, null);

        populateDepartureTime(train, row);
        ((TextView) row.findViewById(R.id.destination)).setText(train.destinationText());
        ((TextView) row.findViewById(R.id.platform)).setText(train.platform());
        attachShowDetailsListener(train, row);
        displayArrivalTime(row, train);

        table.addView(row);
    }

    private void attachShowDetailsListener(final DepartingTrain train, View row)
    {
        row.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(ShowTrainsActivity.this, ShowDetailsActivity.class);
                intent.putExtra(NavigatorActivity.NAVIGATOR_STATE, navigatorState);
                intent.putExtra(TRAIN, train);
                ShowTrainsActivity.this.startActivity(intent);
            }
        });
    }

    private void populateDepartureTime(final DepartingTrain train, View row)
    {
        final TextView due = (TextView) row.findViewById(R.id.due);
        final TextView comment = (TextView) row.findViewById(R.id.comment);
        final TextView alert = (TextView) row.findViewById(R.id.alert);
        train.getDepartureTime()
                .consume(
                        new Consumer<BadTrainState>() {
                            @Override
                            public void consume(BadTrainState badTrainState) {
                                alert.setText(badTrainState.name());
                                due.setText(train.getScheduledTime());

                                comment.setVisibility(View.GONE);
                            }
                        },
                        new Consumer<String>() {
                            @Override
                            public void consume(String expectedAt) {
                                if (train.onTime())
                                {
                                    due.setText(expectedAt);
                                    alert.setText("");
                                    alert.setBackgroundColor(Color.TRANSPARENT);
                                    // could set colour here?
                                    comment.setText("On time");
                                }
                                else
                                {
                                    due.setText(train.getScheduledTime());
                                    // could set colour here?
                                    comment.setText(expectedAt);
                                    alert.setText("");
                                    alert.setBackgroundColor(Color.TRANSPARENT);
                                }
                            }
                        }
                );
    }
}
