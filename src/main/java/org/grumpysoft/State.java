package org.grumpysoft;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.TextView;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class State {
    State(TextView view) {
        this.stationState = new StationState(view);
    }

    public void unsetStation() {
        if (stationState.toStation != null) {
            stationState.withToStation(null);
        } else {
            stationState.withFromStation(null);
        }
    }

    public void launchShowTrainsActivity(Context context) {
        final Intent showTrainsIntent = new Intent(context, ShowTrainsActivity.class);
        showTrainsIntent.putExtra("state", stationState);
        context.startActivity(showTrainsIntent);
    }

    public boolean unwind() {
        return stationState.unwind();
    }

    public static class BoardOrError {
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

        public boolean hasBoard() {
            return board != null;
        }

        @SuppressWarnings("UnusedDeclaration")
        private Exception error() {
            return error;
        }

        public DepartureBoard board() {
            return board;
        }
    }

    static final LiveTrainsService service =
            DepartureBoards.nationalRailServer(new DefaultHttpClient(new ThreadSafeClientConnManager(new BasicHttpParams(), new DefaultHttpClient().getConnectionManager().getSchemeRegistry()), new BasicHttpParams()));

    private static class GetBoardsTask extends AsyncTask<String, Integer, BoardOrError> {
        
        final Context context;

        private GetBoardsTask(Context context) {
            this.context = context;
        }

        @Override
        protected BoardOrError doInBackground(String... strings) {
            try {
                final String fromStation = strings[0];
                final String toStation = strings[1];
                if (toStation.equals("Anywhere!"))
                    return new BoardOrError(service.boardFor(fromStation));
                else
                    return new BoardOrError(service.boardForJourney(fromStation, toStation));
            } catch (IOException e) {
                return new BoardOrError(e);
            }
        }

        @Override
        protected void onPostExecute(BoardOrError boardOrError) {
            super.onPostExecute(boardOrError);
        }
    }
    
    private final StationState stationState;

    public boolean selectStation(String station) {
        if (stationState.fromStation == null) {
            stationState.withFromStation(station);
            return false;
        } else {
            stationState.withToStation(station);
            return true;
        }
    }

    public static BoardOrError fetchTrains(Context context, StationState stationState) {
        final GetBoardsTask task = new GetBoardsTask(context);
        try {
            return task.execute(stationState.fromStation, stationState.toStation).get();
        } catch (InterruptedException e) {
            //FIXME: pass on the interrupt..
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
