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
   * Loads data_dir/configuration.txt or its previous-valid backup, validating before applying on
   * the EDT. If neither is usable, existing settings are preserved. Backup recovery notifies the
   * user. Startup finalizes after loading when a data directory is configured.
   */
  static void read(main owner) {
    ConfigurationReadWorkflow.read(owner);
  }
}
