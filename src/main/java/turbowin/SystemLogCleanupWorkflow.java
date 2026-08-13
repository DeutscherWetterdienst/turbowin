package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.SwingWorker;

/** Owns asynchronous cleanup of older TurboWin system log files. */
final class SystemLogCleanupWorkflow {

  private SystemLogCleanupWorkflow() {}

  static void deleteLogFile(
      String logsDirectory, String systemLogsDirectory, SimpleDateFormat monthlyFormat, Date date) {
    File logFile =
        new File(
            SystemLogWriterWorkflow.logFilePath(
                logsDirectory, systemLogsDirectory, monthlyFormat.format(date)));
    if (logFile.exists()) {
      logFile.delete();
    }
  }

  static void start() {
    new SwingWorker<Void, Void>() {
      GregorianCalendar cal_delete_datum;

      @Override
      protected Void doInBackground() throws Exception {
        // System log filenames use the UTC monthly format "turbowin_system_MMM_yyyy.txt".
        // Delete log files that are three months old and older.
        for (int i = 3; i <= 12; i++) {
          cal_delete_datum = new GregorianCalendar();
          cal_delete_datum.add(Calendar.MONTH, -i);

          deleteLogFile(
              main.logs_dir,
              main.TURBOWIN_SYSTEM_LOGS_DIR,
              main.sdf_tsl_1,
              cal_delete_datum.getTime());
        }

        return null;
      }
    }.execute();
  }
}
