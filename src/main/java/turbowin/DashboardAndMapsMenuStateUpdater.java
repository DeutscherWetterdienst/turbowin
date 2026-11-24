package turbowin;

import javax.swing.JMenuItem;

/** Updates dashboard and map menu items according to connection and runtime modes. */
final class DashboardAndMapsMenuStateUpdater {

  private DashboardAndMapsMenuStateUpdater() {}

  static void update(
      int connectionMode,
      boolean apr,
      boolean offlineMode,
      JMenuItem barometer,
      JMenuItem awsDigital,
      JMenuItem latestObservation,
      JMenuItem awsSensorOffline,
      JMenuItem awsHybrid,
      JMenuItem awsWindRadar,
      JMenuItem latestAwsMeasurements,
      JMenuItem observationOffline,
      JMenuItem observationOnline,
      JMenuItem awsVisualOffline,
      JMenuItem awsSensorOnline,
      JMenuItem awsVisualOnline,
      JMenuItem aprRadar) {
    setAll(
        true,
        barometer,
        awsDigital,
        latestObservation,
        awsSensorOffline,
        awsHybrid,
        awsWindRadar,
        latestAwsMeasurements,
        observationOffline,
        observationOnline,
        awsVisualOffline,
        awsSensorOnline,
        awsVisualOnline,
        aprRadar);

    boolean awsConnected =
        connectionMode == 3 || connectionMode == 9 || connectionMode == 10 || connectionMode == 11;
    boolean barometerConnected =
        connectionMode == 1
            || connectionMode == 2
            || connectionMode == 4
            || connectionMode == 5
            || connectionMode == 6
            || connectionMode == 7
            || connectionMode == 8;

    if (!awsConnected) {
      setAll(
          false,
          awsDigital,
          awsHybrid,
          awsWindRadar,
          latestAwsMeasurements,
          awsSensorOffline,
          awsVisualOffline,
          awsSensorOnline,
          awsVisualOnline);
    }
    if (!barometerConnected) {
      barometer.setEnabled(false);
    }
    if (awsConnected) {
      latestObservation.setEnabled(false);
      observationOffline.setEnabled(false);
      observationOnline.setEnabled(false);
    }
    if (!apr) {
      aprRadar.setEnabled(false);
    }
    if (!offlineMode) {
      // Online TurboWeb mode has no offline map files; those links are only for offline mode.
      // AWS sensor maps use sensor_data files; AWS visual and observation maps use the IMMT log.
      observationOffline.setEnabled(false);
      awsSensorOffline.setEnabled(false);
      awsVisualOffline.setEnabled(false);
    }

    // For DWD (Germany), the AWS wind-radar dashboard has a compatibility exception and may be
    // disabled even when an AWS is connected.
  }

  private static void setAll(boolean enabled, JMenuItem... items) {
    for (JMenuItem item : items) {
      item.setEnabled(enabled);
    }
  }
}
