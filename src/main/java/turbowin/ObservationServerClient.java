package turbowin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Performs a single HTTP GET and returns its response code.
 *
 * <p>The method is intentionally GET for both HTTP and HTTPS. Do not enable output on the
 * connection: {@code URLConnection#setDoOutput(true)} implicitly changes the request method to
 * POST.
 */
final class ObservationServerClient {

  private ObservationServerClient() {}

  static int getResponseCode(String url)
      throws MalformedURLException, URISyntaxException, IOException {
    return getResponseCode(url, uri -> (HttpURLConnection) uri.toURL().openConnection());
  }

  static int getResponseCode(String url, ConnectionOpener connectionOpener)
      throws MalformedURLException, URISyntaxException, IOException {
    HttpURLConnection connection = connectionOpener.open(new URI(url));
    connection.setRequestMethod("GET");
    return connection.getResponseCode();
  }

  @FunctionalInterface
  interface ConnectionOpener {
    HttpURLConnection open(URI uri) throws IOException;
  }
}
