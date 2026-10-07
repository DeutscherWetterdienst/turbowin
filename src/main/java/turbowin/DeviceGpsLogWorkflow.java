package turbowin;

import java.util.List;
import javax.swing.SwingWorker;

/** Owns asynchronous display of buffered GPS device-log data. */
final class DeviceGpsLogWorkflow {

  private DeviceGpsLogWorkflow() {}

  static void start(mydevice_log owner, String newline) {
    new SwingWorker<String, String>() {
      @Override
      protected String doInBackground() throws Exception {
        publish(new String[] {owner.log_separator()});
        publish(new String[] {newline});
        publish(new String[] {"--- RECEIVED GPS DATA ---"});

        String GPS_memory_line = "";
        for (int m = main_RS232_RS422.MAX_GPS_ARRAY; m > 0; m--) {
          GPS_memory_line = main_RS232_RS422.GPS_array_device_log[m];
          publish(new String[] {GPS_memory_line});
        }

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void process(List<String> data) {
        // process: Receives data chunks from the publish method asynchronously on the Event
        // Dispatch Thread.
        for (String received_line : data) {
          // NB eg http://www.javacreed.com/swing-worker-example/
          //    This swing component is only accessed from the process() method and never used
          // from within the doInBackbround() method
          //    or other methods directly (by directly we mean from the same thread) invoked from
          // it.

          // NB altijd in for loop omdat meerdere ontvangen string's verzameld kunnen zijn voordat
          // het hier geprocessed wordt(inherent aan SwingWorker)
          owner.appendDeviceGpsLine(received_line);
        }
      } // protected void process(List<String> data)

      @Override
      protected void done() {
        owner.resetDeviceLogCursor();
      } // protected void done()
    }.execute(); // new SwingWorker<String, String>()
  }
}
