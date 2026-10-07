package turbowin;

import javax.swing.SwingWorker;

/** Owns asynchronous refreshing of the latest AWS measurements. */
final class LatestMeasurementsRefreshWorkflow {

  private LatestMeasurementsRefreshWorkflow() {}

  static void start(mylatestmeasurements owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        mylatestmeasurements.Read_Sensor_Data_Files_For_Latest_AWS_Measurements();

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        owner.reset_all_table_cells();
        owner.insert_AWS_values_in_table_fields();
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
