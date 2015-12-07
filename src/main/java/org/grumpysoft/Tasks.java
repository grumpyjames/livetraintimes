package org.grumpysoft;

import android.content.Context;
import android.os.AsyncTask;
import net.digihippo.soap.WWHLDBServiceSoap;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public final class Tasks implements Serializable {
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
            WWHLDBServiceSoap.liveTrainsService();

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
                if (toStation.equals("ANY"))
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
    
    public static BoardOrError fetchTrains(Context context, NavigatorState navigatorState) {
        final GetBoardsTask task = new GetBoardsTask(context);
        try {
            final String stationOne = navigatorState.stationOne.get().threeLetterCode();
            final String stationTwo = navigatorState.stationTwo.or(Anywhere.INSTANCE).threeLetterCode();
            return task.execute(stationOne, stationTwo).get();
        } catch (InterruptedException e) {
            //FIXME: pass on the interrupt..
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Tasks() {}
}
