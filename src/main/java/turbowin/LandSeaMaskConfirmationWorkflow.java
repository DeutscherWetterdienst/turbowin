package turbowin;

import static turbowin.main.INVALID;

import javax.swing.JOptionPane;

/** Coordinates the position confirmation step of the land-sea mask check. */
final class LandSeaMaskConfirmationWorkflow {

  private LandSeaMaskConfirmationWorkflow() {}

  static boolean run() {
    // called from doInBackground() 4x
    //    - Output_Obs_to_server_menu_actionPerformed() [main.java]
    //    - Output_obs_by_email_all_manual() [main.java]
    //    - Output_obs_to_file_actionPerformed() [main.java]
    //    - Output_obs_to_clipboard_actionPerformed() [main.java]

    // NB om de grootte van de jar te beperken wordt er alleen gecheckt op 1 graads vak niveau
    // (zgf_sam)
    //    en (i.t.t. TurboWin) niet op 1/10 graads vak niveau (zgf_lkw)

    int Octant = INVALID;
    boolean zee_vak_ok = true;

    System.out.println("--- Checking entered position against a land-sea mask");

    LandSeaMaskPositionCalculator.Position maskPosition =
        LandSeaMaskPositionCalculator.calculate(
            myposition.latitude_hemisphere,
            myposition.longitude_hemisphere,
            myposition.int_latitude_degrees,
            myposition.int_longitude_degrees);
    Octant = maskPosition.octant();
    zee_vak_ok = LandSeaMaskWorkflow.check(maskPosition);

    if (zee_vak_ok == false) {
      String info = "";
      info = "-land mask check-\n";
      info += "obs position:\n";
      info += myposition.latitude_degrees;
      info += "\u00B0 ";
      info += myposition.latitude_minutes;
      info += "' ";
      info += myposition.latitude_hemisphere;
      info += "  ";
      info += myposition.longitude_degrees;
      info += "\u00B0 ";
      info += myposition.longitude_minutes;
      info += "' ";
      info += myposition.longitude_hemisphere;
      info += "\n";

      if (JOptionPane.showConfirmDialog(
              null, info, main.APPLICATION_NAME + " please confirm", JOptionPane.YES_NO_OPTION)
          == JOptionPane.NO_OPTION) {
        JOptionPane.showMessageDialog(
            null,
            "Please correct the error (no final obs was coded)",
            main.APPLICATION_NAME + " warning",
            JOptionPane.WARNING_MESSAGE);
        zee_vak_ok = false;
      } else {
        zee_vak_ok = true;
      }
    } // if (zee_vak_ok == false)

    return zee_vak_ok;
  }
}
