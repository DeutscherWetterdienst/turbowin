package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/** Queues configuration snapshots for ordered background writes to both directories. */
final class ConfigurationWriteWorkflow {

  private static SaveAttempt latestSave = new SaveAttempt();
  private static volatile boolean exitPending;
  private static volatile long exitGeneration;

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
    final SaveAttempt attempt = new SaveAttempt();
    latestSave = attempt;

    if ((dataDirectory == null || dataDirectory.isEmpty())
        && (logsDirectory == null || logsDirectory.isEmpty())) {
      attempt.failures.add("No configuration directory is configured.");
    }

    if (dataDirectory != null && !dataDirectory.isEmpty()) {
      startWorker.accept(
          new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
              try {
                writer.write(dataDirectory, lines, MAX_AANTAL_CONFIGURATIEREGELS);
              } catch (IOException | RuntimeException ex) {
                attempt.failed(dataDirectory, ex);
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
              } catch (IOException | RuntimeException ex) {
                attempt.failed(logsDirectory, ex);
                failed = true;
              }
              return null;
            }

            @Override
            protected void done() {
              // Report on the EDT so a modal warning cannot stall the save queue.
              if (failed
                  && !exitPending
                  && !attempt.exitSave
                  && attempt.exitGeneration == exitGeneration) {
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

  static void saveBeforeExit(Consumer<SaveResult> completed) {
    saveBeforeExit(completed, ConfigurationWriteWorkflow::writeConfigurationFile);
  }

  static synchronized void saveBeforeExit(
      Consumer<SaveResult> completed, ConfigurationWriter writer) {
    exitPending = true;
    exitGeneration++;
    try {
      write(writer);
    } catch (RuntimeException ex) {
      SaveAttempt failed = new SaveAttempt();
      failed.failures.add("Unable to prepare settings (" + ex.getClass().getSimpleName() + ")");
      latestSave = failed;
    }
    awaitPendingSaves(completed);
  }

  static void cancelExit() {
    exitPending = false;
  }

  /** Calls back on the EDT after queued saves, including saves submitted while waiting, finish. */
  static synchronized void awaitPendingSaves(Consumer<SaveResult> completed) {
    SaveAttempt observed = latestSave;
    SAVE_QUEUE.execute(
        () -> SwingUtilities.invokeLater(() -> completeWhenCurrent(observed, completed)));
  }

  private static synchronized void completeWhenCurrent(
      SaveAttempt observed, Consumer<SaveResult> completed) {
    if (latestSave != observed) {
      awaitPendingSaves(completed);
    } else {
      completed.accept(observed.result());
    }
  }

  record SaveResult(List<String> failures) {
    SaveResult {
      failures = List.copyOf(failures);
    }

    boolean successful() {
      return failures.isEmpty();
    }
  }

  private static final class SaveAttempt {
    private final List<String> failures = new ArrayList<>();
    private final boolean exitSave = exitPending;
    private final long exitGeneration = ConfigurationWriteWorkflow.exitGeneration;

    synchronized void failed(String directory, Exception error) {
      failures.add(
          new File(directory, CONFIGURATION_FILE) + " (" + error.getClass().getSimpleName() + ")");
    }

    synchronized SaveResult result() {
      return new SaveResult(failures);
    }
  }

  @FunctionalInterface
  interface ConfigurationWriter {
    void write(String directory, String[] lines, int maximumLines) throws IOException;
  }
}
