package turbowin;

import javax.swing.SwingWorker;

/** Owns asynchronous secondary-instrument loading for the RS232 graph timer. */
final class Rs232ViewSecondarySensorTimerWorkflow {

  private Rs232ViewSecondarySensorTimerWorkflow() {}

  static void start(RS232_view owner) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        owner.readSecondarySensorDataFromFile();
        return null;
      }

      @Override
      protected void done() {
        owner.repaintSensorGraph();
      }
    }.execute();
  }
}
