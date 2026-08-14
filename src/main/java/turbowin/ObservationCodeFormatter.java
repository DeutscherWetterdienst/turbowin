package turbowin;

/** Applies legacy observation-code fallbacks without changing null semantics. */
final class ObservationCodeFormatter {

  private ObservationCodeFormatter() {}

  static String direct(String code, String missingValue) {
    if ((code != null) && (code.compareTo("") != 0)) {
      return code;
    }
    return missingValue;
  }

  static String combined(String prefix, String value, String missingValue) {
    if ((prefix != null)
        && (prefix.compareTo("") != 0)
        && (value != null)
        && (value.compareTo("") != 0)) {
      return prefix + value;
    }
    return missingValue;
  }

  static String iceValue(String code) {
    // Internal "u" means unable to report; legacy observation output represents it as the slash
    // missing marker.
    if ((code != null) && (code.compareTo("") != 0)) {
      if (code.trim().equals("u")) {
        return "/";
      }
      return code;
    }
    return "/";
  }
}
