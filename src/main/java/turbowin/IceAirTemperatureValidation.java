package turbowin;

import javax.swing.JOptionPane;

/** Validates ice observations against air temperature. */
final class IceAirTemperatureValidation {

  private IceAirTemperatureValidation() {}

  static boolean validate(boolean airTemperatureValid, float airTemperature, boolean icePresent) {
    if (airTemperatureValid && airTemperature > 25.0 && airTemperature < 99.9 && icePresent) {
      return warning("If 'air temperature' > 25.0 \u00B0C then 'Ice' is not possible");
    }

    return true;
  }

  private static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }
}
