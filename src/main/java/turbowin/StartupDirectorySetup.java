package turbowin;

import static turbowin.main.*;

import java.io.File;
import javax.swing.JOptionPane;

/**
 * Calculates and initializes the fixed offline log and AMVER directories.
 *
 * <p>In offline mode, these directories are always subdirectories of the application's main
 * directory (the directory containing the installed JAR) and are intentionally not
 * user-configurable.
 */
final class StartupDirectorySetup {

  private StartupDirectorySetup() {}

  static Directories directories(String dataDirectory) {
    return new Directories(
        dataDirectory + File.separator + OFFLINE_LOGS_DIR,
        dataDirectory + File.separator + OFFLINE_AMVER_DIR);
  }

  static void initialize(Directories directories) {
    main.logs_dir = directories.logsDirectory;
    File logsDirectory = new File(directories.logsDirectory);
    if (!logsDirectory.exists()) {
      boolean success = logsDirectory.mkdirs();
      if (!success) {
        JOptionPane.showMessageDialog(
            null,
            "Could not create "
                + directories.logsDirectory
                + ", disk write protected or no permission to write",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
    }
    if (logsDirectory.isDirectory()) {
      File systemLogsDirectory =
          new File(directories.logsDirectory + File.separator + TURBOWIN_SYSTEM_LOGS_DIR);
      if (!systemLogsDirectory.exists()) {
        systemLogsDirectory.mkdir();
        log_turbowin_system_message("[GENERAL] created dir " + systemLogsDirectory);
      }
    }

    File amverDirectory = new File(directories.amverDirectory);
    if (!amverDirectory.exists()) {
      boolean success = amverDirectory.mkdirs();
      if (!success) {
        JOptionPane.showMessageDialog(
            null,
            "Could not create "
                + directories.amverDirectory
                + ", disk write protected or no permission to write",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
    }
  }

  static final class Directories {
    final String logsDirectory;
    final String amverDirectory;

    private Directories(String logsDirectory, String amverDirectory) {
      this.logsDirectory = logsDirectory;
      this.amverDirectory = amverDirectory;
    }
  }
}
