package turbowin;

import static turbowin.main.APPLICATION_NAME;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Opens a file or URL using the desktop and platform fallbacks. */
final class BrowserOpenWorkflow {

  private BrowserOpenWorkflow() {}

  static boolean isWebAddress(String address) {
    return address.contains("http") || address.contains("HTTP");
  }

  static void startNonLinux(String subjectAddress) {
    new SwingWorker<Integer, Void>() {
      @Override
      protected Integer doInBackground() throws Exception {
        int code = 0;

        if (Desktop.isDesktopSupported()) {
          Desktop desktop = Desktop.getDesktop();
          try {
            if (!isWebAddress(subjectAddress)) {
              // Local files use Desktop.open; HTTP addresses are opened with Desktop.browse.
              desktop.open(new File(subjectAddress));
            } else {
              desktop.browse(new URI(subjectAddress));
            }
          } catch (IOException | URISyntaxException ex) {
            code = -1;
          }
        } else {
          code = -1;
        }

        if (code == -1) {
          // Raspberry Pi/Linux installations may need KDE or XDG; macOS uses "open" as fallback.
          try {
            Runtime.getRuntime().exec(new String[] {"kde-open", subjectAddress});
          } catch (IOException ex) {
            try {
              Runtime.getRuntime().exec(new String[] {"xdg-open", subjectAddress});
            } catch (IOException ex2) {
              try {
                Runtime.getRuntime().exec(new String[] {"open", subjectAddress});
              } catch (IOException ex3) {
                code = -2;
              }
            }
          }
        }

        return code;
      }

      @Override
      protected void done() {
        try {
          Integer responseCode = get();
          if (responseCode == -2) {
            String message = "[GENERAL] Error invoking default web browser or pdf reader";
            JOptionPane.showMessageDialog(
                null, message, APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            main.log_turbowin_system_message(message);
          }
        } catch (InterruptedException | ExecutionException ex) {
          String message =
              "[GENERAL] Error invoking default web browser or pdf reader; " + ex.toString();
          main.log_turbowin_system_message(message);
        }
      }
    }.execute();
  }
}
