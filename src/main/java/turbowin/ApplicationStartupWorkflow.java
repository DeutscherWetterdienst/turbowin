package turbowin;

/** Handles the remaining application-mode startup decisions. */
final class ApplicationStartupWorkflow {

  private ApplicationStartupWorkflow() {}

  static String applicationMode(boolean offline) {
    // Intentionally blank initially; it may later be overwritten when a barometer or AWS is
    // coupled.
    return offline ? "" : "web mode";
  }

  static void loadConfiguration(main owner, boolean offlineViaCommandLine) {
    if (offlineViaCommandLine) {
      owner.lees_configuratie_regels();
    }
  }
}
