package turbowin;

/** Handles level-three confirmation checks for MSL air pressure. */
final class AirPressureConfirmationValidation {

  private AirPressureConfirmationValidation() {}

  static boolean validate(boolean pressureValid, float pressure) {
    if (pressureValid && !(pressure >= 910.0 && pressure <= 1050.0)) {
      if (!confirm(
          "Air pressure (MSL) < 950.0 hPa or > 1050.0 hPa\n Press the NO button if it was a typing error, press the YES button if this air pressure (MSL) is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
