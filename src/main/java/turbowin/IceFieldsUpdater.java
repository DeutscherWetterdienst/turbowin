package turbowin;

import static turbowin.main.*;

/** Updates the ice indication on the main observation screen. */
final class IceFieldsUpdater {

  private IceFieldsUpdater() {}

  static void update() {
    if (((IceState.ci_code.compareTo("") != 0) && (IceState.ci_code != null))
        || ((IceState.Si_code.compareTo("") != 0) && (IceState.Si_code != null))
        || ((IceState.bi_code.compareTo("") != 0) && (IceState.bi_code != null))
        || ((IceState.Di_code.compareTo("") != 0) && (IceState.Di_code != null))
        || ((IceState.zi_code.compareTo("") != 0) && (IceState.zi_code != null))) {
      // "u" means unable to report; all-u ice data must not be shown as present.
      if ((IceState.ci_code.trim().compareTo("u") != 0)
          || (IceState.Si_code.trim().compareTo("u") != 0)
          || (IceState.bi_code.trim().compareTo("u") != 0)
          || (IceState.Di_code.trim().compareTo("u") != 0)
          || (IceState.zi_code.trim().compareTo("u") != 0)) {
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
