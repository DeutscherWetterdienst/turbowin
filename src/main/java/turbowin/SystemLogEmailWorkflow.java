package turbowin;

import static turbowin.main.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.TimeZone;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous composition and opening of an email for system logs. */
final class SystemLogEmailWorkflow {

  private SystemLogEmailWorkflow() {}

  static void start() {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        Desktop desktop = null;
        String email_body_line = "";

        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
          try {
            TimeZone timeZone = TimeZone.getTimeZone("UTC");
            Calendar cal = Calendar.getInstance(timeZone);

            String file_naam_1 = "turbowin_system_" + sdf_tsl_1.format(cal.getTime()) + ".txt";
            cal.add(Calendar.MONTH, -1);
            String file_naam_2 = "turbowin_system_" + sdf_tsl_1.format(cal.getTime()) + ".txt";

            String logs_path =
                main.logs_dir
                    + java.io.File.separator
                    + main.TURBOWIN_SYSTEM_LOGS_DIR
                    + java.io.File.separator;
            File system_file_1 = new File(logs_path + file_naam_1);
            File system_file_2 = new File(logs_path + file_naam_2);

            // Writing every system-log line into the mail body fails; list the current and
            // previous monthly log files for manual attachment instead.
            if (system_file_1.exists()) {
              email_body_line = "Please attach manually the file: " + system_file_1;
            }

            if (system_file_2.exists()) {
              String previous_log = "Please attach manually the file: " + system_file_2;
              if (system_file_1.exists()) {
                email_body_line += "\nand\n\n" + previous_log;
              } else {
                email_body_line = previous_log;
              }
            }

            if (!system_file_1.exists() && !system_file_2.exists()) {
              JOptionPane.showMessageDialog(
                  null,
                  "No " + APPLICATION_NAME + " system log files found",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
            } else {
              String email_recipient = "";
              String email_subject = APPLICATION_NAME + " System logs " + main.ship_name;
              String mail_txt =
                  email_recipient + "?subject=" + email_subject + "&body=" + email_body_line;
              URI uriMailTo = null;
              try {
                uriMailTo = new URI("mailto", mail_txt, null);
              } catch (URISyntaxException ex) {
                JOptionPane.showMessageDialog(
                    null,
                    "Error invoking default Email program" + " (" + ex + ")",
                    main.APPLICATION_NAME + " error",
                    JOptionPane.WARNING_MESSAGE);
              }
              desktop.mail(uriMailTo);
            }
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error invoking default Email program" + " (" + ex + ")",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
        } else {
          JOptionPane.showMessageDialog(
              null,
              "Error invoking default Email program (-Desktop- method not supported on this computer system)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }

        return null;
      }
    }.execute();
  }
}
