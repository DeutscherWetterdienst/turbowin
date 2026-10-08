package turbowin;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/** Owns asynchronous loading of ISO-country codes for the station-data form. */
final class StationDataIsoCountriesWorkflow {

  private StationDataIsoCountriesWorkflow() {}

  static void start(mystationdata owner) {
    new SwingWorker<String[], Void>() {
      @Override
      protected String[] doInBackground() throws Exception {
        String[] records;
        try (InputStream is =
            owner.getClass().getResourceAsStream(main.ICONS_DIRECTORY + "ISO_landen_codes.txt")) {
          records = IsoCountryCodeReader.read(is, mystationdata.MAX_AANTAL_ISO_LANDEN_REGELS);
        } catch (IOException ex) {
          String info = "[GENERAL] reading error 'ISO country codes' file (" + ex + ")";
          main.log_turbowin_system_message(info);
          records = owner.getIsoCountryRecords();
        }
        return records;
      }

      @Override
      protected void done() {
        try {
          owner.updateIsoCountryList(get());
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          owner.updateIsoCountryList(owner.getIsoCountryRecords());
        } catch (ExecutionException ex) {
          owner.updateIsoCountryList(owner.getIsoCountryRecords());
        }
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
