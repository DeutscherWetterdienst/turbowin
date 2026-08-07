package turbowin;

import static turbowin.main.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.UnknownHostException;
import javax.net.ssl.HttpsURLConnection;

/**
 * Executes the legacy Format 101 retry and address-failover policy.
 *
 * <p>All resolved addresses are tried because a transient routing problem or TCP reset can affect
 * one address while another succeeds. Connection failures are retried with exponential backoff so
 * transient server/network conditions can recover without overwhelming the endpoint.
 */
final class Format101ServerClient {

  private Format101ServerClient() {}

  static String execute(String url, boolean isHttps)
      throws InterruptedException, java.net.URISyntaxException {
    int responseCode = OK_RESPONSE_FORMAT_101;
    String responseString = "";
    int maxRetries = 3;
    boolean success = false;

    try {
      InetAddress[] addresses = InetAddress.getAllByName(new URI(url).toURL().getHost());

      for (InetAddress address : addresses) {
        System.out.println("--- Trying IP: " + address.getHostAddress());
        int attempt = 0;
        int backoff = 1000;

        while (attempt < maxRetries && !success) {
          attempt++;

          HttpURLConnection connection = null;

          try {
            String message =
                "[MANUAL] sending 'GET' request ("
                    + (isHttps ? "https" : "http")
                    + ") to URL: "
                    + url;
            main.log_turbowin_system_message(message);

            URI uri = new URI(url);

            if (isHttps) {
              connection = (HttpsURLConnection) uri.toURL().openConnection();
            } else {
              connection = (HttpURLConnection) uri.toURL().openConnection();
            }

            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();

          } catch (MalformedURLException | java.net.URISyntaxException ex) {
            responseCode = RESPONSE_MALFORMED_URL;
            responseString = ex.getMessage();

          } catch (java.net.SocketException ex) {
            responseCode = RESPONSE_NO_INTERNET;
            responseString = ex.getMessage();

            Thread.sleep(backoff);
            backoff *= 2;

          } catch (IOException ex) {
            responseCode = RESPONSE_NO_INTERNET;
            responseString = ex.getMessage();

            Thread.sleep(backoff);
            backoff *= 2;

          } finally {
            // Always disconnect to free up resources, including after failed attempts.
            if (connection != null) {
              connection.disconnect();
            }
          }

          if (responseCode == 200) {
            success = true;
          }
        }

        if (success) {
          break;
        }
      }

      if (!success) {
        System.out.println("--- All IP addresses failed after retries.");
      }

    } catch (UnknownHostException | MalformedURLException ex) {
      responseCode = RESPONSE_NO_INTERNET;
      responseString = ex.getMessage();
    }

    String response = Integer.toString(responseCode);

    if (!responseString.equals("")) {
      response += " (" + responseString + ")";
    }

    return response;
  }
}
