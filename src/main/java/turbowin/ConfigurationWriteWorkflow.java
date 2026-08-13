package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
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
    fill_configuratie_array();

    if (data_dir != null && !data_dir.isEmpty()) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          try {
            writeConfigurationFile(data_dir, configuratie_regels, MAX_AANTAL_CONFIGURATIEREGELS);
          } catch (IOException ex) {
            System.out.println("--- Function schrijf_configuratie_regels(): " + ex);
          }
          return null;
        }
      }.execute();
    }

    if (logs_dir != null && !logs_dir.isEmpty()) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          try {
            writeConfigurationFile(logs_dir, configuratie_regels, MAX_AANTAL_CONFIGURATIEREGELS);
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "unable to write to: " + logs_dir + File.separator + CONFIGURATION_FILE,
                APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
          return null;
        }
      }.execute();
    }
  }
}
