package net.digihippo.soap;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.grumpysoft.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static java.util.Collections.singleton;
import static org.grumpysoft.CallingPoint.singlePoint;

public class SoapLiveTrainsService implements DepartureBoardService {
    public static DepartureBoardService departureBoardService() {
        final WWHLDBServiceSoap wwhldbServiceSoap = new WWHLDBServiceSoap();

        final WWHAccessToken accessToken = new WWHAccessToken();
        accessToken.TokenValue = "dcfa2a36-cb60-4e03-9264-c9544446945f";

        return new SoapLiveTrainsService(wwhldbServiceSoap, accessToken);
    }

    private static final Function<WWHCallingPoint, CallingPoint> CallingPointExtractor =
            new Function<WWHCallingPoint, CallingPoint>() {
                @Override
                public CallingPoint apply(final WWHCallingPoint wwhCallingPoint) {
                    return singlePoint(wwhCallingPoint.locationName, wwhCallingPoint.st);
                }
            };
    private static final Function<WWHArrayOfCallingPoints, Iterable<CallingPoint>> CallingPointsExtractor =
            new Function<WWHArrayOfCallingPoints, Iterable<CallingPoint>>() {
                @Override
                public Iterable<CallingPoint> apply(final WWHArrayOfCallingPoints wwhArrayOfCallingPoints) {
                    return transform(wwhArrayOfCallingPoints.callingPoint, CallingPointExtractor);
                }
            };

    private static final Function<WWHServiceLocation, String> ExtractViaDestination =
            new Function<WWHServiceLocation, String>() {
                @Override
                public String apply(WWHServiceLocation wwhServiceLocation) {
                    return wwhServiceLocation.via;
                }
            };
    private static final Function<WWHServiceLocation, String> ExtractLocationName = new Function<WWHServiceLocation, String>() {
        @Override
        public String apply(WWHServiceLocation wwhServiceLocation) {
            return wwhServiceLocation.locationName;
        }
    };
    private static final Function<WWHServiceItemWithCallingPoints, DepartingTrain> ExtractDepartingTrain =
            new Function<WWHServiceItemWithCallingPoints, DepartingTrain>() {
                @Override
                public DepartingTrain apply(final WWHServiceItemWithCallingPoints wwhServiceItem) {
                    boolean isCircularRoute =
                            wwhServiceItem.isCircularRoute == null ? false : wwhServiceItem.isCircularRoute;
                    ImmutableList<String> destinations =
                            copyOf(transform(wwhServiceItem.destination, ExtractLocationName));
                    ImmutableList<String> viaDestinations = copyOf(
                            filter(transform(wwhServiceItem.destination, ExtractViaDestination), Predicates.notNull()));
                    String expectedDepartureTime =
                            wwhServiceItem.etd.equals("On time") ? wwhServiceItem.std : wwhServiceItem.etd;
                    return new DepartingTrain(
                            isCircularRoute,
                            destinations,
                            viaDestinations,
                            wwhServiceItem.platform,
                            expectedDepartureTime,
                            convertCallingPoints(wwhServiceItem.subsequentCallingPoints));
                }
            };

    private final WWHLDBServiceSoap wwhldbServiceSoap;
    private final WWHAccessToken accessToken;

    public SoapLiveTrainsService(WWHLDBServiceSoap wwhldbServiceSoap, WWHAccessToken accessToken) {
        this.wwhldbServiceSoap = wwhldbServiceSoap;
        this.accessToken = accessToken;
    }

    @Override
    public DepartureBoard boardFor(final Station from, final Optional<Station> to) throws Exception {

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

    private DepartureBoard toDepartureBoard(final WWHStationBoardWithDetails wwhStationBoard) {
        return new DepartureBoard(transform(wwhStationBoard.trainServices, ExtractDepartingTrain));
    }

    private static Collection<Collection<CallingPoint>> convertCallingPoints(WWHArrayOfArrayOfCallingPoints wwhServiceDetails) {
        final ImmutableList.Builder<CallingPoint> callingPoints = ImmutableList.builder();

        // Know: first list is the 'master' (what about if we query from the 'split' station?)
        // Splitaways start with the split station.
        final WWHArrayOfCallingPoints masterBranch = wwhServiceDetails.get(0);
        final Iterable<WWHArrayOfCallingPoints> otherBranches = Iterables.skip(wwhServiceDetails, 1);
        final List<CallingPointBuilder> forks =
                Lists.newArrayListWithCapacity(wwhServiceDetails.size());

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
            }

            callingPoints.add(CallingPoint.singlePoint(next.locationName, next.st));
        }


        return copyOf(concat(
                singleton(callingPoints.build()),
                transform(forks, new Function<CallingPointBuilder, Collection<CallingPoint>>() {
                    @Override
                    public Collection<CallingPoint> apply(CallingPointBuilder callingPointBuilder) {
                        return callingPointBuilder.build();
                    }
                })));
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

        public Collection<CallingPoint> build() {
            while (iterator.hasNext()) {
                WWHCallingPoint next = iterator.next();
                callingPoints.add(CallingPoint.singlePoint(next.st, next.locationName));
            }
            return callingPoints.build();
        }
    }
}
