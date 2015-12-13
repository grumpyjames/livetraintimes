package net.digihippo.soap;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import org.grumpysoft.*;

import java.io.IOException;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

class SoapLiveTrainsService implements LiveTrainsService {
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

                        @Override
                        public PointStatus status() {
                            return PointStatus.NO_REPORT;
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
    public ArrivalBoard arrivalsAt(String crsAt) throws IOException {
        return null;
    }

    @Override
    public DepartureBoard boardFor(String crsFrom) throws IOException {
        try {
            return toDepartureBoard(
                    wwhldbServiceSoap.GetDepBoardWithDetails(20, crsFrom, null, null, null, null, accessToken),
                    null);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public DepartureBoard boardForJourney(String crsFrom, String crsTo) throws IOException {
        try {
            return toDepartureBoard(
                    wwhldbServiceSoap.GetDepBoardWithDetails(
                            20, crsFrom, crsTo, null, null, null, accessToken),
                    Stations.lookup(crsTo));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private DepartureBoard toDepartureBoard(final WWHStationBoardWithDetails wwhStationBoard, Station toStation) {
        return new MyDepartureBoard(wwhStationBoard, toStation);
    }

    private static class MyDepartureBoard implements DepartureBoard {
        private final WWHStationBoardWithDetails wwhStationBoard;
        private final Station toStation;

        public MyDepartureBoard(WWHStationBoardWithDetails wwhStationBoard, Station toStation) {
            this.wwhStationBoard = wwhStationBoard;
            this.toStation = toStation;
        }

        @Override
        public Iterable<? extends DepartingTrain> departingTrains() {
            return transform(wwhStationBoard.trainServices, ExtractDepartingTrain);
        }

        @Override
        public boolean hasToStation() {
            return toStation != null;
        }

        @Override
        public Station toStation() {
            return toStation;
        }

        @Override
        public Station station() {
            return null;
        }

        @Override
        public String generatedTime() {
            return "now";
        }

        @Override
        public String updateText() {
            return "huh?";
        }

    }

    private static Iterable<CallingPoint> convertCallingPoints(WWHArrayOfArrayOfCallingPoints wwhServiceDetails) {
        return copyOf(concat(transform(wwhServiceDetails, CallingPointsExtractor)));
    }
}
