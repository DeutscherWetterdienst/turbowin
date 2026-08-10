package turbowin;

import static turbowin.main.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.swing.SwingWorker;

/** Owns asynchronous writing of TurboWin system log messages. */
final class SystemLogWriterWorkflow {

  private SystemLogWriterWorkflow() {}

  static void start(String message) {
    // The console receives the raw message; the file uses a UTC timestamp and the format
    // turbowin_system_MMM_yyyy.txt for its monthly log file.
    System.out.println(message);

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String file_naam = "turbowin_system_" + main.sdf_tsl_1.format(new Date()) + ".txt";
        String volledig_path_turbowin_system_logs =
            main.logs_dir
                + java.io.File.separator
                + main.TURBOWIN_SYSTEM_LOGS_DIR
                + java.io.File.separator
                + file_naam;

        try (BufferedWriter out =
            new BufferedWriter(new FileWriter(volledig_path_turbowin_system_logs, true))) {
          out.write(main.sdf_tsl_2.format(new Date()) + " UTC ");
          out.write(message);
          out.newLine();
        } catch (IOException ex) {
          System.out.println("+++ " + ex + "; trying to create the logs folder");
          // Create the system-log subdirectory after a write failure so a later write can succeed.

          if ((logs_dir != null) && (logs_dir.compareTo("") != 0)) {
            File f = new File(logs_dir);
            if (f.exists() && f.isDirectory()) {
              String turbowin_system_logs_dir =
                  main.logs_dir + java.io.File.separator + main.TURBOWIN_SYSTEM_LOGS_DIR;
              final File dir_turbowin_system_logs = new File(turbowin_system_logs_dir);

              if (dir_turbowin_system_logs.exists() == false) {
                dir_turbowin_system_logs.mkdir();
              }
            }
          }
        }

        return null;
      }
    }.execute();
  }
}
