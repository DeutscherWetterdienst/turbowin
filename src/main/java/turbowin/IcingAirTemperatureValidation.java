package turbowin;

import javax.swing.JOptionPane;

/** Validates icing observations against air temperature. */
final class IcingAirTemperatureValidation {

  private IcingAirTemperatureValidation() {}

  static boolean validate(boolean airTemperatureValid, float airTemperature, boolean icingPresent) {
    if (airTemperatureValid && airTemperature > 20.0 && airTemperature < 99.9 && icingPresent) {
      return warning(
          "If 'air temperature' > 20.0 \u00B0C then 'Icing (Ice accretion)' is not possible");
    }

    return true;
  }

  private static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }
}
