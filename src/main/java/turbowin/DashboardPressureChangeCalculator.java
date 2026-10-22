package turbowin;

/** Calculates the three-hour pressure change shown on the serial dashboard. */
final class DashboardPressureChangeCalculator {

  private DashboardPressureChangeCalculator() {}

  static Double calculate(String pressureChange) {
    if (pressureChange.length() < 1 || pressureChange.indexOf("*") != -1) {
      return null;
    }

    try {
      double value = Math.round(Double.parseDouble(pressureChange) * 10) / 10.0d;
      return value > 100.0 ? Double.MAX_VALUE : value;
    } catch (NumberFormatException exception) {
      return Double.MAX_VALUE;
    }
  }
}
