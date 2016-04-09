package de.laboranowitsch.priceimporter.util;

import org.junit.Test;

import java.time.ZonedDateTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Testing of {@link ZoneDateTime} utility.
 *
 * Created by cla on 4/9/16.
 */
public class ZoneDateTimeTests {

    /**
     * Test conversion
     */
    @Test
    public void testConversion() {
        ZonedDateTime dateTime = de.laboranowitsch.priceimporter.util.ZoneDateTime.of("2016/03/22 04:30:00");
        assertThat("has the right year", dateTime.getYear(), is(equalTo(2016)));
        assertThat("has the right month", dateTime.getMonthValue(), is(equalTo(3)));
        assertThat("has the right day of month", dateTime.getDayOfMonth(), is(equalTo(22)));
        assertThat("has the right hour of day", dateTime.getHour(), is(equalTo(04)));
        assertThat("has the right minutes of hour", dateTime.getMinute(), is(equalTo(30)));
        assertThat("has the right seconds of minutes", dateTime.getSecond(), is(equalTo(00)));
    }
}
