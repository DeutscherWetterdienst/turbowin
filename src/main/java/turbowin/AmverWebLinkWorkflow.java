package turbowin;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous opening of the AMVER web link. */
final class AmverWebLinkWorkflow {

  private AmverWebLinkWorkflow() {}

  static void start() {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        String os = OSDetector.getOSString();

        String link_url = "https://www.amver.com";

        return DesktopUtils.openLink(link_url, os);
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          Integer response_code = get();

          if (response_code == -1) {
            String message = "[GENERAL] Error invoking default web browser";
            JOptionPane.showMessageDialog(
                null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            main.log_turbowin_system_message(message);
          } else if (response_code == -2) {
            String message = "[GENERAL] Error invoking URL";
            JOptionPane.showMessageDialog(
                null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            main.log_turbowin_system_message(message);
          }
        } // try
        catch (InterruptedException | ExecutionException ex) {
          String message = "[GENERAL] Error invoking default web browser; " + ex.toString();
          main.log_turbowin_system_message(message);
        } // catch
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
