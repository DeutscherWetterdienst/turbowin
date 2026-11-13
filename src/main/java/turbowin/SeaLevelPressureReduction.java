package turbowin;

/** Temperature-dependent station-to-sea-level pressure correction, in hPa. */
final class SeaLevelPressureReduction {

  private SeaLevelPressureReduction() {}

  // Callers must supply outside air temperature from the current observation, after record-age
  // and integrity checks. Null means that no suitable measurement is available.
  static double heightCorrection(double correctedPressure, double height, String airTemperature) {
    return correctedPressure * height / (29.27 * (temperatureCelsius(airTemperature) + 273.15));
  }

  private static double temperatureCelsius(String measurement) {
    if (measurement != null) {
      try {
        double temperature = Double.parseDouble(measurement.trim());
        // Matches the StarX outside-air range; also excludes missing/error sentinels.
        if (Double.isFinite(temperature) && temperature > -70.0 && temperature < 70.0) {
          return temperature;
        }
      } catch (NumberFormatException ignored) {
        // Missing, flagged, or malformed measurement: use the standard atmosphere.
      }
    }
    return 15.0;
  }
}
