package turbowin;

import static turbowin.main.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns the desktop FM13 observation email workflow. */
final class Fm13DesktopEmailWorkflow {

  private Fm13DesktopEmailWorkflow() {}

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

        // Before more Desktop API is used, first check
        // whether the API is supported by this particular
        // virtual machine (VM) on this particular host.
        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
          try {
            // if ddmmyyyy in subject field -> replace by actual utc date of observation

            String obs_email_subject_new =
                obs_email_subject.replaceAll(
                    "ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00");

            // String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  +
            // "&body=" + obs_write;
            String mail_txt =
                ObservationEmailComposer.buildMailText(
                    obs_email_recipient, obs_email_cc, obs_email_subject_new, obs_write);

            URI uriMailTo = null;
            try {
              uriMailTo = new URI("mailto", mail_txt, null);
            } catch (URISyntaxException ex) {
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking default Email program (URISyntaxException)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
            }

            desktop.mail(uriMailTo);

          } // try
          catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error invoking default Email program",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
        } // if (Desktop.isDesktopSupported())
        else // Desktop method not supported
        {
          // Now try to open with mailto
          //
          // Now (mailto)workarounds follow with 'best shots'
          //
          //
          // NB The main problem with using mailto is with breaking lines. Use %0A for carriage
          // returns, %20 for spaces.
          // NB
          // http://stackoverflow.com/questions/17373/how-do-i-open-the-default-mail-program-with-a-subject-and-body-in-a-cross-platfo
          // NB http://www.wsoftware.de/practices/proc-execs.html

          /* determine the OS this program is running on */
          String os = OSDetector.getOSString();

          // JOptionPane.showMessageDialog(null, "Error invoking default Email program (method not
          // supported on this computer system)", main.APPLICATION_NAME + " error",
          // JOptionPane.WARNING_MESSAGE);
          // Runtime runtime = Runtime.getRuntime();

          // NB Use %0A for carriage returns, %20 for spaces [see "UrlUtils.urlEncode(obs_write)"]
          // if ddmmyyyy in subject field -> replace by actual utc date of observation
          String obs_email_subject_new =
              UrlUtils.urlEncode(
                  obs_email_subject.replaceAll(
                      "ddhhmm", mydatetime.YY_code + mydatetime.GG_code + "00"));

          // String mail_txt = obs_email_recipient + "?subject=" + obs_email_subject_new  + "&body="
          // + UrlUtils.urlEncode(obs_write);
          String mail_txt = "";
          if (obs_email_cc.length() > 3) {
            // NB after the email address you'll use a question mark to prefix the first variable,
            // and ampersands ( & ) for each consecutive variable.
            // (https://developer.yoast.com/guide-mailto-links/)
            mail_txt =
                obs_email_recipient
                    + "?cc"
                    + obs_email_cc
                    + "&subject="
                    + obs_email_subject_new
                    + "&body="
                    + UrlUtils.urlEncode(obs_write);
          } else {
            mail_txt =
                obs_email_recipient
                    + "?subject="
                    + obs_email_subject_new
                    + "&body="
                    + UrlUtils.urlEncode(obs_write);
          }

          // if (amos_mail) // DO NOT USE %0A for carriage returns and %20 for spaces [so NOT
          // "UrlUtils.urlEncode(obs_write)"] NB DIDN'T WORK EITHER SO FROM THIS VERSION NO
          // PARTICULAR CODE
          // FOR AMOS MAIL
          if (os.equals("WINDOWS")) {
            try {
              // deprecated from JDK19
              // runtime.exec("cmd /c " + "start " + "mailto:" + mail_txt);

              // NB during a test on 25-05-2016 on Windows 10 Runtime.getRuntime(); worked only
              // partially (the body contents wasn't copied to the amial client)

              // create cmd array
              String[] cmdArray = MailtoCommandBuilder.forWindows(mail_txt);

              // create a process and execute cmdArray
              Process process = Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking default email client (-Desktop-method not supported on this computer system and Runtime alternatives failed)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
            }
          } // if (os.equals("WINDOWS"))
          else if (os.equals("MACOS")) {
            try {
              // deprecated from JDK19
              // runtime.exec("open " + "mailto:" + mail_txt);

              // create cmd array
              String[] cmdArray = MailtoCommandBuilder.forMacOs(mail_txt);

              // create a process and execute cmdArray
              Process process = Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking default email client (-Desktop-method not supported on this computer system and Runtime alternatives failed)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
            }
          } // else if (os.equals("MACOS"))
          else // most probably a Linux;
          {
            try {
              // deprecated from JDK19
              // runtime.exec("xdg-open " + "mailto:" + mail_txt);

              // create cmd array
              String[] cmdArray = MailtoCommandBuilder.forLinux(mail_txt);

              // create a process and execute cmdArray
              Process process = Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e2) {
              try {
                // deprecated from JDK19
                // runtime.exec("kde-open " + "mailto:" + mail_txt);

                // create cmd array
                String[] cmdArray = MailtoCommandBuilder.forKde(mail_txt);

                // create a process and execute cmdArray
                Process process = Runtime.getRuntime().exec(cmdArray);
              } catch (IOException e3) {
                JOptionPane.showMessageDialog(
                    null,
                    "Error invoking default email client (-Desktop-method not supported on this computer system and Runtime alternatives failed)",
                    main.APPLICATION_NAME + " error",
                    JOptionPane.WARNING_MESSAGE);
              }
            }
          } // else
        } // else (Desktop method not supported)

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        IMMT_log();

        Reset_all_meteo_parameters();
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
