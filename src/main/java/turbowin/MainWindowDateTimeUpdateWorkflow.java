package turbowin;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

final class MainWindowDateTimeUpdateWorkflow {

  private MainWindowDateTimeUpdateWorkflow() {}

  // Modes 3 (EUCAWS), 9/10 (OMC-140 serial/LAN), and 11 (AMOS2X), plus APR, need the notice
  // when the connected AWS/barometer timer is not already updating the date/time on screen.
  static boolean shouldShowUpdateNotice(int connectionMode, boolean apr) {
    return connectionMode == 3
        || connectionMode == 9
        || connectionMode == 10
        || connectionMode == 11
        || apr;
  }

  static void showIfNeeded(int connectionMode, boolean apr) {
    if (!shouldShowUpdateNotice(connectionMode, apr)) {
      return;
    }

    JOptionPane pane =
        new JOptionPane(
            "Screen will be updated within max 1 minute",
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[] {},
            null);
    JDialog updatingDialog = pane.createDialog(main.APPLICATION_NAME);

    Timer timer =
        new Timer(
            2000,
            event -> {
              updatingDialog.dispose();
            });
    timer.setRepeats(false);
    timer.start();
    updatingDialog.setVisible(true);
  }
}
