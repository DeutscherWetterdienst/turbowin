package turbowin;

import static turbowin.main.*;

/** Updates the icing indication on the main observation screen. */
final class IcingFieldsUpdater {

  private IcingFieldsUpdater() {}

  static void update() {
    if (((IcingState.Is_code.compareTo("") != 0) && (IcingState.Is_code != null))
        || ((IcingState.EsEs_code.compareTo("") != 0) && (IcingState.EsEs_code != null))
        || ((IcingState.Rs_code.compareTo("") != 0) && (IcingState.Rs_code != null))) {
      jTextField21.setText("present");
    } else {
      jTextField21.setText("");
    }

    coded_obs_update();
  }
}
