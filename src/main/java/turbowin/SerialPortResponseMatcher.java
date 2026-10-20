package turbowin;

/** Performs null-safe marker matching on responses read from serial ports. */
final class SerialPortResponseMatcher {

  private SerialPortResponseMatcher() {}

  static boolean contains(String response, String marker) {
    return response != null && response.contains(marker);
  }

  static boolean containsAny(String response, String... markers) {
    if (response == null) {
      return false;
    }
    for (String marker : markers) {
      if (response.contains(marker)) {
        return true;
      }
    }
    return false;
  }
}
