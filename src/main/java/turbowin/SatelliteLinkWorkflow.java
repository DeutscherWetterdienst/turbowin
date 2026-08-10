package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous opening and error handling for satellite image links. */
final class SatelliteLinkWorkflow {

  private SatelliteLinkWorkflow() {}

  static void start(String url_satellite_image) {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        String os = OSDetector.getOSString();
        return DesktopUtils.openLink(url_satellite_image, os);
      }

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
        } catch (InterruptedException | ExecutionException ex) {
          String message = "[GENERAL] Error invoking default web browser; " + ex.toString();
          main.log_turbowin_system_message(message);
        }
      }
    }.execute();
  }
}
