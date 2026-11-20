package turbowin;

import static turbowin.main.*;

/** Updates the visibility field on the main observation screen. */
final class VisibilityFieldsUpdater {

  private VisibilityFieldsUpdater() {}

  static void update() {
    if ((VisibilityState.VV.compareTo("") != 0) && (VisibilityState.VV != null))
      jTextField18.setText(VisibilityState.VV);
    else jTextField18.setText("");

    coded_obs_update();
  }
}
