package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Loads configuration asynchronously and finalizes application startup. */
final class ConfigurationReadWorkflow {

  private ConfigurationReadWorkflow() {}

  static String configurationDirectory(String logsDirectory, String dataDirectory) {
    return dataDirectory == null ? "" : dataDirectory;
  }

  static void read(main owner) {
    read(
        () -> StartupFinalizationWorkflow.complete(owner),
        main::log_turbowin_system_message,
        message ->
            JOptionPane.showMessageDialog(
                owner,
                message,
                APPLICATION_NAME + " configuration recovery",
                JOptionPane.WARNING_MESSAGE));
  }

  static void read(Runnable completeStartup, Consumer<String> report) {
    read(completeStartup, report, report);
  }

  static void read(
      Runnable completeStartup, Consumer<String> report, Consumer<String> notifyRecovery) {
    final String dataDirectory = data_dir;
    main.hulp_dir = configurationDirectory(null, dataDirectory);

    if (!main.hulp_dir.isEmpty()) {
      new SwingWorker<ConfigurationLoader.Result, Void>() {
        @Override
        protected ConfigurationLoader.Result doInBackground() {
          return ConfigurationLoader.load(dataDirectory);
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
            if (result.recovered()) {
              notifyRecovery.accept(
                  "Settings were recovered from configuration.txt.bak because "
                      + "the active configuration was missing or invalid. The next save will repair "
                      + "the active file and preserve any damaged contents for inspection.");
            }
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
