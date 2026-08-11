package turbowin;

import static turbowin.main.*;

/** Updates ship name and station identifier fields on the main screen. */
final class IdentifierFieldsUpdater {

  private IdentifierFieldsUpdater() {}

  static void update() {
    jTextField1.setText("");
    if ((ship_name.compareTo("") != 0) && (ship_name != null)) {
      jTextField1.setText(ship_name);
    }

    jTextField2.setText("");
    // The station field historically represented the masked call sign; station_ID is the current
    // identifier used for that mapping.
    if ((station_ID.compareTo("") != 0) && (station_ID != null)) {
      jTextField2.setText(station_ID);
    }

    coded_obs_update();
  }
}
