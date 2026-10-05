package turbowin;

import javax.swing.JOptionPane;

/** Handles level-three confirmation checks for MSL air pressure. */
final class AirPressureConfirmationValidation {

  private AirPressureConfirmationValidation() {}

  static boolean validate(boolean pressureValid, float pressure) {
    if (pressureValid && !(pressure >= 910.0 && pressure <= 1050.0)) {
      if (!confirm(
          "Air pressure (MSL) < 950.0 hPa or > 1050.0 hPa\n Press the NO button if it was a typing error, press the YES button if this air pressure (MSL) is ok")) {
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
