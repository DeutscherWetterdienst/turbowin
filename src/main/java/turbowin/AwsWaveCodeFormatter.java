package turbowin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Converts FM13 wave codes to their EUCAWS representation. */
final class AwsWaveCodeFormatter {

  private AwsWaveCodeFormatter() {}

  static String period(String code) {
    // EUCAWS/BUFR cannot represent FM13 wave period 99; parsing also intentionally removes
    // leading zeroes from valid period codes.
    if (code.equals("//") || code.equals("99")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      return Integer.toString(Integer.parseInt(code));
    }
    return "";
  }

  static String height(String code) {
    // FM13 wave heights are half-metre values; convert them to metres and round to one decimal
    // place for EUCAWS, with 99 as the unavailable sentinel.
    if (code.equals("//") || code.equals("99")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      double height = Double.parseDouble(code) / 2;
      BigDecimal roundedHeight = new BigDecimal(height).setScale(1, RoundingMode.HALF_UP);
      return Double.toString(roundedHeight.doubleValue());
    }
    return "";
  }

  static String direction(String code) {
    // A missing FM13 wave-direction code, including the 99 sentinel, is omitted from EUCAWS.
    if (code.equals("//") || code.equals("99")) {
      return "";
    }
    if ((code != null) && (code.compareTo("") != 0)) {
      return code + "0";
    }
    return "";
  }
}
