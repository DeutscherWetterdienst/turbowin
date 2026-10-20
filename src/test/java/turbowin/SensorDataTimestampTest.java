package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;

public class SensorDataTimestampTest {

  @Test
  public void roundsLateSecondsToTheNextMinute() {
    Calendar timestamp = timestampWithSecond(57);

    SensorDataTimestamp.roundToNextMinuteWhenLate(timestamp);

    assertEquals(0, timestamp.get(Calendar.SECOND));
    assertEquals(1, timestamp.get(Calendar.MINUTE));
  }

  @Test
  public void rounds58SecondsToTheNextMinute() {
    Calendar timestamp = timestampWithSecond(58);

    SensorDataTimestamp.roundToNextMinuteWhenLate(timestamp);

    assertEquals(0, timestamp.get(Calendar.SECOND));
    assertEquals(1, timestamp.get(Calendar.MINUTE));
  }

  @Test
  public void rounds59SecondsAcrossTheHourBoundary() {
    Calendar timestamp = timestampWithSecond(12, 59, 59);

    SensorDataTimestamp.roundToNextMinuteWhenLate(timestamp);

    assertEquals(0, timestamp.get(Calendar.SECOND));
    assertEquals(0, timestamp.get(Calendar.MINUTE));
    assertEquals(13, timestamp.get(Calendar.HOUR_OF_DAY));
  }

  @Test
  public void leavesEarlierSecondsUnchanged() {
    Calendar timestamp = timestampWithSecond(56);

    SensorDataTimestamp.roundToNextMinuteWhenLate(timestamp);

    assertEquals(56, timestamp.get(Calendar.SECOND));
    assertEquals(0, timestamp.get(Calendar.MINUTE));
  }

  private static Calendar timestampWithSecond(int second) {
    return timestampWithSecond(12, 0, second);
  }

  private static Calendar timestampWithSecond(int hour, int minute, int second) {
    Calendar timestamp = new GregorianCalendar(2026, Calendar.JANUARY, 1, hour, minute, second);
    timestamp.set(Calendar.MILLISECOND, 0);
    return timestamp;
  }
}
