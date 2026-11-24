package turbowin;

import static turbowin.main.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import javax.swing.SwingWorker;

/** Owns asynchronous writing of TurboWin system log messages. */
final class SystemLogWriterWorkflow {

  private SystemLogWriterWorkflow() {}

  static String logFileName(String month) {
    return "turbowin_system_" + month + ".txt";
  }

  static String logFilePath(String logsDirectory, String systemLogsDirectory, String month) {
    return logsDirectory
        + File.separator
        + systemLogsDirectory
        + File.separator
        + logFileName(month);
  }

  static String logLine(String timestamp, String message) {
    java.util.Objects.requireNonNull(message);
    return timestamp + " UTC " + message;
  }

  static void append(String logsDirectory, String month, String line) throws IOException {
    if (logsDirectory == null || logsDirectory.isBlank()) {
      throw new IOException("Log folder is not configured");
    }
    Path logs = Path.of(logsDirectory);
    if (!Files.isDirectory(logs)) throw new IOException("Log folder is unavailable: " + logs);
    Path systemLogs = logs.resolve(main.TURBOWIN_SYSTEM_LOGS_DIR);
    Files.createDirectories(systemLogs);
    try (BufferedWriter out =
        new BufferedWriter(new FileWriter(systemLogs.resolve(logFileName(month)).toFile(), true))) {
      out.write(line);
      out.newLine();
    }
  }

  static void start(String message) {
    // The console receives the raw message; the file uses a UTC timestamp and the format
    // turbowin_system_MMM_yyyy.txt for its monthly log file.
    System.out.println(message);

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String month = main.sdf_tsl_1.format(new Date());
        try {
          append(main.logs_dir, month, logLine(main.sdf_tsl_2.format(new Date()), message));
        } catch (IOException ex) {
          System.out.println("+++ Unable to write system log: " + ex);
        }

        return null;
      }
    }.execute();
  }
}
