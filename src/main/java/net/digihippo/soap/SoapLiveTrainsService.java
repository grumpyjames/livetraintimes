package net.digihippo.soap;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import org.grumpysoft.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;

class SoapLiveTrainsService implements LiveTrainsService {
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
        return new MyDepartureBoard(wwhStationBoard, wwhldbServiceSoap, accessToken, toStation);
    }

    private static class MyDepartureBoard implements DepartureBoard {
        private final WWHStationBoardWithDetails wwhStationBoard;
        private final WWHLDBServiceSoap wwhldbServiceSoap;
        private final WWHAccessToken accessToken;
        private final Station toStation;

        public MyDepartureBoard(WWHStationBoardWithDetails wwhStationBoard,
                                WWHLDBServiceSoap wwhldbServiceSoap,
                                WWHAccessToken accessToken,
                                Station toStation) {
            this.wwhStationBoard = wwhStationBoard;
            this.wwhldbServiceSoap = wwhldbServiceSoap;
            this.accessToken = accessToken;
            this.toStation = toStation;
        }

        @Override
        public Iterable<? extends DepartingTrain> departingTrains() {
            return transform(wwhStationBoard.trainServices, new Function<WWHServiceItemWithCallingPoints, DepartingTrain>() {
                @Override
                public DepartingTrain apply(final WWHServiceItemWithCallingPoints wwhServiceItem) {
                    return new MyDepartingTrain(wwhServiceItem);
                }
            });
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

        private ServiceDetails toServiceDetails(
                final String serviceId,
                final WWHArrayOfArrayOfCallingPoints wwhServiceDetails) {
            return new MyServiceDetails(serviceId, wwhServiceDetails);
        }

        private static class MyServiceDetails implements ServiceDetails {
            private final String serviceId;
            private final WWHArrayOfArrayOfCallingPoints subsequentCallingPoints;

            public MyServiceDetails(String serviceId, WWHArrayOfArrayOfCallingPoints subsequentCallingPoints) {
                this.serviceId = serviceId;
                this.subsequentCallingPoints = subsequentCallingPoints;
            }

            @Override
            public Location currentLocation() {
                return new Location() {
                    @Override
                    public LocationStatus status() {
                        return LocationStatus.UNKNOWN;
                    }

                    @Override
                    public List<String> stations() {
                        return ImmutableList.of();
                    }
                };
            }

            @Override
            public String serviceId() {
                return serviceId;
            }

            @Override
            public Iterator<CallingPoint> iterator() {
                return concat(transform(subsequentCallingPoints, new Function<WWHArrayOfCallingPoints, Iterable<CallingPoint>>() {
                    @Override
                    public Iterable<CallingPoint> apply(final WWHArrayOfCallingPoints wwhArrayOfCallingPoints) {
                        return transform(wwhArrayOfCallingPoints.callingPoint, new Function<WWHCallingPoint, CallingPoint>() {
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
                        });
                    }
                })).iterator();
            }
        }

        private class MyDepartingTrain implements DepartingTrain {
            private final WWHServiceItemWithCallingPoints wwhServiceItem;

            public MyDepartingTrain(WWHServiceItemWithCallingPoints wwhServiceItem) {
                this.wwhServiceItem = wwhServiceItem;
            }

            @Override
            public List<String> viaDestinations() {
                return ImmutableList.copyOf(
                        filter(transform(wwhServiceItem.destination, new Function<WWHServiceLocation, String>() {
                            @Override
                            public String apply(WWHServiceLocation wwhServiceLocation) {
                                return wwhServiceLocation.via;
                            }
                        }), Predicates.notNull()));
            }

            @Override
            public boolean isCircularRoute() {
                return wwhServiceItem.isCircularRoute == null ? false : wwhServiceItem.isCircularRoute;
            }

            @Override
            public List<String> destinationList() {
                return ImmutableList.copyOf(
                        transform(wwhServiceItem.destination, new Function<WWHServiceLocation, String>() {
                            @Override
                            public String apply(WWHServiceLocation wwhServiceLocation) {
                                return wwhServiceLocation.locationName;
                            }
                        })
                );
            }

            @Override
            public String platform() {
                return wwhServiceItem.platform;
            }

            @Override
            public String scheduledAt() {
                return wwhServiceItem.sta;
            }

            @Override
            public TrainStatus status() {
                // FIXME: lies
                return TrainStatus.ON_TIME;
            }

            @Override
            public String expectedAt() {
                return wwhServiceItem.etd.equals("On time") ?
                        wwhServiceItem.std : wwhServiceItem.etd;
            }

            @Override
            public String serviceId() {
                return wwhServiceItem.serviceID;
            }

            @Override
            public ServiceDetails serviceDetails() {
                return toServiceDetails(serviceId(), wwhServiceItem.subsequentCallingPoints);
            }
        }
    }

}
