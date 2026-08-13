package turbowin;

import static turbowin.main.*;

/** Replaces the active graph view and resets sensor-data polling state. */
final class GraphViewWorkflow {

  private GraphViewWorkflow() {}

  static String airTemperatureMode(int secondaryConnectionMode) {
    return secondaryConnectionMode == 1 ? MODE_AIRTEMP_II : MODE_AIRTEMP;
  }

  static RS232_view show(RS232_view currentGraph, String mode) {
    if (currentGraph != null) {
      stopSensorDataTimers();
      currentGraph.setVisible(false);
    }

    mode_grafiek = mode;
    RS232_view graph = new RS232_view();
    graph.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    graph.setVisible(true);
    return graph;
  }

  private static void stopSensorDataTimers() {
    if (sensor_data_file_ophalen_timer_is_gecreeerd
        && RS232_view.sensor_data_file_ophalen_timer.isRunning()) {
      RS232_view.sensor_data_file_ophalen_timer.stop();
    }
    RS232_view.sensor_data_file_ophalen_timer = null;
    sensor_data_file_ophalen_timer_is_gecreeerd = false;

    if (sensor_data_file_ophalen_timer_is_gecreeerd_II
        && RS232_view.sensor_data_file_ophalen_timer_II.isRunning()) {
      RS232_view.sensor_data_file_ophalen_timer_II.stop();
    }
    RS232_view.sensor_data_file_ophalen_timer_II = null;
    sensor_data_file_ophalen_timer_is_gecreeerd_II = false;
  }
}
