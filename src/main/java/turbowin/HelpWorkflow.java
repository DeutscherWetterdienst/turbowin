package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/** Resolves and opens the application's local or online help pages. */
final class HelpWorkflow {

  private HelpWorkflow() {}

  static String resolveHelpLocation(
      String applicationDirectory, String offlineHelpDirectory, String helpPage, String baseUrl) {
    String localPath =
        applicationDirectory
            + File.separator
            + ".."
            + File.separator
            + "runtime"
            + File.separator
            + offlineHelpDirectory
            + File.separator
            + helpPage;
    if (new File(localPath).isFile()) {
      return localPath;
    }
    try {
      return new URI(baseUrl + helpPage).toString();
    } catch (URISyntaxException ex) {
      return null;
    }
  }

  static void open(String helpPage) {
    String location =
        resolveHelpLocation(
            System.getProperty("app.dir"), OFFLINE_HELP_DIR, helpPage, URL_INTERNET_HELP);
    if (OSDetector.getOSString().equals("LINUX")) {
      support_class.open_browser_on_linux(location);
    } else {
      support_class.open_browser_on_not_linux(location);
    }
  }
}
