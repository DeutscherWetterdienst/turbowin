package turbowin;

/** Handles level-three confirmation checks for ice-accretion thickness. */
final class IceThicknessConfirmationValidation {

  private IceThicknessConfirmationValidation() {}

  static boolean validate(boolean thicknessValid, float thickness) {
    if (thicknessValid && thickness > 20.0) {
      if (!confirm(
          "Ice thickness > 20.0 centimetres?\n Press the NO button if it was a typing error, press the YES button if this ice thickness is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
