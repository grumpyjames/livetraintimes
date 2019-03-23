package net.digihippo.ltt;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class CallingPointTest
{
    private final SimpleDateFormat format =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");


    @Test
    public void arrivalTimeMustBeAfterBoardRequestTime() throws ParseException
    {
        CallingPoint callingPoint = new CallingPoint(
            Stations.reverseLookup("Liverpool Lime Street"),
            "00:01",
            Either.<BadTrainState, String>right("On time"));

        assertThat(
            format.format(
                callingPoint.arrivalTime(
                    format.parse("2018-10-11T23:15:06.233Z"))),
            equalTo("2018-10-12T00:01:00.000"));

    }

}