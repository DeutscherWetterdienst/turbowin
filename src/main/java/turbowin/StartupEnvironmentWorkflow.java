package turbowin;

import java.nio.file.Path;

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
    String configuredDataDirectory = System.getProperty("turbowin.data.dir");
    main.data_dir =
        configuredDataDirectory != null
            ? configuredDataDirectory
            : StartupEnvironment.dataDirectory(os);
    if (configuredDataDirectory == null) {
      LegacyDataMigration.migrate(LegacyInstallationLocator.find(os), Path.of(main.data_dir));
    }
    System.out.println("data dir = " + main.data_dir);

    // Startup currently selects installed desktop mode; marker flags remain for compatibility.
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
