package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous opening and error handling for the statistics link. */
final class StatisticsLinkWorkflow {

  private StatisticsLinkWorkflow() {}

  static void start() {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        String os = OSDetector.getOSString();
        // NB deprecated from October 2024: http://esurfmar.meteo.fr/cgi-bin/meteo/display_vos_ext.cgi?callchx=
        String link_url = "https://esurfmar.meteo.fr/cgi-bin/display_vos_ext.cgi?callchx=";
        link_url += station_ID;
        return DesktopUtils.openLink(link_url, os);
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
