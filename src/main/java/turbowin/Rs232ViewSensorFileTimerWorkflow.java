package turbowin;

import javax.swing.SwingWorker;

/** Owns asynchronous primary sensor-file loading triggered by the graph timer. */
final class Rs232ViewSensorFileTimerWorkflow {

  private Rs232ViewSensorFileTimerWorkflow() {}

  static void start(RS232_view owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        owner.readPrimarySensorDataFromFile();
        return null;
      }

      @Override
      protected void done() {
        owner.repaintSensorGraph();
      }
    }.execute();
  }
}
