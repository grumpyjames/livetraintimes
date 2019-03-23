package net.digihippo.ltt;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class CallingPoint implements Serializable {
    public final Station station;
    public final String scheduledAtTime;
    public final Either<BadTrainState, String> et;

    public CallingPoint(Station station, String scheduledAtTime, Either<BadTrainState, String> et) {
        this.station = station;
        this.scheduledAtTime = scheduledAtTime;
        this.et = et;
    }

    @Override
    public String toString() {
        return station.fullName() + "@" + scheduledAtTime;
    }

    boolean hasArrivalTime()
    {
        return et.isRight();
    }

    Date arrivalTime(Date requestTime)
    {
        String unwrap = et.unwrap();
        if (unwrap.equals("On time"))
        {
            return parseDate(scheduledAtTime, requestTime);
        }
        else
        {
            return parseDate(unwrap, requestTime);
        }
    }

    private Date parseDate(String scheduledAtTime, Date requestTime)
    {
        String[] parts = scheduledAtTime.split(":");
        final int hours = Integer.parseInt(parts[0]);
        final int minutes = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(requestTime);
        if (calendar.get(Calendar.HOUR_OF_DAY) > hours)
        {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
