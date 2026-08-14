package turbowin;

/** Formats direct observation codes for the EUCAWS message. */
final class AwsObservationCodeFormatter {

  private AwsObservationCodeFormatter() {}

  static String direct(String code, String emptyMarker) {
    if (code.equals(emptyMarker)) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      return code;
    }
    return "";
  }
}
