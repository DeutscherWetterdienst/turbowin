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

  static boolean confirmLevelThree(
      boolean airTemperatureValid, float airTemperature, boolean icingPresent) {
    if (airTemperatureValid && airTemperature > 4.0 && airTemperature < 99.9 && icingPresent) {
      return confirm(
          "Air temperature > 4.0 \u00B0C and Icing (Ice accretion)\n Press the NO button if it was a typing error, press the YES button if this observation is ok");
    }

    return true;
  }

  private static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }

  private static boolean confirm(String message) {
    if (JOptionPane.showConfirmDialog(
            null,
            message,
            main.APPLICATION_NAME + ", please confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.NO_OPTION) {
      JOptionPane.showMessageDialog(
          null,
          "Please correct the error (no final obs was coded)",
          main.APPLICATION_NAME,
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }
}
