package turbowin;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Owns asynchronous reading of the latest IMMT observation for the dashboard. */
final class DashboardLatestObservationWorkflow {

  private DashboardLatestObservationWorkflow() {}

  static void start(Consumer<String> onObservationRead) {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        // first check if there is an immt log source file present (and not empty)
        String volledig_path_immt = main.logs_dir + File.separator + main.IMMT_LOG;

        return ImmtLogReaderWorkflow.readLastRecord(volledig_path_immt);
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          String latest_dashboard_obs = get();
          onObservationRead.accept(latest_dashboard_obs);

        } // try
        catch (InterruptedException | ExecutionException ex) {
          System.out.println(
              "+++ Error in Function: initComponents2() [DASHBOARD_latest_obs.java] " + ex);
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
