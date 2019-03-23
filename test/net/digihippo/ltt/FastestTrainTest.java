package net.digihippo.ltt;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FastestTrainTest
{
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

    @Test
    public void fastestTrainOfATwoTrainsIsTheOneThatArrivesSooner()
    {
        DepartingTrain train = new DepartingTrainTestBuilder()
            .requestedAt("2018-11-05T14:00:00.000Z")
            .withPlatform("5")
            .withDestination("Beeston")
            .onTime("Bedford", "14:10")
            .onTime("Leicester", "15:20")
            .onTime("Beeston", "15:46")
            .build()
            .onSchedule("15:43")
            .build();

        DepartingTrain trainTwo = new DepartingTrainTestBuilder()
            .requestedAt("2018-11-05T14:00:00.000Z")
            .withPlatform("3")
            .withDestination("Beeston")
            .onTime("Luton Airport Parkway", "14:05")
            .onTime("Beeston", "15:20")
            .build()
            .onSchedule("15:50")
            .build();

        assertThat(
            fastestTrainIndex(
                Stations.reverseLookup("Beeston"),
                Arrays.asList(train, trainTwo)),
            is(1));
    }

    @Test
    public void nastyLittleEdgeCase()
    {
        String requestedAt = "2018-11-05T22:00:00.000Z";
        DepartingTrain train = new DepartingTrainTestBuilder()
            .requestedAt(requestedAt)
            .withDestination("Beeston")
            .onTime("Beeston", "23:59")
            .build()
            .onSchedule("22:15")
            .build();

        DepartingTrain trainTwo = new DepartingTrainTestBuilder()
            .requestedAt(requestedAt)
            .withPlatform("3")
            .withDestination("Beeston")
            .onTime("Beeston", "00:10")
            .build()
            .onSchedule("22:30")
            .build();

        assertThat(
            fastestTrainIndex(
                Stations.reverseLookup("Beeston"),
                Arrays.asList(train, trainTwo)),
            is(0));
    }

    private int fastestTrainIndex(Station toStation, List<DepartingTrain> haystack)
    {
        int fastest = -1;
        Date earliest = null;
        for (int i = 0; i < haystack.size(); i++)
        {
            final DepartingTrain train = haystack.get(i);
            final Date arrivalTime = train.getArrivalTime(toStation);
            if (fastest == -1 || arrivalTime.before(earliest))
            {
                fastest = i;
                earliest = arrivalTime;
            }
        }
        return fastest;
    }
}
