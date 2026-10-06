package turbowin;

/** Handles level-three confirmation checks for swell measurements. */
final class SwellConfirmationValidation {

  private SwellConfirmationValidation() {}

  static boolean validate(
      boolean firstPeriodValid,
      float firstPeriod,
      boolean firstHeightValid,
      float firstHeight,
      boolean secondPeriodValid,
      float secondPeriod,
      boolean secondHeightValid,
      float secondHeight) {
    if (firstPeriodValid && firstPeriod > 25.0 && firstPeriod < 99.9) {
      if (!confirm(
          "First swell system waves period > 25 seconds \n Press the NO button if it was a typing error, press the YES button if this first swell system waves period is ok")) {
        return false;
      }
    }
    if (firstHeightValid && firstHeight > 12.2 && firstHeight < 99.9) {
      if (!confirm(
          "First swell system waves height > 12.2 metres \n Press the NO button if it was a typing error, press the YES button if this first swell system height waves is ok")) {
        return false;
      }
    }
    if (secondPeriodValid && secondPeriod > 25.0 && secondPeriod < 99.9) {
      if (!confirm(
          "Second swell system waves period > 25 seconds \n Press the NO button if it was a typing error, press the YES button if this second swell system waves period is ok")) {
        return false;
      }
    }
    if (secondHeightValid && secondHeight > 12.2 && secondHeight < 99.9) {
      if (!confirm(
          "Second swell system waves height > 12.2 metres \n Press the NO button if it was a typing error, press the YES button if this second swell system height waves is ok")) {
        return false;
      }
    }
    return true;
  }

  private static boolean confirm(String message) {
    return ValidationDialog.confirm(message);
  }
}
