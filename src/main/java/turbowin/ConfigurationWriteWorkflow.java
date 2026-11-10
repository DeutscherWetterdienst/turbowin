package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Queues configuration snapshots for ordered background writes to both directories. */
final class ConfigurationWriteWorkflow {

  private static final ExecutorService SAVE_QUEUE =
      Executors.newSingleThreadExecutor(
          task -> {
            Thread thread = new Thread(task, "configuration-save");
            thread.setDaemon(true);
            return thread;
          });

  private ConfigurationWriteWorkflow() {}

  static void writeConfigurationFile(String directory, String[] lines, int maximumLines)
      throws IOException {
    ConfigurationFileStore.write(
        new File(directory + File.separator + CONFIGURATION_FILE), lines, maximumLines);
  }

  static void write() {
    write(ConfigurationWriteWorkflow::writeConfigurationFile);
  }

  static void write(ConfigurationWriter writer) {
    write(SAVE_QUEUE::execute, writer);
  }

  static void write(Consumer<SwingWorker<Void, Void>> startWorker) {
    write(startWorker, ConfigurationWriteWorkflow::writeConfigurationFile);
  }

  private static synchronized void write(
      Consumer<SwingWorker<Void, Void>> startWorker, ConfigurationWriter writer) {
    // Keep snapshot capture and both submissions together, including for concurrent callers.
    fill_configuratie_array();
    final String[] lines = configuratie_regels.clone();
    final String dataDirectory = data_dir;
    final String logsDirectory = logs_dir;

    if (dataDirectory != null && !dataDirectory.isEmpty()) {
      startWorker.accept(
          new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
              try {
                writer.write(dataDirectory, lines, MAX_AANTAL_CONFIGURATIEREGELS);
              } catch (IOException ex) {
                System.out.println("--- Function schrijf_configuratie_regels(): " + ex);
              }
              return null;
            }
          });
    }

    if (logsDirectory != null && !logsDirectory.isEmpty()) {
      startWorker.accept(
          new SwingWorker<Void, Void>() {
            private boolean failed;

            @Override
            protected Void doInBackground() throws Exception {
              try {
                writer.write(logsDirectory, lines, MAX_AANTAL_CONFIGURATIEREGELS);
              } catch (IOException ex) {
                failed = true;
              }
              return null;
            }

            @Override
            protected void done() {
              // Report on the EDT so a modal warning cannot stall the save queue.
              if (failed) {
                JOptionPane.showMessageDialog(
                    null,
                    "unable to write to: " + logsDirectory + File.separator + CONFIGURATION_FILE,
                    APPLICATION_NAME + " error",
                    JOptionPane.WARNING_MESSAGE);
              }
            }
          });
    }
  }

  @FunctionalInterface
  interface ConfigurationWriter {
    void write(String directory, String[] lines, int maximumLines) throws IOException;
  }
}
