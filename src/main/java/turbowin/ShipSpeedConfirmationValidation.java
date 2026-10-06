package turbowin;

/** Handles level-three confirmation checks for ship speed. */
final class ShipSpeedConfirmationValidation {

  private ShipSpeedConfirmationValidation() {}

  static boolean validate(String speedCode) {
    if (speedCode.equals("8") || speedCode.equals("9")) {
      if (!confirm(
          "Ship's speed > 35 knots \n Press the NO button if it was a typing error, press the YES button if this average speed is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
