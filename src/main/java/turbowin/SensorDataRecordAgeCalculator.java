package turbowin;

import java.text.ParseException;
import java.util.function.LongSupplier;

/**
 * Calculates absolute sensor-record age in whole minutes from the final 12-character
 * {@code YYYYMMDDHHmm} timestamp field.
 */
final class SensorDataRecordAgeCalculator {

  private SensorDataRecordAgeCalculator() {}

  static long ageMinutes(String timestamp, long nowMillis) throws ParseException {
    return ageMinutes(timestamp, () -> nowMillis);
  }

  static long ageMinutes(String timestamp, LongSupplier nowMillis) throws ParseException {
    long recordMillis = main.sdf4.parse(timestamp).getTime();
    return Math.abs(recordMillis - nowMillis.getAsLong()) / (60 * 1000);
  }
}
