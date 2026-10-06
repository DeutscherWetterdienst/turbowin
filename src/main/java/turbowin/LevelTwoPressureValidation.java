package turbowin;

/** Validates level-two pressure characteristic and tendency consistency. */
final class LevelTwoPressureValidation {

  private LevelTwoPressureValidation() {}

  static boolean validate(
      boolean tendencyValid, String characteristic, String rawAmount, float amount) {
    if (tendencyValid && characteristic.equals("4") && rawAmount.equals("")) {
      return warning(
          "if air pressure characteristic is steady (a = 0), amount of pressure tendency must be 0");
    }

    if (tendencyValid && characteristic.equals("4") && amount > 0.01 && amount < 50.0) {
      return warning(
          "if air pressure characteristic is steady (a = 0), amount of pressure tendency must be 0");
    }

    return true;
  }

  private static boolean warning(String message) {
    return ValidationDialog.warning(message);
  }
}
