package net.digihippo.soap;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import org.grumpysoft.*;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

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
                    return new CallingPoint() {
                        @Override
                        public String stationName() {
                            return wwhCallingPoint.locationName;
                        }

                        @Override
                        public String scheduledTime() {
                            return wwhCallingPoint.st;
                        }
                    };
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

    private static Iterable<CallingPoint> convertCallingPoints(WWHArrayOfArrayOfCallingPoints wwhServiceDetails) {
        return copyOf(concat(transform(wwhServiceDetails, CallingPointsExtractor)));
    }
}
