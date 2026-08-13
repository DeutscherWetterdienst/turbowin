package turbowin;

import static turbowin.main.*;

import java.awt.Color;
import javax.swing.JOptionPane;

/** Handles automated reporting (APR/APTR) toolbar state changes. */
final class AprToolbarWorkflow {

  private AprToolbarWorkflow() {}

  static boolean isBlank(String value) {
    return ReportingSettingsValidation.isBlank(value);
  }

  static boolean isValidDraught(String value) {
    try {
      double draught = Double.parseDouble(value);
      return !value.equals("") && draught >= 0 && draught <= 50;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  static boolean isValidBarometerCorrection(String value) {
    try {
      double correction = Double.parseDouble(value.trim());
      return !value.equals("") && (correction >= -4.0 || correction <= 4.0);
    } catch (NumberFormatException e) {
      return false;
    }
  }

  static void handle() {
    // TODO add your handling code here:
    boolean checks_ok = true;
    boolean additional_checks_ok = true;

    APR = jCheckBox1.isSelected() == true; // now APR = true or false

    if (!APR) {
      // NB reset JLabel39 (e.g. in APR mode: "--- more than 30 minutes to go for next automated
      // upload, please do not insert observation data --- "
      //    must be reseted to original string
      main.jLabel39.setForeground(Color.BLACK);
      main.jLabel39.setText(
          "--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");

      String info = "automated reporting (AP[&T]R) is turned off";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
    } // if (!APR)

    if (APR) {
      // AP[&T]R reporting interval
      if (checks_ok && isBlank(main.APR_reporting_interval)) {
        JOptionPane.showMessageDialog(
            null,
            "AP[&T]R reporting interval not selected (Maintenance -> APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        APR = false;
        jCheckBox1.setSelected(false);
      }

      // AP[&T]R send method
      if (checks_ok && isBlank(main.APTR_AWSR_send_method)) {
        JOptionPane.showMessageDialog(
            null,
            "AP[&T]R / AWSR send method unknown (Maintenance -> APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        APR = false;
        jCheckBox1.setSelected(false);
      }

      // AP[&T]R draught
      if (checks_ok) {
        try {
          double double_WOW_APR_average_draught = Double.parseDouble(main.WOW_APR_average_draught);

          if (!isValidDraught(main.WOW_APR_average_draught)) {
            JOptionPane.showMessageDialog(
                null,
                "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> APR/APTR/AWSR settings)",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            APR = false;
            jCheckBox1.setSelected(false);
          }
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(
              null,
              "normal steaming draft not in range 0.0 - 50.0 (Maintenance -> APR/APTR/AWSR settings)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          checks_ok = false;
          APR = false;
          jCheckBox1.setSelected(false);
        }
      } // if (checks_ok)

      // AP[&T]R barometer ic
      if (checks_ok) {
        try {
          double double_barometer_instrument_correction =
              Double.parseDouble(main.barometer_instrument_correction.trim());

          if (!isValidBarometerCorrection(main.barometer_instrument_correction)) {
            JOptionPane.showMessageDialog(
                null,
                "barometer instrument correction not in range -4.0 - 4.0 (Maintenance -> APR/APTR/AWSR settings)",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
            checks_ok = false;
            APR = false;
            jCheckBox1.setSelected(false);
          }
        } catch (NumberFormatException e) {
          JOptionPane.showMessageDialog(
              null,
              "barometer_instrument_correction not in range -4.0 - 4.0 (Maintenance -> APR/APTR/AWSR settings)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          checks_ok = false;
          APR = false;
          jCheckBox1.setSelected(false);
        }
      } // if (checks_ok)

      // warning checks
      if (checks_ok) {
        additional_checks_ok = WOW_APR_settings.APR_additional_requirements_checks();
      }

      // pop-up message APR was turned on
      if (checks_ok && additional_checks_ok) {
        String info = "automated reporting (AP[&T]R) is turned on";
        JOptionPane.showMessageDialog(
            null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      } else {
        APR = false;
        jCheckBox1.setSelected(false);
      }
    } // if (APR)

    // NB below for APR turned on AND APR turned off !!
    // clear the text fields on the main screen (because maybe there are still values in the text
    // fields from a previous setting eg APR = true) and enable the output menu items again
    AutomatedReportingWorkflow.finishToggle();

    // set start-up sequence finished flag
    // turbowin_start_up_sequence_finished = true;
  }
}
