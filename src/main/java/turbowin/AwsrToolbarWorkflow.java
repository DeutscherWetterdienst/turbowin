package turbowin;

import static turbowin.main.*;

import java.awt.Color;
import javax.swing.JOptionPane;

/** Handles automated weather reporting (AWSR) toolbar state changes. */
final class AwsrToolbarWorkflow {

  private AwsrToolbarWorkflow() {}

  static boolean isBlank(String value) {
    return value.equals("");
  }

  static void handle() {
    // TODO add your handling code here:
    boolean checks_ok = true;
    boolean additional_checks_ok = true;

    AWSR = jCheckBox2.isSelected() == true; // now AWSR = true or false

    if (!AWSR) {
      // NB reset JLabel39 (e.g. in APR mode: "--- more than 30 minutes to go for next automated
      // upload, please do not insert observation data --- "
      //    must be reseted to original string
      main.jLabel39.setForeground(Color.BLACK);
      main.jLabel39.setText(
          "--- adding data: input menu, popup menu, toolbar icons or click on the text labels or fields ---");

      String info = "automated reporting (AWSR) is turned off";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
    } // if (!AWSR)

    if (AWSR) {
      // AWSR reporting interval
      if (checks_ok && isBlank(main.AWSR_reporting_interval)) {
        JOptionPane.showMessageDialog(
            null,
            "ASWR reporting interval not selected (Maintenance -> WOW/APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        AWSR = false;
        jCheckBox2.setSelected(false);
      }

      // AWSR send method
      if (checks_ok && (isBlank(main.APTR_AWSR_send_method))) {
        JOptionPane.showMessageDialog(
            null,
            "AWSR send method unknown (Maintenance -> WOW/APR/APTR/AWSR settings)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        checks_ok = false;
        AWSR = false;
        jCheckBox2.setSelected(false);
      }
      // warning checks
      if (checks_ok) {
        additional_checks_ok = WOW_APR_settings.AWSR_additional_requirements_checks();
      }

      // pop-up message AWSR was turned on
      if (checks_ok && additional_checks_ok) {
        String info = "automated reporting (AWSR) is turned on";
        JOptionPane.showMessageDialog(
            null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      } else {
        AWSR = false;
        jCheckBox2.setSelected(false);
      }
    } // if (AWSR)

    // NB below for AWSR turned on AND AWSR turned off !!
    // clear the text fields on the main screen (because maybe there are still values in the text
    // fields from a previous setting eg AWSR = true) and enable the output menu items again
    main.Reset_all_meteo_parameters();
    main.disable_and_enable_output_menu_items(); // in fact also for ENABLING the output menu
    // options if now set AWSR = false and before AWSR
    // = true

    // save the change
    main.schrijf_configuratie_regels();

    // set start-up sequence finished flag
    // turbowin_start_up_sequence_finished = true;
  }
}
