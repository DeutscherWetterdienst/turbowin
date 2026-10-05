package turbowin;

import javax.swing.JOptionPane;

/** Handles level-three confirmation checks for extreme air temperatures. */
final class AirTemperatureConfirmationValidation {

  private AirTemperatureConfirmationValidation() {}

  static boolean validate(boolean temperatureValid, float temperature) {
    if (temperatureValid && temperature > 50.0 && temperature < 99.9) {
      if (!confirm(
          "Air temperature > 50.0 \u00B0C \n Press the NO button if it was a typing error, press the YES button if this air temp is ok")) {
        return false;
      }
    }

    if (temperatureValid && temperature < -20.0 && temperature > -99.9) {
      if (!confirm(
          "Air temperature < -20.0 \u00B0C \n Press the NO button if it was a typing error, press the YES button if this air temp is ok")) {
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
