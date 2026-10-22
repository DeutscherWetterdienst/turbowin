package turbowin;

/**
 * Formats corrected pressure values as the four-character IMMT PPPP code.
 *
 * <p>The corrected pressure is in hPa, and PPPP encodes it in tenths of hPa without the
 * thousands digit. Therefore, rounded values at or above 10000 have 10000 removed before the
 * result is padded with leading zeroes to exactly four characters.
 */
final class PressureCodeFormatter {

  private PressureCodeFormatter() {}

  static String format(double correctedPressure) {
    int code = (int) Math.floor(correctedPressure * 10 + 0.5);
    if (code >= 10000) {
      code -= 10000;
    }

    String formatted = Integer.toString(code);
    if (formatted.length() < 4) {
      formatted = "0000000000".substring(0, 4 - formatted.length()) + formatted;
    }
    return formatted;
  }
}
