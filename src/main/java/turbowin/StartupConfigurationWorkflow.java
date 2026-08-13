package turbowin;

import java.net.ServerSocket;

/** Loads startup configuration and enforces the command-line offline instance check. */
final class StartupConfigurationWorkflow {

  private StartupConfigurationWorkflow() {}

  static ServerSocket initializeCommandLineOffline(
      main owner, boolean themeChanged, String commandLinePort, int defaultPort) {
    ServerSocket instanceSocket = null;
    if (!themeChanged) {
      instanceSocket = OfflineStartupWorkflow.openInstanceCheck(commandLinePort, defaultPort);
    }
    ApplicationStartupWorkflow.loadConfiguration(owner, true);
    return instanceSocket;
  }
}
