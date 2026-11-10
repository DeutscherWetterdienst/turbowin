package turbowin;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopUtils {

  static boolean isWebAddress(String address) {
    return address.contains("http") || address.contains("HTTP");
  }

  static boolean openWithRuntimeFallback(String linkUrl) {
    try {
      Runtime.getRuntime().exec(new String[] {"kde-open", linkUrl});
      return true;
    } catch (IOException ex) {
      try {
        Runtime.getRuntime().exec(new String[] {"xdg-open", linkUrl});
        return true;
      } catch (IOException ex2) {
        try {
          Runtime.getRuntime().exec(new String[] {"open", linkUrl});
          return true;
        } catch (IOException ex3) {
          return false;
        }
      }
    }
  }

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
      // Try platform commands before Desktop.browse: on Fedora, the Desktop call was observed
      // to delay opening the browser until TurboWin exited, without reporting an error.
      if (!openWithRuntimeFallback(linkUrl)) {
        code = -1;
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
