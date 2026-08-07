package turbowin;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Builds upload URLs without opening a network connection.
 *
 * <p>FM13 payloads intentionally remain unencoded. Format 101 encodes only the observation value
 * as application/x-www-form-urlencoded data; UTF-8 is required for compatibility. The URL and
 * its query separators are not encoded.
 */
final class ServerObservationRequestBuilder {

  private ServerObservationRequestBuilder() {}

  static String fm13Url(String uploadUrl, String observation) {
    return uploadUrl + "obs=" + observation;
  }

  static String format101Url(String uploadUrl, String observation) {
    return uploadUrl + "obs=" + URLEncoder.encode(observation, StandardCharsets.UTF_8);
  }
}
