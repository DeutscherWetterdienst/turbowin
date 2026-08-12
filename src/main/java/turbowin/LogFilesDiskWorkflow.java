package turbowin;

import static turbowin.main.*;

import javax.swing.JOptionPane;

/** Owns confirmation and dispatch for moving meteorological logs to disk. */
final class LogFilesDiskWorkflow {

  private LogFilesDiskWorkflow() {}

  static String moveMode() {
    return MOVE_TO_DISK;
  }

  static void start() {
    String move_mode_logs = moveMode();
    String info = "";
    boolean doorgaan = true;

    info =
        "Uploading log files should be undertaken when it is intended to return the stored log files"
            + " to the National Meteorological Service.\nDo you wish to proceed";

    if (JOptionPane.showConfirmDialog(
            null, info, main.APPLICATION_NAME + " message", JOptionPane.YES_NO_OPTION)
        == JOptionPane.YES_OPTION) {
      doorgaan = true;
    } else {
      JOptionPane.showMessageDialog(
          null,
          "moving log files process cancelled",
          APPLICATION_NAME + " message",
          JOptionPane.INFORMATION_MESSAGE);
      doorgaan = false;
    }

    if (doorgaan) {
      support_class.Move_log_files(move_mode_logs);
    }
  }
}
