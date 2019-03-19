package net.digihippo.ltt;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FastestTrainTest
{
    private static final class CallingPointsBuilder
    {
        private final Station station;
        private final DepartingTrainTestBuilder trainTestBuilder;
        private final List<CallingPoint> callingPoints = new ArrayList<>();

        private CallingPointsBuilder(
            Station station,
            DepartingTrainTestBuilder trainTestBuilder)
        {
            this.station = station;
            this.trainTestBuilder = trainTestBuilder;
        }

        public CallingPointsBuilder onTime(final String stationFullName, final String scheduledAt)
        {
            callingPoints.add(new CallingPoint(
                Stations.reverseLookup(stationFullName),
                scheduledAt,
                Either.<BadTrainState, String>right("On time")
            ));

            return this;
        }

        public DepartingTrainTestBuilder build()
        {
            trainTestBuilder.destinations.add(station);
            trainTestBuilder.callingPointList.add(callingPoints);
            return trainTestBuilder;
        }
    }

    private static final class DepartingTrainTestBuilder
    {
        private boolean isCircularRoute = false;
        private final List<Station> destinations = new ArrayList<>();
        private final List<String> viaDestinations = new ArrayList<>();
        private final List<List<CallingPoint>> callingPointList = new ArrayList<>();
        private Either<BadTrainState, String> status = Either.left(BadTrainState.Delayed);
        private boolean onTime = false;
        private String platform;
        private String scheduledTime;

        public CallingPointsBuilder withDestination(final String stationFullName)
        {
            return new CallingPointsBuilder(
                Stations.reverseLookup(stationFullName),
                this);
        }

        public DepartingTrainTestBuilder withPlatform(String platform)
        {
            this.platform = platform;
            return this;
        }

        public DepartingTrainTestBuilder onSchedule(String scheduledTime)
        {
            this.onTime = true;
            this.scheduledTime = scheduledTime;
            this.status = Either.right("On time");
            return this;
        }

        public DepartingTrain build()
        {
            return new DepartingTrain(
                isCircularRoute,
                destinations,
                viaDestinations,
                platform,
                new ServiceDetails(
                    Collections.<String>emptySet(),
                    callingPointList
                ),
                status,
                scheduledTime,
                onTime
            );
        }
    }

    @Test
    public void fastestTrainOfASingleTrainIsThatTrain()
    {
        DepartingTrain train = new DepartingTrainTestBuilder()
            .withPlatform("5")
            .withDestination("Beeston")
            .onTime("Bedford", "14:10")
            .onTime("Leicester", "15:20")
            .build()
            .onSchedule("15:43")
            .build();

        assertThat(
            fastestTrainIndex(Stations.reverseLookup("Beeston"), Collections.singletonList(train)),
            is(0));
    }

    private int fastestTrainIndex(Station toStation, List<DepartingTrain> haystack)
    {
        return 0;
    }
}
