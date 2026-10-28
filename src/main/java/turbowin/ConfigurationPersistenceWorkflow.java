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
   * <p>The files retain the legacy configuration format. The writes overwrite existing files and
   * are not an atomic transaction; returning from this method does not mean saving has finished.
   */
  static void write() {
    ConfigurationWriteWorkflow.write();
  }

  /**
   * Loads configuration.txt asynchronously and schedules startup finalization.
   *
   * <p>The reader prefers an existing file in logs_dir, then one in data_dir. If neither file
   * exists, it selects the first non-empty directory in the same order. An I/O error while reading
   * the selected file does not trigger a retry from the other directory.
   *
   * <p>Reading clears the line array before opening the file, then applies recognized settings to
   * the existing global variables. Missing settings do not reset those variables. If both directory
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
