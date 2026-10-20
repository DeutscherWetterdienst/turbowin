package turbowin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.function.LongSupplier;

/**
 * Validates legacy GPS date/time fields and returns their minute timestamp.
 *
 * <p>The GPS date is {@code ddMMyy}, the GPS time starts with {@code HHmmss}, and both are parsed
 * as UTC. The returned timestamp is the number of minutes since the Unix epoch; the clock supplier
 * is sampled after parsing so the age check retains the original timing.
 */
final class GpsDateTimeValidator {

  private GpsDateTimeValidator() {}

  static Result validate(
      String gpsDate,
      String gpsTime,
      LongSupplier currentTimeMillis,
      long maximumAgeMinutes) {
    if ((gpsDate.length() != 6) || (gpsTime.length() < 6)) {
      return new Result(
          false,
          "[GPS] GPS date/time format not ok (GPS date = "
              + gpsDate
              + ", GPS time = "
              + gpsTime
              + ")",
          Long.MAX_VALUE);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

    String yyyyMMdd =
        "20" + gpsDate.substring(4) + gpsDate.substring(2, 4) + gpsDate.substring(0, 2);
    String hhmm = gpsTime.substring(0, 4);
    String dateTime = yyyyMMdd + hhmm;

    try {
      Date gpsDateTime = dateFormat.parse(dateTime);
      long timeDifference =
          Math.abs(gpsDateTime.getTime() - currentTimeMillis.getAsLong()) / (60 * 1000);
      if (timeDifference > maximumAgeMinutes) {
        return new Result(
            false,
            "[GPS] GPS date/time obsolete; > "
                + (int) maximumAgeMinutes
                + " minutes old compared with computer date/time (time difference = "
                + timeDifference
                + " minutes)",
            Long.MAX_VALUE);
      }
      return new Result(true, "", gpsDateTime.getTime() / (60 * 1000));
    } catch (ParseException ex) {
      return new Result(false, "[GPS] GPS date/time format not ok (" + ex + ")", Long.MAX_VALUE);
    }
  }

  static final class Result {
    private final boolean valid;
    private final String message;
    private final long minuteTimestamp;

    private Result(boolean valid, String message, long minuteTimestamp) {
      this.valid = valid;
      this.message = message;
      this.minuteTimestamp = minuteTimestamp;
    }

    boolean valid() {
      return valid;
    }

    String message() {
      return message;
    }

    long minuteTimestamp() {
      return minuteTimestamp;
    }
  }
}
