package turbowin;

import javax.swing.SwingWorker;

/** Owns asynchronous AWS sensor map loading for OSM. */
final class OsmAwsSensorMapWorkflow {

  private OsmAwsSensorMapWorkflow() {}

  static void start(OSM owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        mylatestmeasurements
            .Read_Sensor_Data_Files_For_Latest_AWS_Measurements(); // now AWS_array[][] is filled
        // (see
        // myleatestmeasurements.java)

        // display the online/offline map via a web browser
        if (main.OSM_mode.equals(main.OSM_ONLINE_AWS_SENSOR)) {
          owner.OSM_display_AWS_Sensor_on_online_map();
        } else if (main.OSM_mode.equals(main.OSM_OFFLINE_AWS_SENSOR)) {
          owner.OSM_display_AWS_Sensor_on_offline_map();
        }

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        ;
      }
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
