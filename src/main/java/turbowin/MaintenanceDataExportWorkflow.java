package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous exporting of maintenance data. */
final class MaintenanceDataExportWorkflow {

  private MaintenanceDataExportWorkflow() {}

  static void start(mymaintenancedata owner, String maintenanceDataExportFile) {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        String export_maintenance_data_ok = "OK";

        try (BufferedWriter out = new BufferedWriter(new FileWriter(maintenanceDataExportFile))) {
          for (int i = 0; i < main.MAX_AANTAL_CONFIGURATIEREGELS; i++) {
            if ((main.configuratie_regels[i] != null)
                && (main.configuratie_regels[i].compareTo("") != 0)) {
              // System.out.println("+++ configuratie_regels[" + i + "] = " +
              // configuratie_regels[i]);
              out.write(main.configuratie_regels[i]);
              out.newLine(); // newLine(): write a line separator. The line separator string is
              // defined by the system property line.separator, and is not
              // necessarily a single newline ('\n') character.
            }
          } // for (int i = 0; i < MAX_AANTAL_CONFIGURATIEREGELS; i++)

          // user feedback string
          export_maintenance_data_ok =
              "OK, maintenance data written to: " + maintenanceDataExportFile;

        } // try
        catch (IOException ex) {
          export_maintenance_data_ok =
              "Unable to write to: " + maintenanceDataExportFile + " (" + ex + ")";
        } // catch

        return export_maintenance_data_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          String result_export_maintenance_data_ok = get();

          if (result_export_maintenance_data_ok.contains("OK") != true) {
            // show error message
            JOptionPane.showMessageDialog(
                null,
                result_export_maintenance_data_ok,
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          } else {
            // show the succesfully exported results
            JOptionPane.showMessageDialog(
                null,
                result_export_maintenance_data_ok,
                main.APPLICATION_NAME + " info",
                JOptionPane.INFORMATION_MESSAGE);
          }
        } catch (InterruptedException | ExecutionException ex) {
          // show error message
          JOptionPane.showMessageDialog(
              null,
              "Error writing export file (" + ex + ")",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }

        // always show the latest 'operational' maintenance data (never mind the import
        // succeeded)
        owner.show_data();
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
