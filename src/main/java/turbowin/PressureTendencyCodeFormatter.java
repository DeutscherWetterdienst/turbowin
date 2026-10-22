package turbowin;

/** Formats pressure tendency as the three-character IMMT ppp code. */
final class PressureTendencyCodeFormatter {

  private PressureTendencyCodeFormatter() {}

  static String format(double pressureTendency) {
    int code = (int) Math.floor(pressureTendency * 10 + 0.5);
    String formatted = Integer.toString(code);
    if (formatted.length() < 3) {
      formatted = "0000000000".substring(0, 3 - formatted.length()) + formatted;
    }
    return formatted;
  }
}
