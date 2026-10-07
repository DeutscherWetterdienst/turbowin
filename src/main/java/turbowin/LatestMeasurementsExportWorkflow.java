package turbowin;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous exporting of the latest AWS measurements. */
final class LatestMeasurementsExportWorkflow {

  private LatestMeasurementsExportWorkflow() {}

  static void start(mylatestmeasurements owner, String exportFile) {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        String export_ok = owner.write_export_file(exportFile);

        return export_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        String result_export_ok = null;
        try {
          result_export_ok = get();
        } catch (ExecutionException | InterruptedException ex) {
          result_export_ok = "Error writing export file (" + ex + ")";
        }

        if (result_export_ok.contains("OK") != true) {
          // show error message
          JOptionPane.showMessageDialog(
              null,
              result_export_ok,
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } else {
          // show the succesfully exported message
          JOptionPane.showMessageDialog(
              null,
              result_export_ok,
              main.APPLICATION_NAME + " info",
              JOptionPane.INFORMATION_MESSAGE);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
