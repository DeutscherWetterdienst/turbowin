package turbowin;

/** Handles level-three confirmation checks for pressure tendency. */
final class PressureTendencyConfirmationValidation {

  private PressureTendencyConfirmationValidation() {}

  static boolean validateAmount(boolean amountValid, float amount) {
    if (amountValid && amount > 30.0 && amount <= 99.9) {
      return confirm(
          "pressure tendency amount, last 3 hours > 30.0 hPa\n Press the NO button if it was a typing error, press the YES button if this amount of pressure tendency is ok");
    }
    return true;
  }

  static boolean validateCharacteristic(
      boolean amountValid, float amount, boolean characteristicMissing) {
    if (amountValid && amount >= 0.0 && amount <= 99.9 && characteristicMissing) {
      return confirm(
          "Amount of pressure tendency available and characteristic of tendency not available?\n Press the NO button if it was a typing error, press the YES button if this observation is ok");
    }
    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
