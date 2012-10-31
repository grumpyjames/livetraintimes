package org.grumpysoft;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class State {
    public static void unsetStation() {
        if (STATION_STATE.toStation != null) {
            STATION_STATE.withToStation(null);
        } else {
            STATION_STATE.withFromStation(null);
        }
    }

    public static String currentStation() {
        // FIXME: almost certainly broken.
        return STATION_STATE.toStation != null ? STATION_STATE.toStation : STATION_STATE.fromStation;
    }

    public static CharSequence fromOrTo() {
        return STATION_STATE.fromStation == null ? "From.." : "From " + STATION_STATE.fromStation + "\n To..";
    }

    private static class StationState {
        String fromStation;
        String toStation;
        
        private StationState() {
            this.fromStation = null;
            this.toStation = null;            
        }
        
        private void withFromStation(String station) {
            this.fromStation = station;
        }
        
        private void withToStation(String station) {
            this.toStation = station;
        }
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

        @SuppressWarnings("UnusedDeclaration")
        private Exception error() {
            return error;
        }

        private DepartureBoard board() {
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
    
    private static final StationState STATION_STATE = new StationState();

    public static Intent selectStation(String station, Context context) {
        if (STATION_STATE.fromStation == null) {
            STATION_STATE.withFromStation(station);
            return new Intent(context, SelectorTabsActivity.class);
        } else {
            STATION_STATE.withToStation(station);
            final GetBoardsTask task = new GetBoardsTask(context);
            try {
                final BoardOrError board = task.execute(STATION_STATE.fromStation, STATION_STATE.toStation).get();
                if (board.hasBoard()) {
                    State.board = board.board(); // yuk!
                    return new Intent(context, ShowTrainsActivity.class);
                } else {
                   throw new RuntimeException("ffs");
                }
            } catch (InterruptedException e) {
                //FIXME: pass on the interrupt..
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static DepartureBoard board;         
}
