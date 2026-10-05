package turbowin;

import javax.swing.JOptionPane;

/** Handles level-three confirmation checks for ship speed. */
final class ShipSpeedConfirmationValidation {

  private ShipSpeedConfirmationValidation() {}

  static boolean validate(String speedCode) {
    if (speedCode.equals("8") || speedCode.equals("9")) {
      if (!confirm(
          "Ship's speed > 35 knots \n Press the NO button if it was a typing error, press the YES button if this average speed is ok")) {
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
