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

  static String iceCause(String code) {
    // FM13 icing causes map to EUCAWS complementary-code table values: spray, fog, rain, and
    // combinations are represented by the explicit mappings below; some EUCAWS values are not
    // present in FM13.
    // See "EUCAWS inputs/outputs complementary information about codes", Pierre Blouch.
    if (code.equals("/")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      switch (code) {
        case "1": // icing from spray (FM13 code)
          return "8";
        case "2": // icing from fog (FM13 code)
          return "4";
        case "3": // icing from spray and fog (FM13 code)
          return "12";
        case "4": // icing from rain (FM13 code)
          return "2";
        case "5": // icing from spray and rain (FM13 code)
          return "10";
        case "6": // icing from fog and rain (not present in FM13 code)
          return "6";
        case "14": // icing from spray, fog, and rain (not present in FM13 code)
          return "14";
        default:
          return "";
      }
    }
    return "";
  }

  static String iceBearing(String code) {
    // FM13 0 means ship in shore or flaw lead; u means unable to report. Neither is encoded here
    // because this field only represents direction and has no supporting BUFR code table.
    if (code.equals("/") || code.equals("u") || code.equals("0")) {
      return "";
    }
    switch (code) {
      case "1":
        return "45";
      case "2":
        return "90";
      case "3":
        return "135";
      case "4":
        return "180";
      case "5":
        return "225";
      case "6":
        return "270";
      case "7":
        return "315";
      case "8":
        return "360";
      default:
        return "";
    }
  }
}
