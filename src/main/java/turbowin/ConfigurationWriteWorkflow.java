package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Writes configuration data asynchronously to the data and logs directories. */
final class ConfigurationWriteWorkflow {

  private ConfigurationWriteWorkflow() {}

  static void writeConfigurationFile(String directory, String[] lines, int maximumLines)
      throws IOException {
    ConfigurationFileStore.write(
        new File(directory + File.separator + CONFIGURATION_FILE), lines, maximumLines);
  }

  static void write() {
    write(SwingWorker::execute);
  }

  static void write(Consumer<SwingWorker<Void, Void>> startWorker) {
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
                writeConfigurationFile(dataDirectory, lines, MAX_AANTAL_CONFIGURATIEREGELS);
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
            @Override
            protected Void doInBackground() throws Exception {
              try {
                writeConfigurationFile(logsDirectory, lines, MAX_AANTAL_CONFIGURATIEREGELS);
              } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    "unable to write to: " + logsDirectory + File.separator + CONFIGURATION_FILE,
                    APPLICATION_NAME + " error",
                    JOptionPane.WARNING_MESSAGE);
              }
              return null;
            }
          });
    }
  }
}
