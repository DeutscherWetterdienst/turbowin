package turbowin;

import java.io.File;
import javax.swing.SwingWorker;

/** Owns asynchronous cleanup of the primary sensor data file. */
final class Rs232ViewSensorFileCleanupWorkflow {

  private Rs232ViewSensorFileCleanupWorkflow() {}

  static void start(String sensorDataFilePath) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        File file_sensor_data = new File(sensorDataFilePath);
        if (file_sensor_data.exists()) {
          file_sensor_data.delete();
        }
        return null;
      }
    }.execute();
  }
}
