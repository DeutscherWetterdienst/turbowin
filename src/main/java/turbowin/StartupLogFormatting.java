package turbowin;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/** Creates the UTC date formats used by TurboWin system logs. */
final class StartupLogFormatting {

  private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

  private StartupLogFormatting() {}

  static SimpleDateFormat monthlyLogFormat() {
    return format("MMM_yyyy");
  }

  static SimpleDateFormat messageLogFormat() {
    return format("dd-MMM-yyyy HH:mm:ss");
  }

  private static SimpleDateFormat format(String pattern) {
    SimpleDateFormat format = new SimpleDateFormat(pattern);
    format.setTimeZone(UTC);
    return format;
  }
}
