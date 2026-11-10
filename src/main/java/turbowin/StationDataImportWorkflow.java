package turbowin;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Validates station imports in the background, then saves and refreshes on the EDT. */
final class StationDataImportWorkflow {

  private StationDataImportWorkflow() {}

  static void start(String source, Runnable updateStationData) {
    start(
        source,
        updateStationData,
        main::schrijf_configuratie_regels,
        main::log_turbowin_system_message,
        ConfigurationFileStore::read);
  }

  static void start(
      String source,
      Runnable updateStationData,
      Runnable save,
      Consumer<String> report,
      ConfigurationImportReader.Reader reader) {
    final boolean preserveLogsDirectory = main.offline_mode;
    new SwingWorker<ConfigurationImportReader.Result, Void>() {
      @Override
      protected ConfigurationImportReader.Result doInBackground() throws Exception {
        return ConfigurationImportReader.read(source, false, preserveLogsDirectory, reader);
      }

      @Override
      protected void done() {
        try {
          get().apply();
          // The source may itself be one of the files being saved.
          save.run();
          updateStationData.run();
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          report.accept("[GENERAL] station data import interrupted; settings unchanged");
        } catch (ExecutionException ex) {
          report.accept(
              "[GENERAL] unable to import station data from '"
                  + source
                  + "': "
                  + ex.getCause().getMessage()
                  + "; settings unchanged");
        }
      }
    }.execute();
  }
}
