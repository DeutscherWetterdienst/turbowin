package turbowin;

/** Handles level-three confirmation checks for wind-wave measurements. */
final class WindWaveConfirmationValidation {

  private WindWaveConfirmationValidation() {}

  static boolean validate(boolean periodValid, float period, boolean heightValid, float height) {
    if (periodValid && period > 25.0 && period < 99.9) {
      if (!confirm(
          "Wind waves period > 25 seconds \n Press the NO button if it was a typing error, press the YES button if this wind waves period is ok")) {
        return false;
      }
    }

    if (heightValid && height > 12.2 && height < 99.9) {
      if (!confirm(
          "Wind waves height > 12.2 metres \n Press the NO button if it was a typing error, press the YES button if this wind waves height is ok")) {
        return false;
      }
    }

    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
