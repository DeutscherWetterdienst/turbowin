package turbowin;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopUtils {
  /**
   * Attempts to open the given URL in a browser using the appropriate method based on the operating
   * system.
   *
   * @param linkUrl the URL to open
   * @param os the operating system (e.g., "LINUX")
   * @return an integer code where 0 indicates success, -1 indicates failure to open, and -2
   *     indicates a browsing error
   */
  public static int openLink(String linkUrl, String os) {
    int code = 0;
    Desktop desktop = null;

    if ("LINUX".equalsIgnoreCase(os)) {
      // LINUX: try different commands before falling back to Desktop API.
      //
      // LINUX: first try runtime procedure
      //        second try Desktop procedure
      //
      // why?: on Fedora strange results if first Desktop method was invoked, then the browser only
      // opens as soon as TurboWin+ itself is closed....
      //       (so no error on Desktop procedure/method but strange behavior)
      //       In several correspondance items you can find that the Desktop method is not workig
      // fine on Linux
      //
      // related to: hardware accelarator of the webbrowser?
      // (https://stackoverflow.com/questions/69037458/selenium-chromedriver-gives-initializesandbox-called-with-multiple-threads-in)
      //         because the following errors in output window NetBeans IDE when trying to open URL
      //         libva error: vaGetDriverNameByIndex() failed with unknown libva error, driver_name
      // = (null)
      //         [5757:5757:1122/165659.328325:ERROR:sandbox_linux.cc(376)] InitializeSandbox()
      // called with multiple threads in process gpu-process.
      try {
        // First try: using kde-open
        String[] cmdArray = {"kde-open", linkUrl};
        Process process = Runtime.getRuntime().exec(cmdArray);
      } catch (IOException e) {
        try {
          // Second try: using xdg-open
          String[] cmdArray = {"xdg-open", linkUrl};
          Process process = Runtime.getRuntime().exec(cmdArray);
        } catch (IOException e2) {
          try {
            // Third try: using open command
            String[] cmdArray = {"open", linkUrl};
            Process process = Runtime.getRuntime().exec(cmdArray);
          } catch (IOException e3) {
            code = -1;
          }
        }
      }

      // If none of the commands worked, try using the Desktop API.
      if (code == -1) {
        if (Desktop.isDesktopSupported()) {
          desktop = Desktop.getDesktop();
          try {
            URI uri = new URI(linkUrl);
            desktop.browse(uri);
          } catch (IOException | URISyntaxException ex) {
            code = -2;
          }
        } else {
          code = -1;
        }
      }
    } else {
      // For WINDOWS and other operating systems, use the Desktop API directly.
      //
      // Before more Desktop API is used, first check
      // whether the API is supported by this particular
      // virtual machine (VM) on this particular host.
      if (Desktop.isDesktopSupported()) {
        desktop = Desktop.getDesktop();
        try {
          URI uri = new URI(linkUrl);
          desktop.browse(uri);
        } catch (IOException | URISyntaxException ex) {
          code = -2;
        }
      } else {
        code = -1;
      }
    }
    return code;
  }
}
