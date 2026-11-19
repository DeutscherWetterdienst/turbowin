package turbowin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Claims each scheduled WOW minute once, even when sensor records arrive more frequently. */
final class WowUploadGate {
  private Instant lastMinute;

  synchronized boolean claim(Instant time) {
    Instant minute = time.truncatedTo(ChronoUnit.MINUTES);
    if (lastMinute != null && !minute.isAfter(lastMinute)) {
      return false;
    }
    lastMinute = minute;
    return true;
  }
}
