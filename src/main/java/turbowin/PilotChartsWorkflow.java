package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous opening and error handling for pilot charts. */
final class PilotChartsWorkflow {

  private PilotChartsWorkflow() {}

  static void start(String chart) {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        String os = OSDetector.getOSString();

        String sub_url_chart_number = "";
        String sub_url_chart_month = "";

        if (chart.substring(12, 14).equals("SA")) {
          sub_url_chart_number = "105";
        } else if (chart.substring(12, 14).equals("NA")) {
          sub_url_chart_number = "106";
        } else if (chart.substring(12, 14).equals("SP")) {
          sub_url_chart_number = "107";
        } else if (chart.substring(12, 14).equals("NP")) {
          sub_url_chart_number = "108";
        } else if (chart.substring(12, 14).equals("IN")) {
          sub_url_chart_number = "109";
        }

        sub_url_chart_month = chart.substring(15, 18).toLowerCase();
        String link_url =
            "https://download.dwd.de/pub/turbowin/archive/knmi/pilot_charts/"
                + sub_url_chart_number
                + sub_url_chart_month
                + ".pdf";

        return DesktopUtils.openLink(link_url, os);
      }

      @Override
      protected void done() {
        try {
          Integer response_code = get();

          if (response_code == -1) {
            String os = OSDetector.getOSString();
            String message;
            if (os.equals("LINUX")) {
              message = "[GENERAL] Error invoking default web browser (OS = Linux)";
            } else {
              message =
                  "[GENERAL] Error invoking default web browser (-Desktop-method not supported on this computer system)";
            }
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
