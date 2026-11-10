package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
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

  static String configurationDirectoryForRead(String logsDirectory, String dataDirectory) {
    if (logsDirectory != null
        && !logsDirectory.isEmpty()
        && new File(logsDirectory, CONFIGURATION_FILE).isFile()) {
      return logsDirectory;
    }
    if (dataDirectory != null
        && !dataDirectory.isEmpty()
        && new File(dataDirectory, CONFIGURATION_FILE).isFile()) {
      return dataDirectory;
    }
    return configurationDirectory(logsDirectory, dataDirectory);
  }

  static void read(main owner) {
    read(() -> StartupFinalizationWorkflow.complete(owner), main::log_turbowin_system_message);
  }

  static void read(Runnable completeStartup, Consumer<String> report) {
    final String logsDirectory = logs_dir;
    final String dataDirectory = data_dir;
    main.hulp_dir = configurationDirectoryForRead(logsDirectory, dataDirectory);

    if (!main.hulp_dir.isEmpty()) {
      new SwingWorker<ConfigurationLoader.Result, Void>() {
        @Override
        protected ConfigurationLoader.Result doInBackground() {
          return ConfigurationLoader.load(logsDirectory, dataDirectory);
        }

        @Override
        protected void done() {
          try {
            ConfigurationLoader.Result result = get();
            if (result.loaded()) {
              ConfigurationManager.applyConfiguration(result.settings());
              System.arraycopy(result.lines(), 0, configuratie_regels, 0, result.lines().length);
              main.hulp_dir = result.source().getParent().toString();
            }
            result.messages().forEach(report);
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            report.accept("[CONFIGURATION] loading interrupted; retaining existing settings");
          } catch (ExecutionException ex) {
            report.accept(
                "[CONFIGURATION] unexpected loading failure ("
                    + ex.getCause().getClass().getSimpleName()
                    + "); retaining existing settings");
          } finally {
            completeStartup.run();
          }
        }
      }.execute();
    }
  }
}
