package turbowin;

import javax.swing.JOptionPane;

/** Validates level-two consistency between wind and wind-wave observations. */
final class LevelTwoWindWaveValidation {

  private LevelTwoWindWaveValidation() {}

  static boolean validate(
      String windUnits,
      int windSpeed,
      boolean periodValid,
      float period,
      boolean heightValid,
      float height) {
    if (periodValid && windSpeed == 0 && period > 0.01 && period < 99.9) {
      return warning("if (true) wind speed is 0, wind waves period must be 0 or blank");
    }

    if (heightValid && windSpeed == 0 && height > 0.01 && height < 99.9) {
      return warning("if (true) wind speed is 0, wind waves height must be 0 or blank");
    }

    if (heightValid
        && windUnits.trim().indexOf(main.M_S) != -1
        && windSpeed >= 0
        && windSpeed <= 3
        && height > 9.7
        && height < 99.9) {
      return warning(
          "if (true) wind speed in range 0 - 3 m/s, wind waves height must be < 9.8 m or blank");
    }

    if (heightValid
        && windUnits.trim().indexOf(main.KNOTS) != -1
        && windSpeed >= 0
        && windSpeed <= 6
        && height > 9.7
        && height < 99.9) {
      return warning(
          "if (true) wind speed in range 0 - 6 knots, wind waves height must be < 9.8 m or blank");
    }

    return true;
  }

  private static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }
}
