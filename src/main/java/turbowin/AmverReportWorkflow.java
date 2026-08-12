package turbowin;

import static turbowin.main.*;

import javax.swing.JOptionPane;

/** Opens AMVER report forms while preventing multiple active reports. */
final class AmverReportWorkflow {

  private AmverReportWorkflow() {}

  static boolean reportAlreadyOpen() {
    return !amver_report.equals("");
  }

  static void open(String reportType, int width, int height) {
    if (reportAlreadyOpen()) {
      JOptionPane.showMessageDialog(
          null,
          "Please close first a previously opened AMVER form",
          main.APPLICATION_NAME + " message",
          JOptionPane.WARNING_MESSAGE);
    } else {
      amver_report = reportType;
      myamversailingplan form = new myamversailingplan();
      form.setSize(width, height);
      form.setVisible(true);
    }
  }
}
