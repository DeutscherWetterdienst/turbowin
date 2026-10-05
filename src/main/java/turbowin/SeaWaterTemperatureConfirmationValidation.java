package turbowin;

import javax.swing.JOptionPane;

/** Handles level-three confirmation checks for seawater temperature. */
final class SeaWaterTemperatureConfirmationValidation {

  private SeaWaterTemperatureConfirmationValidation() {}

  static boolean validate(boolean temperatureValid, float temperature) {
    if (temperatureValid && temperature > 35.0) {
      if (!confirm(
          "Seawater temperature > 35.0 \u00B0C?\n Press the NO button if it was a typing error, press the YES button if this sewater temp is ok")) {
        return false;
      }
    }

    return true;
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
