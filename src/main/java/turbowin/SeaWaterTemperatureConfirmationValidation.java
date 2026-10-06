package turbowin;

/** Handles level-three confirmation checks for seawater temperature. */
final class SeaWaterTemperatureConfirmationValidation {

  private SeaWaterTemperatureConfirmationValidation() {}

  static boolean validate(boolean temperatureValid, float temperature) {
    if (temperatureValid && temperature > 35.0) {
      if (!confirm(
          "Seawater temperature > 35.0 \u00B0C?\n Press the NO button if it was a typing error, press the YES button if this sewater temp is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
