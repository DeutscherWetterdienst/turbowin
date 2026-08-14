package turbowin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Converts simple FM13 ice codes to their EUCAWS representation.
 *
 * <p>The internal {@code u} value means TurboWin cannot report the value; EUCAWS therefore uses a
 * field-specific fallback code such as 14 or 30.
 */
final class AwsIceCodeFormatter {

  private AwsIceCodeFormatter() {}

  static String thickness(String code) {
    // FM13 ice thickness is in centimetres; convert to metres and round to two decimals for
    // EUCAWS.
    if (code.equals("//")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      double thickness = Double.parseDouble(code) / 100;
      BigDecimal roundedThickness = new BigDecimal(thickness).setScale(2, RoundingMode.HALF_UP);
      return Double.toString(roundedThickness.doubleValue());
    }
    return "";
  }

  static String direct(String code) {
    if (code.equals("/")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      return code;
    }
    return "";
  }

  static String unknownValue(String code, String replacement) {
    if (code.equals("/")) {
      return "";
    }
    if (code.equals("u")) {
      return replacement;
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      return code;
    }
    return "";
  }
}
