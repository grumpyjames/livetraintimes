package net.digihippo.ltt;

import com.google.common.base.Function;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class StationIndexTest
{
    @Test
    public void allInputIsCaseInsensitive() throws UnsupportedEncodingException {
        StationIndex<String> stationIndex = StationIndex.parse(
                Arrays.asList(
                        "Marmalade",
                        "mr MARooWISE speechwit",
                        "Leviation mArrrre",
                        "Hello Susan"
                ), new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return s;
                    }
                });

        assertThat(
                stationIndex.search("mar"),
                containsInAnyOrder("Marmalade", "mr MARooWISE speechwit", "Leviation mArrrre"));
    }
}