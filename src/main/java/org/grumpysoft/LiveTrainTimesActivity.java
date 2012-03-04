package org.grumpysoft;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import java.io.IOException;

public class LiveTrainTimesActivity extends Activity
{
    final LiveTrainsService service =
            DepartureBoards.nationalRailServer(new DefaultHttpClient(new ThreadSafeClientConnManager(new BasicHttpParams(), new DefaultHttpClient().getConnectionManager().getSchemeRegistry()), new BasicHttpParams()));

    public static final String type = "TYPE";
    public static final String currentSelection = "CURRENT";

    private final String from = "FROM";
    private final String to = "TO";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)        
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Utility.changeFonts((ViewGroup) findViewById(R.id.scroll), getAssets());

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reviveCurrentStations(extras);
            if (extras.containsKey(type))
                setStation(extras.getInt(type), (String) extras.get("STATION"));            
        }
        addNecessaryResetter(R.id.from, R.id.from_clear);
        addNecessaryResetter(R.id.to, R.id.to_clear);
        
        
        final Button fromSelectButton = (Button) findViewById(R.id.from_select);
        setupSelectorButton(fromSelectButton, R.id.from);
        final Button toSelectButton = (Button) findViewById(R.id.to_select);
        setupSelectorButton(toSelectButton, R.id.to);


        final Button submitButton = (Button) findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptBoardFetch();
            }
        });
    }

    private void addNecessaryResetter(int textViewId, int clearButtonId) {
        final TextView textView = (TextView) findViewById(textViewId);
        final View clearButton = findViewById(clearButtonId);
        final String anywhere = getResources().getString(R.string.anywhere);
        if (!textView.getText().equals(anywhere)) {
            clearButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    textView.setText(anywhere);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            clearButton.setVisibility(View.VISIBLE);
        } else {
            clearButton.setVisibility(View.INVISIBLE);
        }        
    }

    private void reviveCurrentStations(Bundle extras) {
        setStation(R.id.from, (String)extras.get(from));
        setStation(R.id.to, (String)extras.get(to));
    }

    private void setStation(int type, String station) {
        final TextView view = (TextView) findViewById(type);
        view.setText(station);
    }

    private void setupSelectorButton(Button fromSelectButton, final int id) {
        fromSelectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {                
                final Intent intent = new Intent(getBaseContext(), SelectorTabsActivity.class);
                saveCurrentStations(intent);
                intent.putExtra(type, id);
                intent.putExtra(currentSelection, getTextView(id).getText());
                startActivity(intent);
            }
        });
    }

    private TextView getTextView(int id) {
        return (TextView) findViewById(id);
    }

    private void saveCurrentStations(Intent intent) {
        intent.putExtra(from, fromStation());
        intent.putExtra(to, toStation());
    }
    
    private static class BoardOrError {
        private final DepartureBoard board;
        private final Exception error;

        private BoardOrError(DepartureBoard board) {
            this.board = board;
            this.error = null;
        }
        
        private BoardOrError(Exception exc) {
            error = exc;
            board = null;
        }
        
        private boolean hasBoard() {
            return board != null;   
        }
        
        private Exception error() {
            return error;
        }
        
        private DepartureBoard board() {
            return board;
        }                  
    }

    private class GetBoardsTask extends AsyncTask<String, Integer, BoardOrError> {

        @Override
        protected BoardOrError doInBackground(String... strings) {
            try {
                final String fromStation = strings[0];
                final String toStation = strings[1];
                if (toStation.equals(getString(R.string.anywhere)))
                    return new BoardOrError(service.boardFor(fromStation));
                else                    
                    return new BoardOrError(service.boardForJourney(fromStation, toStation));
            } catch (IOException e) {
                return new BoardOrError(e);
            }
        }

        @Override
        protected void onPostExecute(BoardOrError boardOrError) {
            final TextView textView = (TextView) findViewById(R.id.output);
            if (boardOrError.hasBoard())
                showDepartureBoard(boardOrError);
            else
                showError(boardOrError, textView);
            super.onPostExecute(boardOrError);
        }

        private void showError(BoardOrError boardOrError, TextView textView) {
            textView.setText("Problemo: " + boardOrError.error().getMessage());
        }

        private void showDepartureBoard(BoardOrError boardOrError) {
            State.board = boardOrError.board(); // yuk!
            final Intent intent = new Intent(getBaseContext(), ShowTrainsActivity.class);
            startActivity(intent);
        }
    }

    private void attemptBoardFetch() {
        new GetBoardsTask().execute(fromStation(), toStation());
    }

    private String fromStation() {
        return textOf(R.id.from);
    }

    private String toStation() {
        return textOf(R.id.to);
    }

    private String textOf(int to) {
        return ((TextView) findViewById(to)).getText().toString();
    }
}
