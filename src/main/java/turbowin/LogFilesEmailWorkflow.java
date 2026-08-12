package turbowin;

import static turbowin.main.*;

import java.io.File;
import javax.swing.JOptionPane;

/** Owns validation and preparation of meteorological log files for email. */
final class LogFilesEmailWorkflow {

  private LogFilesEmailWorkflow() {}

  static boolean customEmailSettingsComplete() {
    return !logs_email_recipient.equals("")
        && !your_custom_address.equals("")
        && !custom_security.equals("")
        && !custom_email_server.equals("")
        && !custom_port.equals("");
  }

  static void start() {
    // TODO add your handling code here:
    boolean doorgaan = true;
    String info = "";

    /* are you sure? */
    info =
        "Uploading log files should be undertaken when it is intended to return the stored log files"
            + " to the National Meteorological Service.\nDo you wish to proceed";

    if (JOptionPane.showConfirmDialog(
            null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION) {
      doorgaan = true;
    } else {
      JOptionPane.showMessageDialog(
          null,
          "moving log files process cancelled",
          APPLICATION_NAME + " message",
          JOptionPane.INFORMATION_MESSAGE);
      doorgaan = false;
    }

    /* logs dir must be set before (e.g. C:/Users/User/Downloads/logs) */
    if (logs_dir.trim().equals("") == true || logs_dir.trim().length() < 2) {
      doorgaan = false;
      info = "Logs folder unknown, select: Maintenance -> Log files settings and retry";
      JOptionPane.showMessageDialog(
          null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
    }

    /* check logs email recipient is set */
    if (logs_email_recipient.equals("")) {
      info = "logs email recipient not set\n" + "select: Maintenance -> Email settings";
      JOptionPane.showMessageDialog(
          null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    /* check if there is an immt log source file present (and not empty) */
    if (doorgaan) {
      main.volledig_path_srcFilename_immt = logs_dir + java.io.File.separator + IMMT_LOG;
      File immt_source_file = new File(main.volledig_path_srcFilename_immt);
      if (immt_source_file.exists() && immt_source_file.length() > 10) {
        doorgaan = true;
      } else {
        JOptionPane.showMessageDialog(
            null,
            "Move log files cancelled, reason: nothing to move; IMMT log (file with all stored observations for climatological use) empty ",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        doorgaan = false;
      }
    }

    // summary pop-up message in the case of CUSTOM email logs send method */
    if (doorgaan && log_files_email_send_method.equals(LOGS_CUSTOM_EMAIL)) {
      if (!customEmailSettingsComplete()) {
        info =
            "not all required email parameters set (logs email recipient, your email address, server, port, security[TLS,SSL,STARTTLS])\n"
                + "select: Maintenance -> Email settings";
        JOptionPane.showMessageDialog(
            null, info, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        doorgaan = false;
      } else {
        info =
            "sending meteo logs\n"
                + "\n"
                + "to: "
                + logs_email_recipient
                + "\n"
                + // eg "user@example.com,user@example.org";
                "from: "
                + your_custom_address
                + "\n"
                + // eg nedlloyd_ebro@nedlloyd.nl
                "subject: "
                + METEO_LOGS
                + ship_name
                + "\n"
                + "attachement: "
                + main.ship_name
                + " "
                + main.LOGS_ZIP
                + "\n"
                + "\n"
                + "Do you wish to proceed";

        if (JOptionPane.showConfirmDialog(
                null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) {
          doorgaan = true;
        } else {
          JOptionPane.showMessageDialog(
              null,
              "moving log files process cancelled",
              APPLICATION_NAME + " message",
              JOptionPane.INFORMATION_MESSAGE);
          doorgaan = false;
        }
      } // else
    } // if (doorgaan && log_files_email_send_method.equals(LOGS_CUSTOM_EMAIL))

    if (doorgaan) {
      /* check sub dir temp already present, if not -> create */
      temp_logs_dir = logs_dir + java.io.File.separator + "temp";
      final File dirs = new File(temp_logs_dir);

      if (dirs.exists() == false) {
        //// * create subdir 'temp' (e.g. C:\Users\User\Downloads\logs/temp) */
        /// temp_logs_dir = logs_dir + java.io.File.separator + "temp";

        final boolean success = dirs.mkdirs();
        if (success == false) {
          doorgaan = false;
          JOptionPane.showMessageDialog(
              null,
              "Could not create " + temp_logs_dir + ", operation cancelled",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } else {
          doorgaan = true;
        }
      } // if (temp_logs_dir.trim().equals("") == true || temp_logs_dir.trim().length() < 2)
      else // temp sub dir already present
      {
        // delete all the files in the temp dir (remains of a previous zip action)
        final File file_logs_dir = new File(temp_logs_dir + java.io.File.separator);
        String[] filenames =
            file_logs_dir
                .list(); // Returns an array of strings naming the files and directories in the
        // directory denoted by this abstract pathname.

        for (int i = 0; i < filenames.length; i++) {
          File file_to_be_deleted = new File(temp_logs_dir + java.io.File.separator + filenames[i]);
          if (file_to_be_deleted.delete() == false) {
            JOptionPane.showMessageDialog(
                null,
                "delete error: "
                    + filenames[i]
                    + " (Maintenance_Move_log_files_by_email_actionPerformed)",
                APPLICATION_NAME + " error",
                JOptionPane.ERROR_MESSAGE);
          }
        } // for (int i = 0; i < filenames.length; i++)

        doorgaan = true;
      } // else
    } // if (doorgaan)

    /* move log files from logs_dir (e.g. C:\Users\User\Downloads\logs) to temp_logs_dir (e.g. C:\Users\User\Downloads\logs/temp) */
    if (doorgaan) {
      String move_mode_logs = MOVE_TO_EMAIL;
      doorgaan = support_class.Move_log_files(move_mode_logs); // and zip the moved log files
    } // if (doorgaan)

    // obs and E-mail address OK -> proceed
    if (doorgaan == true) {
      LogEmailSendWorkflow.start();
    } // if (doorgaan == true)
  } // GEN-LAST:event_Maintenance_Move_log_files_by_email_actionPerformed
}
