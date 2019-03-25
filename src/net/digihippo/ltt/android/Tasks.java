package net.digihippo.ltt.android;

import android.os.AsyncTask;
import net.digihippo.ltt.Anywhere;
import net.digihippo.ltt.DepartureBoard;
import net.digihippo.ltt.DepartureBoardService;
import net.digihippo.ltt.Station;
import net.digihippo.ltt.ldb.AndroidTrainService;
import net.digihippo.ltt.ldb.LdbLiveTrainsService;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

final class Tasks implements Serializable {
    private static final DepartureBoardService service = new LdbLiveTrainsService();

    public enum ErrorType
    {
        Connectivity,
        Programmer,
        UnexpectedResponse,
        Other;
    }

    static class BoardOrError {
        private final DepartureBoard board;
        private final Exception error;
        private final ErrorType errorType;

        private BoardOrError(DepartureBoard board) {
            this.board = board;
            this.error = null;
            this.errorType = null;
        }

        private BoardOrError(Exception exc, ErrorType errorType) {
            error = exc;
            this.errorType = errorType;
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

        ErrorType getErrorType()
        {
            return errorType;
        }
    }

    private static class GetBoardsTask extends AsyncTask<NavigatorState, Integer, BoardOrError> {
        private final BoardReceiver context;

        private GetBoardsTask(BoardReceiver context) {
            this.context = context;
        }

        @Override
        protected BoardOrError doInBackground(NavigatorState... states) {
            final NavigatorState navigatorState = states[0];
            try {
                return performRequest(navigatorState);
            } catch (IOException e) {
                return handleError(navigatorState, e);
            } catch (AndroidTrainService.ParseException pe) {
                return new BoardOrError(pe, ErrorType.Programmer);
            }
        }

        private BoardOrError performRequest(NavigatorState navigatorState) throws IOException
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
                new Exception("Failed to retrieve trains: " + navigatorState, e);
            // see if we can reach Google
            try
            {
                URL url = new URL( "https://www.google.com");
                final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("HEAD");
                if (urlConnection.getResponseCode() < 300)
                {
                    return new BoardOrError(withHelpfulMessage, ErrorType.Other);
                }

                return new BoardOrError(withHelpfulMessage, ErrorType.Connectivity);
            }
            catch (IOException ioe)
            {
                return new BoardOrError(withHelpfulMessage, ErrorType.Connectivity);
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
