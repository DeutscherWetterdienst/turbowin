package turbowin;

import static turbowin.main.*;

import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns online and offline OpenStreetMap dispatch. */
final class OsmMapWorkflow {

  private OsmMapWorkflow() {}

  static void start() {
    if (osm_class == null) {
      osm_class = new OSM();
    }

    if (OSM_mode.equals(OSM_OFFLINE_MANUAL)
        || OSM_mode.equals(OSM_OFFLINE_AWS_SENSOR)
        || OSM_mode.equals(OSM_OFFLINE_AWS_VISUAL)) {
      new SwingWorker<Boolean, Void>() {
        @Override
        protected Boolean doInBackground() throws Exception {
          // Check for OSM files and copy missing resources from the application JAR before display.
          return osm_class.OSM_control_center();
        }

        @Override
        protected void done() {
          try {
            boolean doorgaan = get();

            if (doorgaan) {
              if (OSM_mode.equals(OSM_OFFLINE_MANUAL) || OSM_mode.equals(OSM_OFFLINE_AWS_VISUAL)) {
                osm_class.OSM_IMMT_on_leaflet_map();
              } else if (OSM_mode.equals(OSM_OFFLINE_AWS_SENSOR)) {
                osm_class.OSM_AWS_Sensor_data_on_leaflet_map();
              }
            } else {
              String info = "Error when displaying Obs's offline map";
              main.log_turbowin_system_message("[OSM] " + info);
              JOptionPane.showMessageDialog(
                  null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
            }
          } catch (InterruptedException | ExecutionException ex) {
            System.out.println(
                "+++ Error in Function: Maps_Obs_Map_Offline_actionPerformed. " + ex);
          }
        }
      }.execute();
    } else if (OSM_mode.equals(OSM_ONLINE_MANUAL)
        || OSM_mode.equals(OSM_ONLINE_AWS_SENSOR)
        || OSM_mode.equals(OSM_ONLINE_AWS_VISUAL)) {
      if (OSM_mode.equals(OSM_ONLINE_MANUAL) || OSM_mode.equals(OSM_ONLINE_AWS_VISUAL)) {
        osm_class.OSM_IMMT_on_leaflet_map();
      } else if (OSM_mode.equals(OSM_ONLINE_AWS_SENSOR)) {
        osm_class.OSM_AWS_Sensor_data_on_leaflet_map();
      }
    } else {
      String info = "Error when displaying Obs's map, unknown OSM display mode";
      main.log_turbowin_system_message("[OSM] " + info);
      JOptionPane.showMessageDialog(null, info, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    }
  }
}
