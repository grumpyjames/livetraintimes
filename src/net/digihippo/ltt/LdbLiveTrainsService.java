package net.digihippo.ltt;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.digihippo.ltt.ldb.AndroidTrainService;

import java.util.*;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static net.digihippo.ltt.SoapLiveTrainsService.infer;

public class LdbLiveTrainsService implements DepartureBoardService
{
    private final AndroidTrainService androidTrainService =
        new AndroidTrainService("dcfa2a36-cb60-4e03-9264-c9544446945f");

    @Override
    public DepartureBoard boardFor(
        Station fromStation,
        Optional<Station> toStation) throws Exception
    {
        AndroidTrainService.Response response = androidTrainService.fetchTrains(
            fromStation.threeLetterCode(),
            toStation.isPresent() ? toStation.get().threeLetterCode() : null);


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

            trains.add(new DepartingTrain(
                isCircularRoute,
                ImmutableList.copyOf(destinations),
                ImmutableList.copyOf(viaDestinations),
                service.platform,
                toServiceDetails(service.callingPointLists),
                infer(service.etd, service.std),
                service.std,
                "On time".equals(service.etd)
            ));
        }

        return new DepartureBoard(
            trains
        );
    }

    @Override
    public void httpsIsBroken()
    {
        androidTrainService.httpIsBroken();
    }

    private static ServiceDetails
    toServiceDetails(List<List<AndroidTrainService.CallingPoint>> callingPointsList) {
        final ImmutableList.Builder<CallingPoint> callingPoints = ImmutableList.builder();

        // Know: first list is the 'master' (what about if we query from the 'split' station?)
        // Splitaways start with the split station.
        final List<AndroidTrainService.CallingPoint> masterBranch = callingPointsList.get(0);
        final List<List<AndroidTrainService.CallingPoint>> otherBranches =
            callingPointsList.subList(1, callingPointsList.size());
        final List<CallingPointBuilder> forks =
            new ArrayList<>(callingPointsList.size());
        final Set<String> forkLocations = new HashSet<>();

        for (final AndroidTrainService.CallingPoint next : masterBranch) {
            Iterable<List<AndroidTrainService.CallingPoint>> journeysStartingHere =
                Iterables.filter(otherBranches, new Predicate<List<AndroidTrainService.CallingPoint>>() {
                    @Override
                    public boolean apply(List<AndroidTrainService.CallingPoint> callingPoints) {
                        return callingPoints.get(0).locationName.equals(next.locationName);
                    }
                });

            for (List<AndroidTrainService.CallingPoint> journey : journeysStartingHere) {
                CallingPointBuilder cpb = new CallingPointBuilder(journey.iterator());
                cpb.addAll(callingPoints);
                forks.add(cpb);
                forkLocations.add(next.locationName);
            }

            callingPoints.add(
                CallingPoint.singlePoint(
                    Stations.lookup(next.crs),
                    next.scheduledTime,
                    infer(next.expectedTime, next.scheduledTime)));
        }


        return new ServiceDetails(
            forkLocations,
            copyOf(
                concat(
                    ImmutableList.<List<CallingPoint>>of(callingPoints.build()),
                    transform(forks,
                        new Function<CallingPointBuilder, List<CallingPoint>>() {
                            @Override
                            public List<CallingPoint> apply(CallingPointBuilder cpb) {
                                return cpb.build();
                            }
                        }))));
    }

    private static final class CallingPointBuilder {
        private final ImmutableList.Builder<CallingPoint> callingPoints = ImmutableList.builder();
        private final Iterator<AndroidTrainService.CallingPoint> iterator;

        private CallingPointBuilder(Iterator<AndroidTrainService.CallingPoint> iterator) {
            this.iterator = iterator;
        }

        public void addAll(ImmutableList.Builder<CallingPoint> callingPoints) {
            this.callingPoints.addAll(callingPoints.build());
        }

        public List<CallingPoint> build() {
            while (iterator.hasNext()) {
                AndroidTrainService.CallingPoint next = iterator.next();
                callingPoints.add(
                    CallingPoint.singlePoint(
                        Stations.lookup(next.crs),
                        next.scheduledTime,
                        infer(next.expectedTime, next.scheduledTime)));
            }
            return callingPoints.build();
        }
    }
}
