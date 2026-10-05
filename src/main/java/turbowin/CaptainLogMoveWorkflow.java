package turbowin;

import static turbowin.main.APPLICATION_NAME;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Copies the captain log asynchronously. */
final class CaptainLogMoveWorkflow {

  private CaptainLogMoveWorkflow() {}

  static void start(String source, String destination, Runnable onSuccess) {
    new SwingWorker<String, Void>() {
      @Override
      protected String doInBackground() throws Exception {
        return LogFileCopyWorkflow.copy(source, destination);
      }

      @Override
      protected void done() {
        String result = null;
        try {
          result = get();
        } catch (InterruptedException | ExecutionException ex) {
          // Preserve the existing behavior: no additional message is shown here.
        }

        if ("NOT_OK".equals(result)) {
          JOptionPane.showMessageDialog(
              null,
              "Unable to move " + source + " to " + destination,
              APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } else if ("OK".equals(result)) {
          onSuccess.run();
        }
      }
    }.execute();
  }
}
