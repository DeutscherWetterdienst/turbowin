package turbowin;

import java.io.File;

/** Calculates startup paths and offline-mode flags without touching application state. */
final class StartupEnvironment {

  private StartupEnvironment() {}

  static String dataDirectory(String os) {
    if (os.equals("WINDOWS")) {
      return "C:" + File.separator + "ProgramData" + File.separator + "TurboWinPlus";
    }
    return File.separator + "opt" + File.separator + "turbowinplus" + File.separator + "data";
  }

  static OfflineMode detectOfflineMode(
      String dataDirectory,
      String jnlpOfflineFile,
      String commandLineOfflineFile,
      String launcherFile,
      String linuxLauncherFile) {
    File dataDirectoryFile = new File(dataDirectory);
    // The offline JNLP marker is present for the supplied offline JNLP route and absent in
    // TurboWeb's online deployment.
    boolean viaJnlp = new File(dataDirectoryFile, jnlpOfflineFile).exists();
    // Preserve the legacy default: the command-line startup route is selected when no marker is
    // present, and the command-line or launcher markers explicitly select the same route.
    boolean viaCommandLine = true;
    if (new File(dataDirectoryFile, commandLineOfflineFile).exists()
        || new File(dataDirectoryFile, launcherFile).exists()
        || new File(dataDirectoryFile, linuxLauncherFile).exists()) {
      viaCommandLine = true;
    }
    return new OfflineMode(true, viaJnlp, viaCommandLine);
  }

  static final class OfflineMode {
    final boolean offline;
    final boolean viaJnlp;
    final boolean viaCommandLine;

    private OfflineMode(boolean offline, boolean viaJnlp, boolean viaCommandLine) {
      this.offline = offline;
      this.viaJnlp = viaJnlp;
      this.viaCommandLine = viaCommandLine;
    }
  }
}
