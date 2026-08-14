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
}
