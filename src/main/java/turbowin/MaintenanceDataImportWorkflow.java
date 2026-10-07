package turbowin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous importing of maintenance data. */
final class MaintenanceDataImportWorkflow {

  private MaintenanceDataImportWorkflow() {}

  static void start(mymaintenancedata owner, String maintenanceDataImportFile) {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        String file_line = null;
        int teller;
        String import_maintenance_data_ok = "OK";
        boolean ship_name_txt_present = false;
        boolean call_sign_txt_present = false;

        // read all lines from imported maintenance data (eg
        // TurboWin_configuration_Happy_Sailor.txt) file
        try (BufferedReader in = new BufferedReader(new FileReader(maintenanceDataImportFile))) {
          teller = 0;

          // at least ship name and call sign must be present in the import file
          while ((file_line = in.readLine()) != null) {
            // System.out.println("+++ 1] " + file_line);

            // NB e.g. configuratie_regels[2]  = "wind source        : estimated; true speed and
            // true direction"
            // read all lines from import file and check for presence of ship name and call sign
            if (file_line.indexOf(main.SHIP_NAME_TXT) != -1) {
              ship_name_txt_present = true;
            }
            if (file_line.indexOf(main.CALL_SIGN_TXT) != -1) {
              call_sign_txt_present = true;
            }

            teller++;

            // for safety
            if (teller >= main.MAX_AANTAL_CONFIGURATIEREGELS) {
              break;
            }
          } // while((file_line = in.readLine()) != null)
        } // try
        catch (IOException ex) {
          import_maintenance_data_ok =
              "Error reading import file (" + maintenanceDataImportFile + ")";
        } // catch

        // continue if ship name text string and call sign text string are present (these are not
        // the name and call sign itself but the TEXT entrance ("ship name          :")
        if (ship_name_txt_present && call_sign_txt_present) {
          // initialisation
          for (teller = 0; teller < main.MAX_AANTAL_CONFIGURATIEREGELS; teller++) {
            main.configuratie_regels[teller] = "";
          }

          // reset reading!!
          try (BufferedReader in2 = new BufferedReader(new FileReader(maintenanceDataImportFile))) {
            teller = 0;
            while ((file_line = in2.readLine()) != null) {
              // System.out.println("+++ 2] " + file_line);

              // NB e.g. configuratie_regels[2]  = "wind source        : estimated; true speed and
              // true direction"
              // read all lines from configuration file
              main.configuratie_regels[teller] = file_line;
              teller++;

              // for safety
              if (teller >= main.MAX_AANTAL_CONFIGURATIEREGELS) {
                break;
              }
            } // while((file_line = in.readLine()) != null)
          } catch (IOException ex) {
            import_maintenance_data_ok =
                "Error reading import file (" + maintenanceDataImportFile + ")";
          } // catch

          // put collected meta data from configuration file into appropriate global vars
          main.meta_data_from_configuration_regels_into_global_vars();

          // write meta (station) data to muffins or configuration files
          main.schrijf_configuratie_regels();

          // user feedback string
          import_maintenance_data_ok =
              "OK, maintenance data successfully importend from: " + maintenanceDataImportFile;

        } //    if (ship_name_present && call_sign_present)
        else {
          import_maintenance_data_ok =
              "Invalid format import file (" + maintenanceDataImportFile + ")";
        }

        return import_maintenance_data_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          String result_import_maintenance_data_ok = get();

          if (result_import_maintenance_data_ok.contains("OK") == false) {
            // show error message
            JOptionPane.showMessageDialog(
                null,
                result_import_maintenance_data_ok,
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          } else {
            // show the imported results
            JOptionPane.showMessageDialog(
                null,
                result_import_maintenance_data_ok,
                main.APPLICATION_NAME + " info",
                JOptionPane.INFORMATION_MESSAGE);
          }
        } catch (InterruptedException | ExecutionException ex) {
          // show error message
          JOptionPane.showMessageDialog(
              null,
              "Error reading import file (" + ex + ")",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }

        // always show the latest 'operational' maintenance data (never mind the import succeeded)
        owner.show_data();
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
