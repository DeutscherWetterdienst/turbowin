package turbowin;

/** Handles level-three confirmation checks for unusually high wind speeds. */
final class WindSpeedConfirmationValidation {

  private WindSpeedConfirmationValidation() {}

  static boolean validate(String windUnits, int windSpeed) {
    if (windUnits.trim().indexOf(main.M_S) != -1 && windSpeed > 28 && windSpeed < 500.0) {
      return confirm(
          "Wind speed > 28 m/s (> 55 knots) \n Press the NO button if it was a typing error, press the YES button if this wind speed is ok");
    }

    if (windUnits.trim().indexOf(main.KNOTS) != -1 && windSpeed > 55 && windSpeed < 500.0) {
      return confirm(
          "Wind speed > 55 knots \n Press the NO button if it was a typing error, press the YES button if this wind speed is ok");
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
