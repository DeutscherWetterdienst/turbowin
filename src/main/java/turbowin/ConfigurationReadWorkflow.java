package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;

/** Loads configuration asynchronously and finalizes application startup. */
final class ConfigurationReadWorkflow {

  private ConfigurationReadWorkflow() {}

  static String configurationDirectory(String logsDirectory, String dataDirectory) {
    if (logsDirectory != null && !logsDirectory.isEmpty()) {
      return logsDirectory;
    }
    if (dataDirectory != null && !dataDirectory.isEmpty()) {
      return dataDirectory;
    }
    return "";
  }

  static void read(main owner) {
    main.hulp_dir = configurationDirectory(logs_dir, data_dir);

    if (!main.hulp_dir.isEmpty()) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          String path = main.hulp_dir + File.separator + CONFIGURATION_FILE;
          try {
            ConfigurationFileStore.read(
                new File(path), configuratie_regels, MAX_AANTAL_CONFIGURATIEREGELS);
          } catch (IOException e) {
            // The configuration file may not exist on first use.
          }
          meta_data_from_configuration_regels_into_global_vars();
          return null;
        }

        @Override
        protected void done() {
          StartupFinalizationWorkflow.complete(owner);
        }
      }.execute();
    }
  }
}
