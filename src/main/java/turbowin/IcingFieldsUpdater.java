package turbowin;

import static turbowin.main.*;

/** Updates the icing indication on the main observation screen. */
final class IcingFieldsUpdater {

  private IcingFieldsUpdater() {}

  static void update() {
    if (((myicing.Is_code.compareTo("") != 0) && (myicing.Is_code != null))
        || ((myicing.EsEs_code.compareTo("") != 0) && (myicing.EsEs_code != null))
        || ((myicing.Rs_code.compareTo("") != 0) && (myicing.Rs_code != null))) {
      jTextField21.setText("present");
    } else {
      jTextField21.setText("");
    }

    coded_obs_update();
  }
}
