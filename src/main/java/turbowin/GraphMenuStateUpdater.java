package turbowin;

import javax.swing.JMenuItem;

/** Updates graph menu items according to the connected instrument modes. */
final class GraphMenuStateUpdater {

  private GraphMenuStateUpdater() {}

  static void update(
      int connectionMode,
      int secondaryConnectionMode,
      JMenuItem pressure,
      JMenuItem airTemperature,
      JMenuItem seaSurfaceTemperature,
      JMenuItem windSpeed,
      JMenuItem windDirection,
      JMenuItem total) {
    // Even when settings indicate a barometer or AWS but no hardware was found (defaultPort ==
    // null), graphs remain available from the main menu for checking old values.
    // When the window is minimized, the graph options are disabled when defaultPort == null.
    if (connectionMode == 0) {
      setAll(
          false, pressure, airTemperature, seaSurfaceTemperature, windSpeed, windDirection, total);
    } else if (connectionMode == 1
        || connectionMode == 2
        || connectionMode == 4
        || connectionMode == 5
        || connectionMode == 6) {
      // Modes 1/2/4/5/6 are PTB220, PTB330, Mintaka Duo, and Mintaka Star barometers.
      pressure.setEnabled(true);
      airTemperature.setEnabled(false);
      seaSurfaceTemperature.setEnabled(false);
      windSpeed.setEnabled(false);
      windDirection.setEnabled(false);
      total.setEnabled(false);
    } else if (connectionMode == 7 || connectionMode == 8) {
      // Modes 7/8 are Mintaka StarX combinations with a Mintaka Star barometer.
      pressure.setEnabled(true);
      airTemperature.setEnabled(true);
      seaSurfaceTemperature.setEnabled(false);
      windSpeed.setEnabled(false);
      windDirection.setEnabled(false);
      total.setEnabled(false);
    } else if (connectionMode == 3
        || connectionMode == 9
        || connectionMode == 10
        || connectionMode == 11) {
      // Modes 3/9/10/11 are EUCAWS, OMC-140, and AMOS2X AWS modes.
      setAll(
          true, pressure, airTemperature, seaSurfaceTemperature, windSpeed, windDirection, total);
    }

    if (secondaryConnectionMode == 1) {
      airTemperature.setEnabled(true);
    }
  }

  private static void setAll(boolean enabled, JMenuItem... items) {
    for (JMenuItem item : items) {
      item.setEnabled(enabled);
    }
  }
}
