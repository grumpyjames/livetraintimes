package net.digihippo.ltt.ldb;

import android.annotation.SuppressLint;
import net.digihippo.ltt.CallingPoint;
import net.digihippo.ltt.DepartingTrain;
import net.digihippo.ltt.DepartureBoard;
import net.digihippo.ltt.DepartureBoardService;
import net.digihippo.ltt.ServiceDetails;
import net.digihippo.ltt.Station;
import net.digihippo.ltt.Stations;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static net.digihippo.ltt.DepartureTimes.infer;

public class LdbLiveTrainsService implements DepartureBoardService
{
    public static final String TOKEN = "dcfa2a36-cb60-4e03-9264-c9544446945f";
    private final AndroidTrainService androidTrainService =
        new AndroidTrainService(TOKEN);

    @Override
    public DepartureBoard boardFor(Station fromStation) throws IOException
    {
        return boardFor(fromStation, null);
    }

    @Override
    public DepartureBoard boardFor(
        Station fromStation,
        Station toStation) throws IOException
    {
        AndroidTrainService.Response response =
            null;

        response = androidTrainService.fetchTrains(
            fromStation.threeLetterCode(),
            toStation == null ? null : toStation.threeLetterCode());


        final List<DepartingTrain> trains = new ArrayList<>();
        for (AndroidTrainService.Service service : response.services)
        {
            boolean isCircularRoute =
                service.isCircularRoute;
            final List<Station> destinations = new ArrayList<>(service.destinations.size());
            final List<String> viaDestinations = new ArrayList<>();
            for (AndroidTrainService.Destination destination : service.destinations)
            {
                destinations.add(Stations.lookup(destination.crs));
                if (destination.via != null)
                {
                    viaDestinations.add(destination.via);
                }
            }

            // We can't easily parse the time zone from the response because it contains
            // a variable amount of precision in the subsecond region. My god.
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            format.setTimeZone(TimeZone.getTimeZone("Europe/London"));

            try
            {
                trains.add(new DepartingTrain(
                    format.parse(response.generatedAt),
                    isCircularRoute,
                    destinations,
                    viaDestinations,
                    service.platform,
                    toServiceDetails(service.callingPointLists),
                    infer(service.etd, service.std),
                    service.std,
                    "On time".equals(service.etd)
                ));
            } catch (ParseException e)
            {
                throw new RuntimeException(e);
            }
        }

        return new DepartureBoard(trains);
    }

    private static ServiceDetails
    toServiceDetails(List<List<AndroidTrainService.CallingPoint>> callingPointsList) {
        final List<CallingPoint> callingPoints = new ArrayList<>();

        // Know: first list is the 'master' (what about if we query from the 'split' station?)
        // Splitaways start with the split station.
        final List<AndroidTrainService.CallingPoint> masterBranch = callingPointsList.get(0);
        final List<List<AndroidTrainService.CallingPoint>> otherBranches =
            callingPointsList.subList(1, callingPointsList.size());
        final List<CallingPointBuilder> forks =
            new ArrayList<>(callingPointsList.size());
        final Set<String> forkLocations = new HashSet<>();

        for (final AndroidTrainService.CallingPoint next : masterBranch) {

            for (List<AndroidTrainService.CallingPoint> journey : otherBranches)
            {
                if (journey.get(0).locationName.equals(next.locationName))
                {
                    CallingPointBuilder cpb = new CallingPointBuilder(journey.iterator());
                    cpb.addAll(callingPoints);
                    forks.add(cpb);
                    forkLocations.add(next.locationName);
                }
            }

            callingPoints.add(
                new CallingPoint(
                    Stations.lookup(next.crs),
                    next.scheduledTime,
                    infer(next.expectedTime, next.scheduledTime)));
        }

        final List<List<CallingPoint>> callingPointLists = new ArrayList<>();
        callingPointLists.add(callingPoints);
        for (CallingPointBuilder fork : forks)
        {
            callingPointLists.add(fork.build());
        }

        return new ServiceDetails(forkLocations, callingPointLists);
    }

    private static final class CallingPointBuilder {
        private final List<CallingPoint> callingPoints = new ArrayList<>();
        private final Iterator<AndroidTrainService.CallingPoint> iterator;

        private CallingPointBuilder(Iterator<AndroidTrainService.CallingPoint> iterator) {
            this.iterator = iterator;
        }

        void addAll(List<CallingPoint> callingPoints) {
            this.callingPoints.addAll(callingPoints);
        }

        List<CallingPoint> build() {
            while (iterator.hasNext()) {
                AndroidTrainService.CallingPoint next = iterator.next();
                callingPoints.add(
                    new CallingPoint(
                        Stations.lookup(next.crs),
                        next.scheduledTime,
                        infer(next.expectedTime, next.scheduledTime)));
            }
            return callingPoints;
        }
    }
}
