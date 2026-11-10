package turbowin;

/**
 * Compatibility facade for asynchronous configuration persistence operations.
 *
 * <p>The installed desktop application persists settings in {@code configuration.txt}. Java Web
 * Start's historical "muffin" storage is no longer used. Settings are represented as labeled,
 * fixed-width text lines; the writer places the wind-source line at {@code configuratie_regels[7]},
 * but the reader identifies settings by their labels rather than their array positions.
 */
final class ConfigurationPersistenceWorkflow {

  private ConfigurationPersistenceWorkflow() {}

  static String configurationDirectory(String logsDirectory, String dataDirectory) {
    return ConfigurationReadWorkflow.configurationDirectory(logsDirectory, dataDirectory);
  }

  /**
   * Builds configuration lines from the current global settings and starts separate background
   * writes of configuration.txt to each non-empty data_dir and logs_dir.
   *
   * <p>Both background writes use the same snapshot of lines and destination paths. Each file is
   * replaced atomically, retaining the legacy format. The two copies are not a single transaction;
   * returning from this method does not mean saving has finished.
   */
  static void write() {
    ConfigurationWriteWorkflow.write();
  }

  /**
   * Loads configuration.txt asynchronously and schedules startup finalization.
   *
   * <p>The reader tries logs_dir, then data_dir if the preferred copy is missing, unreadable,
   * malformed, or contains no recognized settings. Values are parsed into temporary settings before
   * being applied on the EDT. Diagnostics identify the selected copy and recovery reasons.
   *
   * <p>Missing settings in a valid partial file preserve existing values. If neither copy is
   * usable, existing settings and lines are retained and startup still finalizes. If both directory
   * names are empty, no read or startup finalization is scheduled.
   *
   * <p>After the configuration has been read and converted into the global configuration variables,
   * the read workflow completes the startup sequence. This includes startup logging and cleanup,
   * station metadata and UI initialization, connection initialization, and updating menu and
   * toolbar availability according to the loaded configuration.
   */
  static void read(main owner) {
    ConfigurationReadWorkflow.read(owner);
  }
}
