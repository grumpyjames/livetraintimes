package net.digihippo.ltt;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;

import static net.digihippo.ltt.Stations.allStations;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class StationIndexTest
{
    @Test
    public void byThreeLetterCode()
    {
        StationIndex<Station> index = StationIndex.parse(
            allStations(),
            new Function<Station, String>()
            {
                @Override
                public String apply(Station station)
                {
                    return station.fullName();
                }
            },
            new Function<Station, String>()
            {
                @Override
                public String apply(Station station)
                {
                    return station.threeLetterCode();
                }
            });

        Iterable<Station> clj = index.search("CLJ");
        assertThat(clj, Matchers.<Station>iterableWithSize(1));
    }

    @Test
    public void allInputIsCaseInsensitive()
    {
        StationIndex<String> stationIndex = StationIndex.parse(
            Arrays.asList(
                "Marmalade",
                "mr MARooWISE speechwit",
                "Leviation mArrrre",
                "Hello Susan"
            ), new Function<String, String>()
            {
                @Override
                public String apply(String s)
                {
                    return s;
                }
            });

        assertThat(
            stationIndex.search("mar"),
            containsInAnyOrder("Marmalade", "mr MARooWISE speechwit", "Leviation mArrrre"));
    }

    @Test
    public void londonWaterloo()
    {
        StationIndex<String> stationIndex = StationIndex.parse(
            Arrays.asList(
                "London Waterloo",
                "London Marylebone"
            ), new Function<String, String>()
            {
                @Override
                public String apply(String s)
                {
                    return s;
                }
            });

        assertThat(
            stationIndex.search("London W"),
            containsInAnyOrder("London Waterloo"));
    }

    @Test
    public void beNiceWithTwoByteWords()
    {
        StationIndex<String> stationIndex = StationIndex.parse(
            Arrays.asList(
                "St Lo Mc Bo",
                "London St Pancras",
                "Boroda",
                "St Peter"
            ), new Function<String, String>()
            {
                @Override
                public String apply(String s)
                {
                    return s;
                }
            });

        assertThat(
            stationIndex.search("st "),
            containsInAnyOrder("St Lo Mc Bo", "London St Pancras", "St Peter"));

        assertThat(
            stationIndex.search("lo "),
            containsInAnyOrder("St Lo Mc Bo"));
    }
}