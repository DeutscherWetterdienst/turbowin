package turbowin;

/**
 * Prepares the observation values displayed by the leaflet map.
 *
 * <p>Map values use the following display conventions:
 *
 * <ul>
 *   <li>Wind direction is displayed in degrees, or as {@code variable} for variable wind.
 *   <li>Wind speed is displayed in m/s when the configured units contain {@link main#M_S};
 *       otherwise it falls back to knots.
 *   <li>Temperatures are displayed in degrees Celsius; integer values receive a {@code .0} decimal.
 *   <li>Mean sea-level pressure is displayed in hPa.
 *   <li>Values equal to the {@code INVALID} sentinel are displayed as empty values where
 *       applicable.
 * </ul>
 */
final class PositionMapPreparationWorkflow {

  private PositionMapPreparationWorkflow() {}

  static Observation prepare(
      String day,
      String month,
      String year,
      String hour,
      int windDirection,
      int variableWindDirection,
      int invalidValue,
      int windSpeed,
      String windUnits,
      String airTemperature,
      String seaWaterTemperature,
      String mslPressure) {
    return new Observation(
        day,
        month,
        year,
        hour,
        formatWindDirection(windDirection, variableWindDirection, invalidValue),
        formatWindSpeed(windSpeed, invalidValue, windUnits),
        formatTemperature(airTemperature),
        formatTemperature(seaWaterTemperature),
        formatPressure(mslPressure));
  }

  /**
   * Formats wind direction for the map. Variable wind is represented by the literal {@code
   * variable}; an invalid direction is represented by an empty value.
   */
  private static String formatWindDirection(
      int direction, int variableDirection, int invalidValue) {
    if (direction == variableDirection) {
      return "variable";
    }
    if (direction != invalidValue) {
      return Integer.toString(direction) + " degr";
    }
    return "";
  }

  /**
   * Formats wind speed for the map. Speeds are displayed in m/s when the configured units indicate
   * m/s; all other units, including unknown units, fall back to knots. An invalid speed is
   * represented by an empty value.
   */
  private static String formatWindSpeed(int speed, int invalidValue, String windUnits) {
    if (speed == invalidValue) {
      return "";
    }
    return Integer.toString(speed) + (windUnits.trim().indexOf(main.M_S) != -1 ? " m/s" : " knots");
  }

  /**
   * Formats a temperature in degrees Celsius. Integer temperatures are normalized with a trailing
   * {@code .0}; an empty or null temperature produces an empty value.
   */
  private static String formatTemperature(String temperature) {
    if (temperature.compareTo("") != 0 && temperature != null) {
      return temperature + (temperature.indexOf('.') == -1 ? ".0" : "") + " &#176C";
    }
    return "";
  }

  /** Formats mean sea-level pressure in hPa; an empty or null pressure produces an empty value. */
  private static String formatPressure(String pressure) {
    return (pressure.compareTo("") != 0 && pressure != null) ? pressure + " hPa" : "";
  }

  static final class Observation {
    final String day;
    final String month;
    final String year;
    final String hour;
    final String windDirection;
    final String windSpeed;
    final String airTemperature;
    final String seaWaterTemperature;
    final String mslPressure;

    private Observation(
        String day,
        String month,
        String year,
        String hour,
        String windDirection,
        String windSpeed,
        String airTemperature,
        String seaWaterTemperature,
        String mslPressure) {
      this.day = day;
      this.month = month;
      this.year = year;
      this.hour = hour;
      this.windDirection = windDirection;
      this.windSpeed = windSpeed;
      this.airTemperature = airTemperature;
      this.seaWaterTemperature = seaWaterTemperature;
      this.mslPressure = mslPressure;
    }
  }
}
