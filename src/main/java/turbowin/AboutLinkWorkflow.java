package turbowin;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous opening of links from the about window. */
final class AboutLinkWorkflow {

  private AboutLinkWorkflow() {}

  static void start(String linkSubject, String sotVosLink, String gitlabLink) {
    new SwingWorker<Integer, Void>() {

      @Override
      protected Integer doInBackground() throws Exception {
        String os = OSDetector.getOSString();

        String link_url = "";
        if (linkSubject.equals(sotVosLink)) {
          link_url = "https://www.ocean-ops.org/sot/vos/";
        } else if (linkSubject.equals(gitlabLink)) {
          link_url = "https://github.com/DeutscherWetterdienst/turbowin";
        }

        return DesktopUtils.openLink(link_url, os);
      } // protected Integer doInBackground() throws Exception

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
          // main.jTextField4.setText(main.sdf_tsl_2.format(new Date()) + " UTC " + message);
        } // catch
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
