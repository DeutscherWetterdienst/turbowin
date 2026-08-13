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
    main.data_dir = StartupEnvironment.dataDirectory(os);
    System.out.println("data dir = " + main.data_dir);

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
