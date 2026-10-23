package turbowin;

/** Calculates dew point temperature from air temperature, relative humidity, and pressure. */
final class DewPointCalculator {

  private static final double C1 = 6.112;
  private static final double C2 = 17.62;
  private static final double C3 = 243.12;
  private static final double DEFAULT_PRESSURE_FACTOR = 1.0047187;

  private DewPointCalculator() {}

  static double calculate(String airTemperature, double relativeHumidity, String mslPressure) {
    if (airTemperature == null || airTemperature.trim().isEmpty()) {
      return main.INVALID;
    }

    try {
      return calculate(Double.parseDouble(airTemperature.trim()), relativeHumidity, mslPressure);
    } catch (NumberFormatException exception) {
      return main.INVALID;
    }
  }

  static double calculate(double airTemperature, double relativeHumidity, String mslPressure) {
    if (!Double.isFinite(airTemperature)
        || !Double.isFinite(relativeHumidity)
        || relativeHumidity < 0.0
        || relativeHumidity > 1.0
        || airTemperature < -75.0
        || airTemperature > 75.0) {
      return main.INVALID;
    }

    // At saturation, using the air temperature avoids a small negative value caused by rounding
    // in the logarithmic formula (for example, at 0 °C and 100 % RH).
    if (relativeHumidity >= 0.99) {
      return airTemperature;
    }

    double pressureFactor = pressureFactor(mslPressure);
    double saturationVaporPressure =
        pressureFactor * C1 * Math.exp(C2 * airTemperature / (C3 + airTemperature));
    double dewPoint = C3 / (-1.0 + C2 / Math.log(relativeHumidity * saturationVaporPressure / C1));

    return Double.isFinite(dewPoint) ? dewPoint : main.INVALID;
  }

  private static double pressureFactor(String mslPressure) {
    if (mslPressure == null || mslPressure.trim().isEmpty()) {
      return DEFAULT_PRESSURE_FACTOR;
    }

    try {
      double pressure = Double.parseDouble(mslPressure.trim());
      if (pressure > 900.0 && pressure < 1100.0) {
        return 1.0016 + 0.00000315 * pressure - 0.074 / pressure;
      }
    } catch (NumberFormatException exception) {
      // Use the standard pressure factor when the optional pressure value is malformed.
    }

    return DEFAULT_PRESSURE_FACTOR;
  }
}
