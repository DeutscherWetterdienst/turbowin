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

  /** Queues an immutable snapshot for an atomic save to data_dir/configuration.txt. */
  static void write() {
    ConfigurationWriteWorkflow.write();
  }

  /**
   * Loads only data_dir/configuration.txt, validating settings before applying them on the EDT.
   * Missing or invalid configuration preserves existing settings. Startup finalizes after loading
   * when a data directory is configured.
   */
  static void read(main owner) {
    ConfigurationReadWorkflow.read(owner);
  }
}
