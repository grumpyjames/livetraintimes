package net.digihippo.ltt.android;

import android.os.AsyncTask;
import net.digihippo.ltt.Anywhere;
import net.digihippo.ltt.DepartureBoard;
import net.digihippo.ltt.DepartureBoardService;
import net.digihippo.ltt.Station;
import net.digihippo.ltt.ldb.LdbLiveTrainsService;
import org.joda.time.Instant;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

final class Tasks implements Serializable {
    private static final DepartureBoardService service = new LdbLiveTrainsService();

    static class BoardOrError {
        private final DepartureBoard board;
        private final Exception error;
        private final boolean networkConnectivityPresent;

        private BoardOrError(DepartureBoard board) {
            this.board = board;
            this.error = null;
            this.networkConnectivityPresent = true;
        }

        private BoardOrError(Exception exc, boolean networkConnectivityPresent) {
            error = exc;
            this.networkConnectivityPresent = networkConnectivityPresent;
            board = null;
        }

        boolean hasBoard() {
            return board != null;
        }

        Exception error() {
            return error;
        }

        DepartureBoard board() {
            return board;
        }

        boolean isNetworkConnectivityPresent()
        {
            return networkConnectivityPresent;
        }
    }

    private static class GetBoardsTask extends AsyncTask<NavigatorState, Integer, BoardOrError> {
        private final ShowTrainsActivity context;

        private GetBoardsTask(ShowTrainsActivity context) {
            this.context = context;
        }

        @Override
        protected BoardOrError doInBackground(NavigatorState... states) {
            final NavigatorState navigatorState = states[0];
            try {
                return performRequest(navigatorState);
            } catch (Exception e) {
                if (e.getMessage().contains("Trust anchor"))
                {
                    service.httpsIsBroken();
                    // don't recurse in case, magically, the http version throws the same error.
                    try
                    {
                        return performRequest(navigatorState);
                    } catch (Exception again)
                    {
                        return handleError(navigatorState, again);
                    }
                }

                return handleError(navigatorState, e);
            }
        }

        private BoardOrError performRequest(NavigatorState navigatorState) throws Exception
        {
            final Station from = navigatorState.stationOne;

            if (navigatorState.stationTwo != null &&
                navigatorState.stationTwo != Anywhere.INSTANCE)
            {
                return new BoardOrError(service.boardFor(from, navigatorState.stationTwo));
            }
            else
            {
                return new BoardOrError(service.boardFor(from));
            }
        }

        private BoardOrError handleError(NavigatorState navigatorState, Exception e)
        {
            Exception withHelpfulMessage =
                new Exception("At " + Instant.now() + ", failed to retrieve trains: " + navigatorState, e);
            // see if we can reach Google's public DNS
            Socket sock = new Socket();
            try
            {
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return new BoardOrError(withHelpfulMessage, true);
            }
            catch (IOException ioe)
            {
                return new BoardOrError(withHelpfulMessage, false);
            }
        }

        @Override
        protected void onPostExecute(BoardOrError boardOrError) {
            super.onPostExecute(boardOrError);
            context.onBoardOrError(boardOrError);
        }
    }
    
    static void fetchTrains(ShowTrainsActivity context, NavigatorState navigatorState) {
        final GetBoardsTask task = new GetBoardsTask(context);
        task.execute(navigatorState);
    }

    private Tasks() {}
}
