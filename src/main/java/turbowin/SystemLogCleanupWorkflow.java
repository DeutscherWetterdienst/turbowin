package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.SwingWorker;

/** Owns asynchronous cleanup of older TurboWin system log files. */
final class SystemLogCleanupWorkflow {

  private SystemLogCleanupWorkflow() {}

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

          String file_naam =
              "turbowin_system_" + main.sdf_tsl_1.format(cal_delete_datum.getTime()) + ".txt";
          String volledig_path_turbowin_system_logs =
              main.logs_dir
                  + java.io.File.separator
                  + main.TURBOWIN_SYSTEM_LOGS_DIR
                  + java.io.File.separator
                  + file_naam;

          File file_log_data = new File(volledig_path_turbowin_system_logs);
          if (file_log_data.exists()) {
            file_log_data.delete();
          }
        }

        return null;
      }
    }.execute();
  }
}
