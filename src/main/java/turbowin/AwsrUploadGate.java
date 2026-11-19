package turbowin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Prevents repeated dispatch of the same scheduled UTC hour within this application session. */
final class AwsrUploadGate {
  private Instant lastScheduledHour;
  private Instant lastRetryHour;
  private Instant previousScheduledHour;
  private Instant previousRetryHour;

  synchronized boolean claim(Instant time, boolean retry) {
    Instant hour = time.truncatedTo(ChronoUnit.HOURS);
    Instant last = retry ? lastRetryHour : lastScheduledHour;
    if (last != null && !hour.isAfter(last)) {
      return false;
    }
    if (retry) {
      previousRetryHour = lastRetryHour;
      lastRetryHour = hour;
    } else {
      previousScheduledHour = lastScheduledHour;
      lastScheduledHour = hour;
    }
    return true;
  }

  synchronized void release(Instant time, boolean retry) {
    Instant hour = time.truncatedTo(ChronoUnit.HOURS);
    if (retry && hour.equals(lastRetryHour)) {
      lastRetryHour = previousRetryHour;
    } else if (!retry && hour.equals(lastScheduledHour)) {
      lastScheduledHour = previousScheduledHour;
    }
  }
}
