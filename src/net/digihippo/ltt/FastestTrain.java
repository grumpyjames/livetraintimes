package net.digihippo.ltt;

import java.util.Date;
import java.util.List;

public final class FastestTrain
{
    public static int fastestTrainIndex(
        Station toStation, List<DepartingTrain> haystack)
    {
        int fastest = -1;
        Date earliest = null;
        for (int i = 0; i < haystack.size(); i++)
        {
            final DepartingTrain train = haystack.get(i);
            final Date arrivalTime = train.findArrivalTimeAt(toStation);
            if (arrivalTime != null && (fastest == -1 || arrivalTime.before(earliest)))
            {
                fastest = i;
                earliest = arrivalTime;
            }
        }
        return fastest;
    }

    private FastestTrain() {}
}
