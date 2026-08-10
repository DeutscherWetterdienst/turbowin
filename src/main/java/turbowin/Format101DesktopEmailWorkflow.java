package turbowin;

import static turbowin.main.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the desktop Format 101 observation email workflow. */
final class Format101DesktopEmailWorkflow {

  private Format101DesktopEmailWorkflow() {}

  static void start() {
    // This function called by: Output_obs_by_email_all()

    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        /*
        // Version 6 of the Java Platform, Standard Edition (Java SE), continues to narrow the gap with
        // new system tray functionality, better  print support for JTable, and now the Desktop API
        // (java.awt.Desktop API).
        //
        // Use the Desktop.isDesktopSupported() method to determine whether the Desktop API is available.
        // On the Solaris Operating System and the Linux platform, this API is dependent on Gnome libraries.
        // If those libraries are unavailable, this method will return false. After determining that the API is
        // supported, that is, the isDesktopSupported() returns true, the application can retrieve a Desktop
        // instance using the static method getDesktop().
        //
        */
        Desktop desktop = null;
        String email_body_line = "";
        boolean doorgaan = true;

        // Before more Desktop API is used, first check
        // whether the API is supported by this particular
        // virtual machine (VM) on this particular host.
        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
          try {
            final String volledig_path_format_101_compressed_file =
                main.logs_dir
                    + java.io.File.separator
                    + FORMAT_101_ROOT_DIR
                    + java.io.File.separator
                    + FORMAT_101_TEMP_DIR
                    + java.io.File.separator
                    + "HPK_"
                    + FORMAT_101_INPUT_FILE; // NB adding "HPK_" to the input file name is
            // automatically done by the C-code compression
            // functions

            //////// format 101 message in the e-mail body //////
            //
            if (main.obs_101_email.equals(FORMAT_101_BODY) == true) {
              // read the compressed obs (format 101) which is the only line in file
              // HPK_format_101.txt
              email_body_line =
                  Format101EmailContentResolver.resolve(
                      main.FORMAT_101_BODY, new File(volledig_path_format_101_compressed_file));
              if (email_body_line.equals("") == true) {
                doorgaan = false;
              }
            } // if (main.obs_101_email.equals(FORMAT_101_BODY) == true);

            /////// format 101 message as attachment //////
            //
            else if (main.obs_101_email.equals(FORMAT_101_ATTACHEMENT) == true) {
              final File compressed_file = new File(volledig_path_format_101_compressed_file);
              email_body_line =
                  Format101EmailContentResolver.resolve(
                      main.FORMAT_101_ATTACHEMENT, compressed_file);

              if (email_body_line.equals("") == false) {
                doorgaan = true;
              } else {
                JOptionPane.showMessageDialog(
                    null,
                    "No format 101 obs available. The following file does not exist: "
                        + volledig_path_format_101_compressed_file,
                    main.APPLICATION_NAME + " error",
                    JOptionPane.WARNING_MESSAGE);
                doorgaan = false;
              } // else
            } // else if (main.obs_101_email.equals(FORMAT_101_ATTACHEMENT) == true)

            /////// invoke email program //////
            //
            if (doorgaan == true) {
              /* if ddmmyyyy in subject field -> replace by actual utc date of observation */
              String obs_email_subject_new =
                  obs_email_subject.replaceAll(
                      "ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");

              // String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  +
              // "&body=" + email_body_line;
              String mail_txt =
                  ObservationEmailComposer.buildMailText(
                      obs_email_recipient, obs_email_cc, obs_email_subject_new, email_body_line);

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
            } // if (doorgaan == true)
          } // try
          catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error invoking default Email program" + " (" + ex + ")",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
        } // if (Desktop.isDesktopSupported())
        else {
          JOptionPane.showMessageDialog(
              null,
              "Error invoking default Email program (-Desktop- method not supported on this computer system)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        } // else

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        IMMT_log();

        Reset_all_meteo_parameters();
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }

  static String get_format_101_obs_from_file() {
    final String volledig_path_format_101_compressed_file =
        main.logs_dir
            + java.io.File.separator
            + FORMAT_101_ROOT_DIR
            + java.io.File.separator
            + FORMAT_101_TEMP_DIR
            + java.io.File.separator
            + "HPK_"
            + FORMAT_101_INPUT_FILE; // NB adding "HPK_" to the input file name is automatically
    // done by the C-code compression functions

    return Format101FileReader.readFirstLine(new File(volledig_path_format_101_compressed_file));
  }
}
