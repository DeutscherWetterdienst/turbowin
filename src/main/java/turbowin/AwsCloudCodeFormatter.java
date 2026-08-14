package turbowin;

/**
 * Converts FM13 cloud codes to their EUCAWS representation.
 *
 * <p>BUFR table 020012 requires offsets 10 (low), 20 (middle), and 30 (high). Middle-cloud Cm
 * values may be 7a, 7b, or 7c, so those values use only their first digit before applying the
 * offset.
 */
final class AwsCloudCodeFormatter {

  private AwsCloudCodeFormatter() {}

  static String convert(String code, int offset, String label) {
    return convert(code, offset, label, false);
  }

  static String convertFirstDigit(String code, int offset, String label) {
    return convert(code, offset, label, true);
  }

  private static String convert(String code, int offset, String label, boolean firstDigit) {
    if (code.equals("/")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      try {
        String value = firstDigit ? code.substring(0, 1) : code;
        return Integer.toString(Integer.parseInt(value) + offset);
      } catch (NumberFormatException ex) {
        System.out.println("+++ Error compile obs for AWS; cloud type " + label + " " + ex);
      }
    }
    return "";
  }
}
