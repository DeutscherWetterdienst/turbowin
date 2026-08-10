package turbowin;

import static turbowin.main.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the asynchronous send and completion handling for meteorological log email. */
final class LogEmailSendWorkflow {

  private LogEmailSendWorkflow() {}

  static void start() {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        Integer responseCode = 0;

        if (log_files_email_send_method.equals(LOGS_CUSTOM_EMAIL)) {
          // NB use the CUSTOM email method BUT this is only possible for the CUSTOM jakarta
          // module not for the CUSTOM python module
          //    so even if python email module is selected in CUSTOM (Maintenance -> Email
          // settings) jakarta email module will be used!!
          if (jakarta_email_class == null) {
            jakarta_email_class = new Jakarta_Email();
          }

          // CUSTOM_TLS_STARTTLS / CUSTOM_TLS / CUSTOM_SSL_STARTTLS / CUSTOM_SSL
          String smtp_mode = custom_security;
          String smtp_host_local = custom_email_server;
          String smtp_password_local = custom_password;
          String send_to = logs_email_recipient;
          String send_from = your_custom_address;
          String email_subject = METEO_LOGS + " " + ship_name;

          // Log emails always use a fixed body and send the log as an attachment.
          String email_body = "see attachment";
          String smtp_port_local = custom_port;
          String attachment = "yes";

          String smtp_password_local_plain = "";
          if (smtp_password_local.equals("null") == false) {
            // custom_password is encrypted; decrypt it before passing it to the Jakarta email
            // module.
            smtp_password_local_plain = myemailsettings.decrypt(smtp_password_local);
          } else {
            // The literal "null" is a sentinel for an unset/un-encrypted password
            // (e.g. EMAIL_SEND_LOCAL_HOST), so it must not be decrypted.
            smtp_password_local_plain = "null";
          }

          responseCode =
              jakarta_email_class.send_jakarta_email_logs(
                  smtp_mode,
                  smtp_host_local,
                  smtp_password_local_plain,
                  send_to,
                  send_from,
                  email_subject,
                  email_body,
                  smtp_port_local,
                  attachment);
        } else {
          Desktop desktop = null;

          if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
            try {
              String body_txt =
                  "please attach manually the file: "
                      + temp_logs_dir
                      + java.io.File.separator
                      + ship_name
                      + " "
                      + LOGS_ZIP;
              String mail_txt =
                  logs_email_recipient
                      + "?subject="
                      + METEO_LOGS
                      + " "
                      + ship_name
                      + "&body="
                      + body_txt;

              URI uriMailTo = null;
              try {
                uriMailTo = new URI("mailto", mail_txt, null);
              } catch (URISyntaxException ex) {
                responseCode = 1;
              }

              desktop.mail(uriMailTo);
            } catch (IOException ex) {
              responseCode = 2;
            }
          } else {
            responseCode = 3;
          }
        }

        return responseCode;
      }

      @Override
      protected void done() {
        try {
          Integer response_code = get();

          switch (response_code) {
            case 1:
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking pc-default Email program (URISyntaxException)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              break;
            case 2:
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking pc-default Email program (IOException)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              break;
            case 3:
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking pc-default Email program (method not supported on this computer system)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              break;
            case 101:
              JOptionPane.showMessageDialog(
                  null,
                  "logs send failed; no attachment found (for details see: Info -> System logs)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              break;
            case 102:
              JOptionPane.showMessageDialog(
                  null,
                  "logs send failed (for details see: Info -> System logs)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              break;
            case 200:
              JOptionPane.showMessageDialog(
                  null,
                  "meteo logs successfully sent to " + logs_email_recipient,
                  main.APPLICATION_NAME + " error",
                  JOptionPane.INFORMATION_MESSAGE);
              break;
          }
        } catch (InterruptedException | ExecutionException ex) {
          main.log_turbowin_system_message("[EMAIL] logs send failed: " + ex);
          JOptionPane.showMessageDialog(
              null,
              "Error sending logs via email: " + ex,
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }
      }
    }.execute();
  }
}
