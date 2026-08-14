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

  static String firstCharacter(String code, String missingValue) {
    // Cm7 may carry an a/b/c suffix; the composed observation protocol uses only its numeric first
    // character.
    if ((code != null) && (code.compareTo("") != 0)) {
      return code.substring(0, 1);
    }
    return missingValue;
  }

  static String windSpeed(String speed, String highSpeed, String separator) {
    // The optional 00fff group carries the extended fff00_code for wind speeds of at least 100
    // units; its 00 prefix identifies the extended high-speed group in the FM13 format.
    if ((speed != null) && (speed.compareTo("") != 0)) {
      if ((highSpeed != null) && (highSpeed.compareTo("") != 0)) {
        return speed + separator + "00" + highSpeed;
      }
      return speed;
    }
    return "//";
  }
}
