package turbowin;

import java.awt.HeadlessException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous export of barometer comparison data. */
final class BarometerComparisonExportWorkflow {

  private BarometerComparisonExportWorkflow() {}

  static void start(String exportFile, ExportData data) {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        String export_maintenance_data_ok = "OK";

        try (BufferedWriter out = new BufferedWriter(new FileWriter(exportFile))) {
          out.write("BAROMETER COMPARISON");
          out.newLine();
          out.newLine();
          out.write("ship name: " + data.shipName());
          out.newLine();
          out.write(
              "height ship barometer above Summer Load Line: "
                  + data.barometerAboveSll()
                  + " metres");
          out.newLine();
          out.write(
              "distance of bottom of the keel to Summer Load Line: " + data.keelSll() + " metres");
          out.newLine();
          out.newLine();
          out.write("date and time: " + data.dateTime() + " UTC");
          out.newLine();
          out.write("position or port: " + data.position());
          out.newLine();
          out.write(
              "ship barometer reading, indicating air pressure at bridge level: "
                  + data.shipBarometerReading()
                  + " hPa");
          out.newLine();
          out.write(
              "reference barometer reading, indicating air pressure at sea level: "
                  + data.referenceBarometerReading()
                  + " hPa");
          out.newLine();
          out.write("actual ship draft: " + data.draft() + " metres");
          out.newLine();
          out.write("outdoor air temperature: " + data.airTemp() + " °C");
          out.newLine();
          out.newLine();
          out.write(
              "actual height of the ship barometer above the waterline: "
                  + data.shipBarometerAboveWl()
                  + " metres");
          out.newLine();
          out.write(
              "ship barometer air pressure converted to sea level: "
                  + data.shipBarometerSeaLevel()
                  + " hPa");
          out.newLine();
          out.write(
              "instrument error ship barometer: " + data.instrumentErrorShipBarometer() + " hPa");
          out.newLine();
          out.write(
              "instrument correction ship barometer: "
                  + data.instrumentCorrectionShipBarometer()
                  + " hPa");
          out.newLine();

          // user feedback string
          export_maintenance_data_ok = "OK, barometer comparison data written to: " + exportFile;
        } catch (IOException | HeadlessException e) {
          // Note: A try-with-resources statement can have catch and finally blocks just like an
          // ordinary try-with-resources statement. In a try-with-resources statement, any catch or
          // finally block is run after the resources declared have been closed.
          export_maintenance_data_ok = "Unable to write to: " + exportFile + " (" + e + ")";
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
            // show the 'succesfully exported' message
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
      } // protected void done()
    }.execute(); // new SwingWorker<String, Void>()
  }

  record ExportData(
      String shipName,
      String barometerAboveSll,
      String keelSll,
      String dateTime,
      String position,
      String shipBarometerReading,
      String referenceBarometerReading,
      String draft,
      String airTemp,
      String shipBarometerAboveWl,
      String shipBarometerSeaLevel,
      String instrumentErrorShipBarometer,
      String instrumentCorrectionShipBarometer) {}
}
