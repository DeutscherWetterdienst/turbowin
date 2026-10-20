package turbowin;

/** Extracts a PEUMB record from a line received from an AWS connection. */
final class PeumbRecordExtractor {

  private PeumbRecordExtractor() {}

  static String extract(String message) {
    int start = message.indexOf("$PEUMB");
    return start == -1 ? null : message.substring(start).replaceAll("[\r\n]", "");
  }
}
