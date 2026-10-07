package turbowin;

import javax.swing.SwingWorker;

/** Owns asynchronous sensor-file loading for the RS232 graph view. */
final class Rs232ViewSensorFileWorkflow {

  private Rs232ViewSensorFileWorkflow() {}

  static void start(RS232_view owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        owner.readSensorDataFromFile();
        return null;
      }

      @Override
      protected void done() {
        owner.repaintSensorGraph();
      }
    }.execute();
  }
}
