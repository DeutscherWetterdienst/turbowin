package turbowin;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/** Formats log backup dates using the legacy filename format. */
final class LogFileBackupDate {

  private LogFileBackupDate() {}

  static String format(Calendar calendar) {
    return new SimpleDateFormat("MMMM dd, yyyy").format(calendar.getTime());
  }
}
