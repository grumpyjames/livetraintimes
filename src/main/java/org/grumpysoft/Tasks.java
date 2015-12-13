package org.grumpysoft;

import android.os.AsyncTask;
import net.digihippo.soap.SoapLiveTrainsService;

import java.io.Serializable;

public final class Tasks implements Serializable {
    private static final DepartureBoardService service = SoapLiveTrainsService.departureBoardService();

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

    private static class GetBoardsTask extends AsyncTask<NavigatorState, Integer, BoardOrError> {
        private final ShowTrainsActivity context;

        private GetBoardsTask(ShowTrainsActivity context) {
            this.context = context;
        }

        @Override
        protected BoardOrError doInBackground(NavigatorState... states) {
            try {
                final NavigatorState navigatorState = states[0];
                return new BoardOrError(
                        service.boardFor(
                                navigatorState.stationOne.get(),
                                navigatorState.stationTwo));
            } catch (Exception e) {
                return new BoardOrError(e);
            }
        }

        @Override
        protected void onPostExecute(BoardOrError boardOrError) {
            super.onPostExecute(boardOrError);
            context.onBoardOrError(boardOrError);
        }
    }
    
    public static void fetchTrains(ShowTrainsActivity context, NavigatorState navigatorState) {
        final GetBoardsTask task = new GetBoardsTask(context);
        task.execute(navigatorState);
    }

    private Tasks() {}
}
