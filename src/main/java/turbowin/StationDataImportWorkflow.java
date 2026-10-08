package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.SwingWorker;

/** Owns asynchronous import of station data from a configuration file. */
final class StationDataImportWorkflow {

  private StationDataImportWorkflow() {}

  static void start(String importFile, Runnable updateStationData) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        int teller;
        String file_line;

        // String volledig_path = hulp_dir + java.io.File.separator + CONFIGURATION_FILE;
        String volledig_path = importFile;

        for (teller = 0; teller < main.MAX_AANTAL_CONFIGURATIEREGELS; teller++) {
          main.configuratie_regels[teller] = "";
        }

        /* read all lines from configuration file */
        try (BufferedReader in = new BufferedReader(new FileReader(volledig_path))) {
          teller = 0;
          while ((file_line = in.readLine()) != null) {
            // do not immport the log dir! (because log dir could be changed after a new
            // install, mainly problems in standalone mode -> fixed logs dir)
            if (((file_line.indexOf(main.LOGS_DIR_TXT) == -1) && (main.offline_mode == true))
                || (main.offline_mode == false)) {
              main.configuratie_regels[teller] = file_line;
            }

            teller++;

            /* for safety */
            if (teller >= main.MAX_AANTAL_CONFIGURATIEREGELS) {
              break;
            }
          } // while((file_line = in.readLine()) != null)
        } // try
        catch (IOException ex) {
          // JOptionPane.showMessageDialog(null, "Error reading file configuration.txt",
          // main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          String info = "[GENERAL] error reading file 'configuration.txt' (" + ex + ")";
          main.log_turbowin_system_message(info);
        } // catch

        /* put collected meta data from configuration file into appropriate global vars */
        main.meta_data_from_configuration_regels_into_global_vars();

        /* and put global vars into this station data form */
        updateStationData.run();

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        /* niets */
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
