package turbowin;

import java.net.ServerSocket;

/** Coordinates the non-generated startup work performed by the main window. */
final class StartupCoordinator {

  private StartupCoordinator() {}

  static void initialize(
      main owner,
      String jnlpOfflineFile,
      String commandLineOfflineFile,
      String launcherFile,
      String linuxLauncherFile,
      String commandLinePort,
      int defaultPort,
      boolean themeChanged) {
    StartupWindowWorkflow.initialize(owner);

    main.sdf_tsl_1 = StartupLogFormatting.monthlyLogFormat();
    main.sdf_tsl_2 = StartupLogFormatting.messageLogFormat();

    main.Reset_all_meteo_parameters();
    // NB Reset_all_meteo_parameters() sets the status line (JTextField4) to "undefined" because
    // the selection criterion (AWS connected) has not yet been determined.
    main.jTextField4.setText("");

    StartupEnvironmentWorkflow.initialize(
        jnlpOfflineFile, commandLineOfflineFile, launcherFile, linuxLauncherFile);
    // FM13 sends observations to the server only online; format 101 supports that output both
    // online and offline.

    main.mode_grafiek = main.MODE_PRESSURE;
    owner.initializeGraphState();

    StartupCommunicationWorkflow.CommunicationComponents communicationComponents =
        StartupCommunicationWorkflow.initialize();
    owner.initializeCommunicationComponents(communicationComponents);

    main.myship = null;

    if (main.offline_mode_via_cmd) {
      ServerSocket instanceSocket =
          StartupConfigurationWorkflow.initializeCommandLineOffline(
              owner, themeChanged, commandLinePort, defaultPort);
      owner.initializeInstanceSocket(instanceSocket);
    }

    SystemTrayWorkflow.initialize();
  }
}
