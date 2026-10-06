package turbowin;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;

/** Handles confirmation dialogs for the observation position-sequence workflow. */
final class PositionSequenceWorkflow {

  private PositionSequenceWorkflow() {}

  static boolean confirmDateTime(Calendar currentObservation) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH");
    String info =
        "-time sequence check-\nobs date/time ("
            + dateFormat.format(currentObservation.getTime())
            + ".00 UTC)";

    if (JOptionPane.showConfirmDialog(
            null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION)
        == JOptionPane.NO_OPTION) {
      JOptionPane.showMessageDialog(
          null,
          "Please correct the error (no final obs was coded)",
          main.APPLICATION_NAME + " warning",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }

  static boolean confirmPosition(String message) {
    if (JOptionPane.showConfirmDialog(
            null, message, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION)
        == JOptionPane.NO_OPTION) {
      JOptionPane.showMessageDialog(
          null,
          "Please correct the obs position (no final obs was coded)",
          main.APPLICATION_NAME + " message",
          JOptionPane.WARNING_MESSAGE);
      return false;
    }
    return true;
  }
}
