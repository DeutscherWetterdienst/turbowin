package turbowin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Prevents repeated dispatch of the same scheduled UTC hour within this application session. */
final class AwsrUploadGate {
  private Instant lastScheduledHour;
  private Instant lastRetryHour;

  synchronized boolean claim(Instant time, boolean retry) {
    Instant hour = time.truncatedTo(ChronoUnit.HOURS);
    Instant last = retry ? lastRetryHour : lastScheduledHour;
    if (last != null && !hour.isAfter(last)) {
      return false;
    }
    if (retry) {
      lastRetryHour = hour;
    } else {
      lastScheduledHour = hour;
    }
    return true;
  }
}
