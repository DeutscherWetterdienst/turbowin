package turbowin;

import javax.swing.JOptionPane;

/** Provides the standard dialogs used by validation workflows. */
final class ValidationDialog {

  private ValidationDialog() {}

  static boolean confirm(String message) {
    if (JOptionPane.showConfirmDialog(
            null,
            message,
            main.APPLICATION_NAME + ", please confirm",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.NO_OPTION) {
      warning("Please correct the error (no final obs was coded)");
      return false;
    }
    return true;
  }

  static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }
}
