package net.digihippo.ltt.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import net.digihippo.ltt.*;

public class NavigatorActivity extends Activity implements FavouriteListener {
    private static final String CHOICE_ID = "choiceId";
    static final String NAVIGATOR_STATE = "navigatorState";
    private static final String CHOICE_ONE = "choiceOne";
    private static final String CHOICE_TWO = "choiceTwo";

    private final ImmutableSet<Integer> typeIds =
            ImmutableSet.of(R.id.departures, R.id.fastest);

    private NavigatorState navigatorState = new NavigatorState();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Object existingState = extras.get(NAVIGATOR_STATE);
            if (existingState != null) {
                navigatorState = (NavigatorState) existingState;
                String choiceId = (String) extras.get("choiceId");
                String station = (String) extras.get("station");

                if (choiceId != null && station != null) {
                    if (choiceId.equals(CHOICE_ONE)) {
                        navigatorState.stationOne = toMaybeStation(station);
                    }
                    if (choiceId.equals(CHOICE_TWO)) {
                        navigatorState.stationTwo = toMaybeStation(station);
                    }
                }
            }
        }

        setContentView(R.layout.navigator);
        attachButtonListeners();
        render();
    }


    private void switchDirection() {
        navigatorState.switchDirection();
        render();
    }

    private Optional<Station> toMaybeStation(String station) {
        if (station.equals(Anywhere.INSTANCE.fullName())) {
            return Optional.<Station>of(Anywhere.INSTANCE);
        }

        return Optional.of(Stations.reverseLookup(station));
    }

    private void render() {
        switch (navigatorState.type) {
            case Departing:
                renderDeparting(navigatorState.stationOne, navigatorState.stationTwo);
                break;
            case FastestTrain:
                renderFastestTrain(navigatorState.stationOne, navigatorState.stationTwo);
                break;
        }
    }

    private void attachButtonListeners() {
        findViewById(R.id.departures).setOnClickListener(new ChangeTypeListener(NavigatorState.Type.Departing));
        findViewById(R.id.fastest).setOnClickListener(new ChangeTypeListener(NavigatorState.Type.FastestTrain));
        findViewById(R.id.choice_one).findViewById(R.id.select_action).setOnClickListener(new SelectStationListener(CHOICE_ONE));
        findViewById(R.id.choice_two).findViewById(R.id.select_action).setOnClickListener(new SelectStationListener(CHOICE_TWO));
        findViewById(R.id.reverse_direction).setOnClickListener(new ReverseDirectionListener());
        findViewById(R.id.go).setOnClickListener(new ShowTrainsListener());
    }

    private void switchTypeTo(NavigatorState.Type type) {
        navigatorState.type = type;
        render();
    }

    private void renderFastestTrain(Optional<Station> stationOne, Optional<Station> stationTwo) {
        setSelectedType(R.id.fastest);

        renderCommon(stationOne, stationTwo, "From: ", "To: ");
        if (present(stationOne) && present(stationTwo)) {
            readyToGo();
        } else {
            error("Please select a 'from' and a 'to' station");
        }
    }

    private void renderDeparting(Optional<Station> stationOne, Optional<Station> stationTwo) {
        setSelectedType(R.id.departures);

        renderCommon(stationOne, stationTwo, "From: ", "To: ");

        if (present(stationOne)) {
            readyToGo();
        } else {
            error("Please select a 'from' station");
        }
    }

    private boolean present(Optional<Station> stationOne) {
        return stationOne.isPresent() && !stationOne.get().equals(Anywhere.INSTANCE);
    }

    private void setSelectedType(int id) {
        for (int typeId : typeIds) {
            if (typeId != id)
            {
                ((TextView) findViewById(typeId)).setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            }
        }
        TextView selected = (TextView) findViewById(id);
        selected.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
    }

    private void renderCommon(
            Optional<Station> stationOne,
            Optional<Station> stationTwo,
            String labelOneText,
            String labelTwoText) {
        Station firstStation = stationOne.or(Anywhere.INSTANCE);
        populateStationChoice(firstStation, findViewById(R.id.choice_one), labelOneText);

        Station secondStation = stationTwo.or(Anywhere.INSTANCE);
        populateStationChoice(secondStation, findViewById(R.id.choice_two), labelTwoText);
    }

    private void readyToGo() {
        TextView errorView = (TextView) findViewById(R.id.error);
        errorView.setText("");

        findViewById(R.id.go).setEnabled(true);
    }

    private void error(String errorText) {
        TextView errorView = (TextView) findViewById(R.id.error);
        errorView.setText(errorText);

        findViewById(R.id.go).setEnabled(false);
    }

    private void populateStationChoice(Station fromStation, View stationView, String labelText) {
        TextView label = (TextView) stationView.findViewById(R.id.label);
        label.setText(labelText);
        StationView.initialiseStationView(stationView, fromStation, new NoOpClickListener(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Favourites.save(getPreferences(0));
    }

    @Override
    public void favouriteAdded(Station station) {
        Favourites.getFavourites().add(station);
    }

    @Override
    public void favouriteRemoved(Station station) {
        Favourites.getFavourites().remove(station);
    }

    private static class NoOpClickListener implements View.OnClickListener {
        @Override public void onClick(View view) {}
    }

    private class ChangeTypeListener implements View.OnClickListener {
        private final NavigatorState.Type type;

        public ChangeTypeListener(NavigatorState.Type type) {
            this.type = type;
        }

        @Override
        public void onClick(View view) {
            NavigatorActivity.this.switchTypeTo(type);
        }
    }

    private class SelectStationListener implements View.OnClickListener {
        private final String choiceId;

        public SelectStationListener(String choiceId) {
            this.choiceId = choiceId;
        }

        @Override
        public void onClick(View view) {
            final Intent selectStationIntent =
                    new Intent(NavigatorActivity.this, ViewFlippingActivity.class);
            selectStationIntent.putExtra(CHOICE_ID, choiceId);
            selectStationIntent.putExtra(NAVIGATOR_STATE, NavigatorActivity.this.navigatorState);
            startActivity(selectStationIntent);
        }
    }

    private class ShowTrainsListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(NavigatorActivity.this, ShowTrainsActivity.class);
            intent.putExtra(NAVIGATOR_STATE, navigatorState);

            startActivity(intent);
        }
    }

    private class ReverseDirectionListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            NavigatorActivity.this.switchDirection();
        }
    }
}
