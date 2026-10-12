package turbowin;

/** Coordinates startup environment detection and initialization. */
final class StartupEnvironmentWorkflow {

  private StartupEnvironmentWorkflow() {}

  static void initialize(
      String jnlpOfflineFile,
      String commandLineOfflineFile,
      String launcherFile,
      String linuxLauncherFile) {
    String os = OSDetector.getOSString();
    // Tests can use a writable sandbox without changing the installed platform defaults.
    main.data_dir = System.getProperty("turbowin.data.dir", StartupEnvironment.dataDirectory(os));
    System.out.println("data dir = " + main.data_dir);

    // JNLP BasicService.isOffline() is not guaranteed to report the true client state; use the
    // file markers instead. FM13 server output is online-only, while format 101 supports both.
    StartupEnvironment.OfflineMode startupMode =
        StartupEnvironment.detectOfflineMode(
            main.data_dir,
            jnlpOfflineFile,
            commandLineOfflineFile,
            launcherFile,
            linuxLauncherFile);
    main.offline_mode = startupMode.offline;
    main.offline_mode_via_jnlp = startupMode.viaJnlp;
    main.offline_mode_via_cmd = startupMode.viaCommandLine;

    if (main.offline_mode) {
      StartupDirectorySetup.initialize(StartupDirectorySetup.directories(main.data_dir));
    }
    main.application_mode = ApplicationStartupWorkflow.applicationMode(main.offline_mode);
    main.jLabel4.setText(applicationLabel(main.APPLICATION_NAME, main.offline_mode));
  }

  static String applicationLabel(String applicationName, boolean offline) {
    return applicationName + " " + ApplicationStartupWorkflow.applicationMode(offline);
  }
}
