package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the asynchronous IMMT log size check and notification. */
final class ImmtLogSizeCheckWorkflow {

  private ImmtLogSizeCheckWorkflow() {}

  static void start() {
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        String volledig_path_immt = logs_dir + java.io.File.separator + IMMT_LOG;
        File immt_file = new File(volledig_path_immt);
        // File length and IMMT_LIMIT are in bytes; the notification displays the limit in Kb.
        return immt_file.exists() && immt_file.length() > IMMT_LIMIT;
      }

      @Override
      protected void done() {
        try {
          boolean immt_size_limit_exceeded = get();
          if (immt_size_limit_exceeded) {
            String info =
                "immt log (file with all stored observations for research and climatological use) exceeds ";
            info += IMMT_LIMIT / 1024;
            info += " Kb.\nPlease select one of the coming days: Maintenance -> Move log files ";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
          }
        } catch (InterruptedException | ExecutionException ex) {
          // Preserve the original behavior: background check failures are silent.
        }
      }
    }.execute();
  }
}
