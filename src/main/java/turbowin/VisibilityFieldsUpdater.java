package turbowin;

import static turbowin.main.*;

/** Updates the visibility field on the main observation screen. */
final class VisibilityFieldsUpdater {

  private VisibilityFieldsUpdater() {}

  static void update() {
    if ((myvisibility.VV.compareTo("") != 0) && (myvisibility.VV != null))
      jTextField18.setText(myvisibility.VV);
    else jTextField18.setText("");

    coded_obs_update();
  }
}
