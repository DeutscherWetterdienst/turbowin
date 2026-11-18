package turbowin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/** Formats log backup dates using the legacy filename format. */
final class LogFileBackupDate {

  private LogFileBackupDate() {}

  static String format(Calendar calendar) {
    return new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(calendar.getTime());
  }
}
