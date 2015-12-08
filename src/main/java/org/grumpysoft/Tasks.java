package org.grumpysoft;

import android.os.AsyncTask;
import android.widget.TableRow;
import net.digihippo.soap.WWHLDBServiceSoap;

import java.io.IOException;
import java.io.Serializable;

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

    private static class GetBoardsTask extends AsyncTask<NavigatorState, Integer, BoardOrError> {
        private final ShowTrainsActivity context;

        private GetBoardsTask(ShowTrainsActivity context) {
            this.context = context;
        }

        @Override
        protected BoardOrError doInBackground(NavigatorState... states) {
            try {
                final NavigatorState navigatorState = states[0];
                if (!navigatorState.stationTwo.isPresent())
                    return new BoardOrError(
                            service.boardFor(
                                    navigatorState.stationOne.get().threeLetterCode()));
                else
                    return new BoardOrError(
                            service.boardForJourney(
                                    navigatorState.stationOne.get().threeLetterCode(),
                                    navigatorState.stationTwo.get().threeLetterCode()
                            ));
            } catch (IOException e) {
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

    public static class FetchDetailsTask extends AsyncTask<DepartingTrain, Integer, ServiceDetailsOrError> {
        private ShowTrainsActivity showTrainsActivity;
        private final TableRow rowToUpdate;

        public FetchDetailsTask(ShowTrainsActivity showTrainsActivity, TableRow rowToUpdate) {
            this.showTrainsActivity = showTrainsActivity;
            this.rowToUpdate = rowToUpdate;
        }

        @Override
        protected ServiceDetailsOrError doInBackground(DepartingTrain... departingTrains) {
            final DepartingTrain train = departingTrains[0];
            try {
                return new ServiceDetailsOrError(train.serviceDetails());
            } catch (IOException ioe) {
                return new ServiceDetailsOrError(ioe.getMessage());
            }
        }

        @Override
        protected void onPostExecute(ServiceDetailsOrError serviceDetailsOrError) {
            super.onPostExecute(serviceDetailsOrError);
            showTrainsActivity.onDetails(rowToUpdate, serviceDetailsOrError);
        }
    }

    static class ServiceDetailsOrError {
        private ServiceDetails serviceDetails;
        private String exceptionText;

        private ServiceDetailsOrError(ServiceDetails serviceDetails) {
            this.serviceDetails = serviceDetails;
        }

        private ServiceDetailsOrError(String exceptionText) {
            this.exceptionText = exceptionText;
            System.out.println(exceptionText);
        }

        public boolean hasDetails() {
            return serviceDetails != null;
        }

        public ServiceDetails details() {
            return serviceDetails;
        }

        @SuppressWarnings("UnusedDeclaration")
        public String errorMsg() {
            return exceptionText;
        }
    }
}
