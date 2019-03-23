package net.digihippo.ltt.soap;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.digihippo.ltt.*;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static net.digihippo.ltt.DepartureTimes.infer;

public class SoapLiveTrainsService implements DepartureBoardService {
    public static DepartureBoardService departureBoardService() {
        final WWHLDBServiceSoap wwhldbServiceSoap = new WWHLDBServiceSoap();

        final WWHAccessToken accessToken = new WWHAccessToken();
        accessToken.TokenValue = "dcfa2a36-cb60-4e03-9264-c9544446945f";

        return new SoapLiveTrainsService(wwhldbServiceSoap, accessToken);
    }
    private static final Function<WWHServiceLocation, String> ExtractViaDestination =
            new Function<WWHServiceLocation, String>() {
                @Override
                public String apply(WWHServiceLocation wwhServiceLocation) {
                    return wwhServiceLocation.via;
                }
            };
    private static final Function<WWHServiceLocation, Station> ExtractLocationName = new Function<WWHServiceLocation, Station>() {
        @Override
        public Station apply(final WWHServiceLocation wwhServiceLocation) {
            return Stations.lookup(wwhServiceLocation.crs);
        }
    };
    private static final Function<WWHServiceItemWithCallingPoints, DepartingTrain> ExtractDepartingTrain =
            new Function<WWHServiceItemWithCallingPoints, DepartingTrain>() {
                @Override
                public DepartingTrain apply(final WWHServiceItemWithCallingPoints wwhServiceItem) {
                    boolean isCircularRoute =
                            wwhServiceItem.isCircularRoute == null ? false : wwhServiceItem.isCircularRoute;
                    ImmutableList<Station> destinations =
                            ImmutableList.copyOf(Iterables.transform(wwhServiceItem.destination, ExtractLocationName));
                    ImmutableList<String> viaDestinations = ImmutableList.copyOf(
                            Iterables.filter(
                                Iterables.transform(wwhServiceItem.destination, ExtractViaDestination),
                                Predicates.notNull()));
                    return new DepartingTrain(
                        new Date() /* wrong, but haven't got the root wwh thing in scope */,
                        isCircularRoute,
                        destinations,
                        viaDestinations,
                        wwhServiceItem.platform,
                        toServiceDetails(wwhServiceItem.subsequentCallingPoints),
                        infer(wwhServiceItem.etd, wwhServiceItem.std),
                        wwhServiceItem.std,
                        "On time".equals(wwhServiceItem.etd)
                    );
                }
            };

    private final WWHLDBServiceSoap wwhldbServiceSoap;
    private final WWHAccessToken accessToken;

    public SoapLiveTrainsService(WWHLDBServiceSoap wwhldbServiceSoap, WWHAccessToken accessToken) {
        this.wwhldbServiceSoap = wwhldbServiceSoap;
        this.accessToken = accessToken;
    }

    private DepartureBoard boardFor(final Station from, final Optional<Station> to) throws Exception {

        return toDepartureBoard(
                wwhldbServiceSoap.GetDepBoardWithDetails(
                        20,
                        from.threeLetterCode(),
                        to.isPresent() ? to.get().threeLetterCode() : null,
                        null,
                        null,
                        null,
                        accessToken)
        );

    }

    public DepartureBoard boardFor(Station fromStation) throws Exception
    {
        return boardFor(fromStation, Optional.<Station>absent());
    }

    public DepartureBoard boardFor(Station fromStation, Station toStation) throws Exception
    {
        return boardFor(fromStation, Optional.of(toStation));
    }

    public void httpsIsBroken()
    {
        wwhldbServiceSoap.httpsIsBroken();
    }

    private DepartureBoard toDepartureBoard(final WWHStationBoardWithDetails wwhStationBoard) {
        return new DepartureBoard(ImmutableList.copyOf(Iterables.transform(wwhStationBoard.trainServices, ExtractDepartingTrain)));
    }

    private static ServiceDetails toServiceDetails(WWHArrayOfArrayOfCallingPoints wwhServiceDetails) {
        final ImmutableList.Builder<CallingPoint> callingPoints = ImmutableList.builder();

        // Know: first list is the 'master' (what about if we query from the 'split' station?)
        // Splitaways start with the split station.
        final WWHArrayOfCallingPoints masterBranch = wwhServiceDetails.get(0);
        final Iterable<WWHArrayOfCallingPoints> otherBranches = Iterables.skip(wwhServiceDetails, 1);
        final List<CallingPointBuilder> forks =
                Lists.newArrayListWithCapacity(wwhServiceDetails.size());
        final ImmutableSet.Builder<String> forkLocations = ImmutableSet.builder();

        for (final WWHCallingPoint next : masterBranch.callingPoint) {
            Iterable<WWHArrayOfCallingPoints> journeysStartingHere =
                    Iterables.filter(otherBranches, new Predicate<WWHArrayOfCallingPoints>() {
                        @Override
                        public boolean apply(WWHArrayOfCallingPoints callingPoints) {
                            return callingPoints.callingPoint.get(0).locationName.equals(next.locationName);
                        }
                    });

            for (WWHArrayOfCallingPoints wwhArrayOfCallingPoints : journeysStartingHere) {
                CallingPointBuilder cpb = new CallingPointBuilder(wwhArrayOfCallingPoints.callingPoint.iterator());
                cpb.addAll(callingPoints);
                forks.add(cpb);
                forkLocations.add(next.locationName);
            }

            callingPoints.add(
                new CallingPoint(Stations.lookup(next.crs), next.st, infer(next.et, next.st)));
        }


        return new ServiceDetails(
                forkLocations.build(),
                ImmutableList.copyOf(
                        Iterables.concat(
                                ImmutableList.<List<CallingPoint>>of(callingPoints.build()),
                                Iterables.transform(forks,
                                        new Function<CallingPointBuilder, List<CallingPoint>>() {
                                            @Override
                                            public List<CallingPoint> apply(CallingPointBuilder cpb) {
                                                return cpb.build();
                                            }
                                        }))));
    }

    private static final class CallingPointBuilder {
        private final ImmutableList.Builder<CallingPoint> callingPoints = ImmutableList.builder();
        private final Iterator<WWHCallingPoint> iterator;

        private CallingPointBuilder(Iterator<WWHCallingPoint> iterator) {
            this.iterator = iterator;
        }

        public void addAll(ImmutableList.Builder<CallingPoint> callingPoints) {
            this.callingPoints.addAll(callingPoints.build());
        }

        public List<CallingPoint> build() {
            while (iterator.hasNext()) {
                WWHCallingPoint next = iterator.next();
                callingPoints.add(
                    new CallingPoint(Stations.lookup(next.crs), next.st, infer(next.et, next.st)));
            }
            return callingPoints.build();
        }
    }
}
