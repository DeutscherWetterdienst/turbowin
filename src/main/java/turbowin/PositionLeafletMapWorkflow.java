package turbowin;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;

/** Owns asynchronous generation and opening of the position leaflet map. */
final class PositionLeafletMapWorkflow {

  private PositionLeafletMapWorkflow() {}

  static void start(Runnable createMap) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
        String os = OSDetector.getOSString();

        Desktop desktop = null;
        Integer code = 0;

        if (os.equals("LINUX")) {
          // Linux launchers are tried before Desktop.open because Desktop can open late on Fedora.
          if ((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0)) {
            String full_path_leaflet_maps_html_file =
                main.logs_dir + java.io.File.separator + main.LEAFLET_MAPS_HTML_FILE;
            createMap.run();

            try {
              String[] cmdArray = {"kde-open", full_path_leaflet_maps_html_file};
              Process process = Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e) {
              try {
                String[] cmdArray = {"xdg-open", full_path_leaflet_maps_html_file};
                Process process = Runtime.getRuntime().exec(cmdArray);
              } catch (IOException e2) {
                try {
                  String[] cmdArray = {"open", full_path_leaflet_maps_html_file};
                  Process process = Runtime.getRuntime().exec(cmdArray);
                } catch (IOException e3) {
                  code = -1;
                }
              }
            }
            if (code == -1) {
              // Use the Desktop API only as a fallback when the Linux launchers are unavailable.
              if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                try {
                  File leaflet_maps_file = new File(full_path_leaflet_maps_html_file);
                  desktop.open(leaflet_maps_file);
                } catch (NullPointerException | IllegalArgumentException | IOException ex1) {
                  System.out.println(
                      "+++ unable to create dynamic html file for leaflet Maps plot [function: OK_button_actionPerformed()] ("
                          + ex1
                          + ")");
                }
              } else {
                code = -1;
              }
            }

            if (code == -1 || code == -2) {
              System.out.println(
                  "+++ unable to create dynamic html file for leaflet Maps plot [function: OK_button_actionPerformed()] (OS = Linux)");
            }
          }
        } else {
          // Other platforms use Desktop only when the API reports browser support.
          if ((Desktop.isDesktopSupported())
              && ((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0))) {
            desktop = Desktop.getDesktop();

            String full_path_leaflet_maps_html_file =
                main.logs_dir + java.io.File.separator + main.LEAFLET_MAPS_HTML_FILE;
            createMap.run();

            try {
              File leaflet_maps_file = new File(full_path_leaflet_maps_html_file);
              desktop.open(leaflet_maps_file);
            } catch (NullPointerException | IllegalArgumentException | IOException ex1) {
              System.out.println(
                  "+++ unable to create dynamic html file for leaflet Maps plot [function: OK_button_actionPerformed()] ("
                      + ex1
                      + ")");
            }
          }
        }

        return null;
      } // protected Void doInBackground() throws Exception
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
