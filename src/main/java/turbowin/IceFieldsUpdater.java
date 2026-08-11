package turbowin;

import static turbowin.main.*;

/** Updates the ice indication on the main observation screen. */
final class IceFieldsUpdater {

  private IceFieldsUpdater() {}

  static void update() {
    if (((myice1.ci_code.compareTo("") != 0) && (myice1.ci_code != null))
        || ((myice1.Si_code.compareTo("") != 0) && (myice1.Si_code != null))
        || ((myice1.bi_code.compareTo("") != 0) && (myice1.bi_code != null))
        || ((myice1.Di_code.compareTo("") != 0) && (myice1.Di_code != null))
        || ((myice1.zi_code.compareTo("") != 0) && (myice1.zi_code != null))) {
      // "u" means unable to report; all-u ice data must not be shown as present.
      if ((myice1.ci_code.trim().compareTo("u") != 0)
          || (myice1.Si_code.trim().compareTo("u") != 0)
          || (myice1.bi_code.trim().compareTo("u") != 0)
          || (myice1.Di_code.trim().compareTo("u") != 0)
          || (myice1.zi_code.trim().compareTo("u") != 0)) {
        jTextField19.setText("present");
      } else {
        jTextField19.setText("");
      }
    } else {
      jTextField19.setText("");
    }

    coded_obs_update();
  }
}
