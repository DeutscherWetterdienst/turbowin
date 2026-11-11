package turbowin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
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
    // Do not use main.sdf4 here.  It is mutable application state and asynchronous sensor
    // workers can outlive code that temporarily reconfigures it (tests do this as well).  The
    // sensor record format is fixed, so use an independent UTC parser for this calculation.
    SimpleDateFormat recordFormat = new SimpleDateFormat("yyyyMMddHHmm");
    recordFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    long recordMillis = recordFormat.parse(timestamp).getTime();
    return Math.abs(recordMillis - nowMillis.getAsLong()) / (60 * 1000);
  }
}
