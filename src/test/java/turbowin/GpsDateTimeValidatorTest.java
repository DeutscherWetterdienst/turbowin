package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.junit.Test;

public class GpsDateTimeValidatorTest {

  @Test
  public void rejectsMissingDateOrTime() {
    GpsDateTimeValidator.Result result =
        GpsDateTimeValidator.validate("", "", () -> 0, 10);

    assertFalse(result.valid());
    assertEquals(Long.MAX_VALUE, result.minuteTimestamp());
  }

  @Test
  public void rejectsGpsDateOlderThanConfiguredThreshold() throws Exception {
    long currentTimeMillis = parseUtc("010124", "000000");

    GpsDateTimeValidator.Result result =
        GpsDateTimeValidator.validate("311223", "000000", () -> currentTimeMillis, 10);

    assertFalse(result.valid());
    assertTrue(result.message().contains("obsolete"));
  }

  @Test
  public void returnsMinuteTimestampForRecentGpsDate() throws Exception {
    long currentTimeMillis = parseUtc("010124", "000000");

    GpsDateTimeValidator.Result result =
        GpsDateTimeValidator.validate("010124", "000000", () -> currentTimeMillis, 10);

    assertTrue(result.valid());
    assertEquals(currentTimeMillis / (60 * 1000), result.minuteTimestamp());
  }

  @Test
  public void appliesStrictWholeMinuteAgeLimit() throws Exception {
    long gpsTimeMillis = parseUtc("010124", "000000");

    GpsDateTimeValidator.Result withinLimit =
        GpsDateTimeValidator.validate(
            "010124", "000000", () -> gpsTimeMillis + 10 * 60 * 1000 + 59_999, 10);
    GpsDateTimeValidator.Result beyondLimit =
        GpsDateTimeValidator.validate(
            "010124", "000000", () -> gpsTimeMillis + 11 * 60 * 1000, 10);

    assertTrue(withinLimit.valid());
    assertFalse(beyondLimit.valid());
  }

  private static long parseUtc(String date, String time) throws Exception {
    SimpleDateFormat format = new SimpleDateFormat("ddMMyyHHmmss");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format.parse(date + time).getTime();
  }
}
