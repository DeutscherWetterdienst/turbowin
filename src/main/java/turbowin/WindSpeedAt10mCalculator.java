package turbowin;

/** Converts an anemometer-height wind speed to the equivalent 10-meter wind speed. */
final class WindSpeedAt10mCalculator {

  private WindSpeedAt10mCalculator() {}

  static double calculate(String heightAnemometer, double windSpeedAtAnemometerHeight) {
    final double roughnessLength = 0.0002;
    int height = Integer.MAX_VALUE;
    double windSpeedAt10m = main.INVALID;

    if (!heightAnemometer.trim().equals("")) {
      try {
        // Preserve the legacy behavior: the value is checked for blankness after trimming,
        // but the original string is passed to parseInt without trimming.
        height = Integer.parseInt(heightAnemometer);
      } catch (NumberFormatException exception) {
        height = Integer.MAX_VALUE;
      }
    }

    if ((height > 10 && height < 200)
        && (windSpeedAtAnemometerHeight >= 0 && windSpeedAtAnemometerHeight < 500)) {
      double conversionFactor = Math.log(10 / roughnessLength) / Math.log(height / roughnessLength);
      windSpeedAt10m = windSpeedAtAnemometerHeight * conversionFactor;
    }

    return windSpeedAt10m;
  }
}
