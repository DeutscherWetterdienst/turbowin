package turbowin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        String latest_immt_record = "";
        String record = "";

        // first check if there is an immt log source file present (and not empty)
        String volledig_path_immt = main.logs_dir + File.separator + main.IMMT_LOG;

        File immt_file = new File(volledig_path_immt);
        if (immt_file.exists() && immt_file.length() > 0) // length() in bytes
        {
          try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt))) {
            while ((record = in.readLine()) != null) {
              latest_immt_record = record;
            }
          } catch (IOException ex) {
            System.out.println("--- Function initComponents2(): " + ex);
          }
        } // if (immt_file.exists() && immt_file.length() > 0)

        return latest_immt_record;
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
