package turbowin;

import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Validates maintenance imports in the background, then applies and saves on the EDT. */
final class MaintenanceDataImportWorkflow {

  private MaintenanceDataImportWorkflow() {}

  static void start(mymaintenancedata owner, String source) {
    start(
        source,
        owner::show_data,
        (success, message) ->
            JOptionPane.showMessageDialog(
                null,
                message,
                main.APPLICATION_NAME + (success ? " info" : " error"),
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE),
        main::schrijf_configuratie_regels,
        ConfigurationFileStore::read);
  }

  static void start(
      String source,
      Runnable refresh,
      BiConsumer<Boolean, String> report,
      Runnable save,
      ConfigurationImportReader.Reader reader) {
    new SwingWorker<ConfigurationImportReader.Result, Void>() {
      @Override
      protected ConfigurationImportReader.Result doInBackground() throws Exception {
        return ConfigurationImportReader.read(source, true, false, reader);
      }

      @Override
      protected void done() {
        try {
          get().apply();
          save.run();
          report.accept(true, "Maintenance data successfully imported from: " + source);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          report.accept(false, "Maintenance data import interrupted; settings unchanged");
        } catch (ExecutionException ex) {
          report.accept(
              false,
              "Unable to import maintenance data from '"
                  + source
                  + "': "
                  + ex.getCause().getMessage()
                  + "; settings unchanged");
        } finally {
          // Always display the operational settings, including after a rejected import.
          refresh.run();
        }
      }
    }.execute();
  }
}
