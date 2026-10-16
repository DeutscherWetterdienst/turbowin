package turbowin;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.junit.Test;

public class SensorDataRecordAgeCalculatorTest {

  private static final String TIMESTAMP = "201502041505";
  private static final long TIMESTAMP_MILLIS = 1423062300000L;

  @Test
  public void calculatesAbsoluteAgeInWholeMinutes() throws Exception {
    SimpleDateFormat original = main.sdf4;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    main.sdf4 = format;
    try {
      assertEquals(
          3, SensorDataRecordAgeCalculator.ageMinutes(TIMESTAMP, TIMESTAMP_MILLIS + 3 * 60 * 1000));
      assertEquals(
          3, SensorDataRecordAgeCalculator.ageMinutes(TIMESTAMP, TIMESTAMP_MILLIS - 3 * 60 * 1000));
    } finally {
      main.sdf4 = original;
    }
  }

  @Test
  public void samplesClockAfterParsingTimestamp() throws Exception {
    SimpleDateFormat original = main.sdf4;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    main.sdf4 = format;
    try {
      assertEquals(
          3,
          SensorDataRecordAgeCalculator.ageMinutes(
              TIMESTAMP,
              () -> {
                main.sdf4 = null;
                return TIMESTAMP_MILLIS + 3 * 60 * 1000;
              }));
    } finally {
      main.sdf4 = original;
    }
  }
}
