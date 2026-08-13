package turbowin;

/** Compatibility facade for asynchronous configuration persistence operations. */
final class ConfigurationPersistenceWorkflow {

  private ConfigurationPersistenceWorkflow() {}

  static String configurationDirectory(String logsDirectory, String dataDirectory) {
    return ConfigurationReadWorkflow.configurationDirectory(logsDirectory, dataDirectory);
  }

  /**
   * Writes configuration.txt as a backup for the muffin/persistent configuration in both the
   * system-defined data_dir and the user-defined logs_dir.
   */
  static void write() {
    ConfigurationWriteWorkflow.write();
  }

  /**
   * Reads configuration.txt when muffin/persistent configuration reading is unavailable. The
   * user-defined logs_dir is preferred in online mode (and is a fixed offline subdirectory), with
   * the system-defined data_dir as the fallback.
   */
  static void read(main owner) {
    ConfigurationReadWorkflow.read(owner);
  }
}
