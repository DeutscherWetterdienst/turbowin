package turbowin;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous launching of an AMVER email message. */
final class AmverEmailLaunchWorkflow {

  private static final int MAX_CHAR_AMVER_EMAIL_BODY = 400;
  private static final String AMVER_EMAIL_ADDRESS = "amvermsg@amver.org";

  private AmverEmailLaunchWorkflow() {}

  static void start(String emailBody, int emailBodyLength) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        Desktop desktop = null;

        // Desktop requires VM/host support and platform libraries such as Gnome on Solaris/Linux.
        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
          try {
            String email_subject = "Amver Sailing plan";
            String email_txt = "";
            if (emailBodyLength < MAX_CHAR_AMVER_EMAIL_BODY) {
              email_txt = AMVER_EMAIL_ADDRESS + "?subject=" + email_subject + "&body=" + emailBody;
            } else {
              email_txt = AMVER_EMAIL_ADDRESS + "?subject=" + email_subject;
              JOptionPane.showMessageDialog(
                  null,
                  "Paste coded amver message from clipboard (right click in e-mail body and select Paste)",
                  main.APPLICATION_NAME + " info",
                  JOptionPane.INFORMATION_MESSAGE);
            }

            URI uriMailTo = null;
            try {
              uriMailTo = new URI("mailto", email_txt, null);
            } catch (URISyntaxException ex) {
              JOptionPane.showMessageDialog(
                  null,
                  "Error invoking default Email program (URISyntaxException)",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
            }

            desktop.mail(uriMailTo);
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "Error invoking default Email program",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
        } else {
          JOptionPane.showMessageDialog(
              null,
              "Error invoking default Email program (method not supported on this computer system)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
        }

        return null;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {}
    }.execute();
  }
}
