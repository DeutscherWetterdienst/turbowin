package turbowin;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class OSM {
  // NB
  // https://stackoverflow.com/questions/1441984/how-can-i-know-whether-an-instance-of-a-class-already-exists-in-memory
  // NB https://stackoverflow.com/questions/43608919/html-offline-map-with-local-tiles-via-leaflet

  /**
   * This class attribute will be the only "instance" of this class It is private so none can reach
   * it directly. And is "static" so it does not need "instances"
   */
  // private static OSM instance;

  /**
   * Constructor make private, to enforce the non-instantiation of the class. So an invocation to:
   * new OSM() outside of this class won't be allowed.
   */
  // private OSM(){} // avoid instantiation.

  /**
   * This class method returns the "only" instance available for this class If the instance is still
   * null, it gets instantiated. Being a class method you can call it from anywhere and it will
   * always return the same instance.
   */
  // public static OSM getInstance()
  // {
  //   if (instance == null)
  //   {
  //      instance = new OSM();
  //   }
  //   return instance;
  // }

  public OSM() {}

  public boolean OSM_control_center() {
    if (OsmMapPath.assets() == null) {
      main.log_turbowin_system_message("[OSM] Offline map resources are missing");
      return false;
    }
    if (main.logs_dir == null || main.logs_dir.trim().length() < 2) {
      main.log_turbowin_system_message("[OSM] logs folder unknown");
      return false;
    }
    ensureOsmDirectory();
    return new File(main.logs_dir, OSM_ROOT_DIR).isDirectory();
  }

  public void OSM_IMMT_on_leaflet_map() {
    // called from: Maps_OSM() [main.java]

    Boolean doorgaan = true;

    // Check logs dir
    if (main.logs_dir.trim().equals("") == true || main.logs_dir.trim().length() < 2) {
      String info = "Log folder is not configured; check the TurboWin configuration";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    // chck immt log
    if (doorgaan) {
      String volledig_path_immt = main.logs_dir + java.io.File.separator + main.IMMT_LOG;

      File immt_file = new File(volledig_path_immt);
      if (immt_file.exists() && immt_file.length() > 0) // length() in bytes
      {
        doorgaan = true;
      } else {
        String info = "No stored observations (immt.log) found on this computer";
        JOptionPane.showMessageDialog(
            null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
        doorgaan = false;
      } // else
    }

    // logs dir ok and immt log ok
    if (doorgaan) {
      // temporary message box (Processing can take a while)
      String info = "Processing and displaying can take up to 1 minute";
      final JOptionPane pane_message =
          new JOptionPane(
              info,
              JOptionPane.INFORMATION_MESSAGE,
              JOptionPane.DEFAULT_OPTION,
              null,
              new Object[] {},
              null);
      final JDialog pane_message_dialog = pane_message.createDialog(main.APPLICATION_NAME);

      Timer timer_message =
          new Timer(
              3000,
              new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                  pane_message_dialog.dispose();
                }
              });
      timer_message.setRepeats(false);
      timer_message.start();
      pane_message_dialog.setVisible(true);

      // temporary (modaless) dialog box "Processing, Please wait"
      final processing processing_dialog = new processing();
      processing_dialog.setSize(400, 300);
      processing_dialog.setVisible(true);

      OsmImmtMapWorkflow.start(this, processing_dialog);
    } //  if (doorgaan)
  }

  public void OSM_AWS_Sensor_data_on_leaflet_map() {
    OsmAwsSensorMapWorkflow.start(this);
  }

  void OSM_display_IMMT_on_online_map(List<String> immt_list) {
    // called from: OSM_IMMT_on_leaflet_map() [OSM.java]
    //
    // NB this function is still in background thread

    String os = OSDetector.getOSString();

    Integer code = 0;
    Desktop desktop = null;

    if (os.equals("LINUX")) {
      // Try platform commands before Desktop.browse: on Fedora, the Desktop call was observed
      // to delay opening the browser until TurboWin exited, without reporting an error.

      if (((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0))) {
        ensureOsmDirectory();

        String full_path_leaflet_maps_html_file =
            OsmMapPath.file(main.logs_dir, OSM_ROOT_DIR, OBS_ONLINE_MAP_HTML_FILE);
        OSM_IMMT_Obsen_on_Map(immt_list, full_path_leaflet_maps_html_file);

        try {
          // create cmd array
          String[] cmdArray = {"kde-open", full_path_leaflet_maps_html_file};

          // create a process and execute cmdArray
          Process process = Runtime.getRuntime().exec(cmdArray);
        } catch (IOException e) {
          try {
            // create cmd array
            String[] cmdArray = {"xdg-open", full_path_leaflet_maps_html_file};

            // create a process and execute cmdArray
            Process process = Runtime.getRuntime().exec(cmdArray);
          } catch (IOException e2) {
            try {
              // create cmd array
              String[] cmdArray = {"open", full_path_leaflet_maps_html_file};

              // create a process and execute cmdArray
              Process process = Runtime.getRuntime().exec(cmdArray);
            } catch (IOException e3) {
              code = -1;
            } // catch (IOException e3)
          } // catch (IOException e2)
        } // catch  (IOException e)
        if (code == -1) {
          if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();

            // open the just created leaflet maps html with the default web browser
            try {
              File leaflet_maps_file =
                  new File(full_path_leaflet_maps_html_file); // String omzetten naar File

              // if (leaflet_maps_file.exists()) // is niet nodig door het try/catch blok
              desktop.open(leaflet_maps_file);
            } catch (NullPointerException | IllegalArgumentException | IOException ex1) {
              // if file is null
              System.out.println(
                  "+++ unable to create dynamic html file for leaflet Maps plot [function: OK_button_actionPerformed()] ("
                      + ex1
                      + ")");
            }
          } // if (Desktop.isDesktopSupported())
          else {
            code = -1;
          } // else
        } // if (code == -1)

        if (code == -1 || code == -2) {
          System.out.println(
              "+++ unable to create dynamic html file for leaflet Maps plot [function: OK_button_actionPerformed()] (OS = Linux)");
        }
      } // if (((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0)))

    } // if (os.equals("LINUX"))
    else //  Windows etc.
    {
      // Before more Desktop API is used, first check
      // whether the API is supported by this particular
      // virtual machine (VM) on this particular host.

      if ((Desktop.isDesktopSupported())
          && ((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0))) {
        desktop = Desktop.getDesktop();

        ensureOsmDirectory();

        String full_path_leaflet_maps_html_file =
            OsmMapPath.file(main.logs_dir, OSM_ROOT_DIR, OBS_ONLINE_MAP_HTML_FILE);
        OSM_IMMT_Obsen_on_Map(immt_list, full_path_leaflet_maps_html_file);

        // open the just created leaflet maps html with the default web browser
        try {
          File leaflet_maps_file =
              new File(full_path_leaflet_maps_html_file); // String omzetten naar File

          // if (leaflet_maps_file.exists()) // is niet nodig door het try/catch blok
          desktop.open(leaflet_maps_file);
        } catch (NullPointerException | IllegalArgumentException | IOException ex1) {
          // if file is null
          System.out.println(
              "+++ unable to create dynamic (online) html file for leaflet Maps plot [function: OSM_display_IMMT_on_online_map()] ("
                  + ex1
                  + ")");
          main.log_turbowin_system_message("[OSM] error creating online Obs's Map html file");
        }
      } // if ((Desktop.isDesktopSupported()) && etc.
    } // else (Windows etc.)
  }

  private void ensureOsmDirectory() {
    // It is possible that the OSM dir does not exist; if not, create it first.
    String osmDirectoryPath = main.logs_dir + java.io.File.separator + OSM_ROOT_DIR;
    File osmDirectory = new File(osmDirectoryPath);
    if (osmDirectory.exists() == false) {
      try {
        osmDirectory.mkdir();
        main.log_turbowin_system_message(
            "[OSM] created OSM dir for obs online map in " + main.logs_dir);
      } catch (Exception ex) {
        // If a security manager exists, its checkWrite method may deny directory creation.
        main.log_turbowin_system_message(
            "[OSM] could NOT create OSM dir for obs online map in "
                + main.logs_dir
                + "; reason: "
                + ex);
      }
    }
  }

  private void OSM_IMMT_Obsen_on_Map(
      List<String> immt_list, String full_path_leaflet_maps_html_file) {
    // called from: OSM_display_IMMT_on_online_map() [OSM.java]
    //              OSM_display_IMMT_on_offline_map() [OSM.java]
    // NB this function is still in background thread
    //
    // NB see also: Input_Position_menu_actionPerformed() [main.java]
    // NB see also: set_latest_obs_values() [DASHBOARD_latest_obs.java]

    // make a dynamic html file
    // NB Before invoking this function the existence of the output dir (the logs dir) is already
    // checked positive
    //
    // voor AIS info see: http://www.marinetraffic.com/ais/nl/addyourarea.aspx?level1=150 (English
    // version: http://www.marinetraffic.com/ais/addyourarea.aspx?level1=150)

    //
    // write HTML/JS code
    //
    try (BufferedWriter out =
        new BufferedWriter(new FileWriter(full_path_leaflet_maps_html_file))) {
      if (main.OSM_mode.equals(main.OSM_ONLINE_MANUAL)
          || main.OSM_mode.equals(main.OSM_ONLINE_AWS_VISUAL)) {
        OsmMapHtmlWriter.writeOnlineShell(out, "TurboWin Obs's Map (internet)");
      } // if (main.OSM_mode.equals(main.OSM_ONLINE_MANUAL) ||
      // main.OSM_mode.equals(main.OSM_ONLINE_AWS_VISUAL))
      else if (main.OSM_mode.equals(main.OSM_OFFLINE_MANUAL)
          || main.OSM_mode.equals(main.OSM_OFFLINE_AWS_VISUAL)) // offline
      {
        OsmMapHtmlWriter.writeOfflineShell(
            out,
            "TurboWin Obs's Map (offline)",
            "var iconOptions = { iconUrl: 'marker_red_meet.png', iconAnchor: [10, 25], iconPopup: [0, -20] };");
      } // else if (main.OSM_mode.equals(main.OSM_OFFLINE_MANUAL) ||
      // main.OSM_mode.equals(main.OSM_OFFLINE_AWS_VISUAL))

      for (String obs : immt_list) {
        System.out.println(obs);

        String month_full = "";
        String latitude_hemisphere = "";
        String longitude_hemisphere = "";
        String true_wind_dir_hulp = "";
        String lat_prefix = "";
        String lon_prefix = "";
        String year = "";
        String month = "";
        String day = "";
        String hour = "";
        String quadrant = ""; // WMO code table 3333
        String latitude = ""; // tenths of degrees
        String longitude = ""; // tenths of degrees
        String pressure_MSL = "";
        String air_temp_sign = ""; // WMO code table 3845
        String air_temp = "";
        String sst_sign = ""; // WMO code table 3845
        String sst = "";
        String wind_units_ind = ""; // WMO code table 1855
        String wind_units_local = "";
        String true_wind_dir = ""; // WMO code table 0877
        String true_wind_speed = "";
        String local_call_sign = ""; // local_call_sign to prevent field hiding (global call-sign)

        if (obs.length() > 80) {
          String infowindow_total = "";

          // see IMMT description
          // eg IMMT record: 32018051112152700620         0129                     0
          // 44TESTNL US 314            23           A599999199999999991191                    9999
          // 999     01234567
          year = obs.substring(1, 5);
          month = obs.substring(5, 7);
          day = obs.substring(7, 9);
          hour = obs.substring(9, 11);

          quadrant = obs.substring(11, 12); // WMO code table 3333
          latitude = obs.substring(12, 15); // tenths of degrees
          longitude = obs.substring(15, 19); // tenths of degrees

          pressure_MSL = obs.substring(37, 41);
          air_temp_sign = obs.substring(29, 30); // WMO code table 3845
          air_temp = obs.substring(30, 33);
          sst_sign = obs.substring(49, 50); // WMO code table 3845
          sst = obs.substring(50, 53);
          wind_units_ind = obs.substring(26, 27); // WMO code table 1855
          true_wind_dir = obs.substring(24, 26); // WMO code table 0877
          true_wind_speed = obs.substring(27, 29);
          local_call_sign = obs.substring(71, 78);
          // wind_waves_period = obs.substring(55, 57);
          // wind_waves_height = obs.substring(57, 59);
          // swell_1_dir       = obs.substring(59, 61);
          // swell_1_period    = obs.substring(61, 63);
          // swell_1_height    = obs.substring(63, 65);

          ///////// position quadrant of the globe
          //
          switch (quadrant) {
            case "1":
              lat_prefix = "";
              latitude_hemisphere = "N";
              lon_prefix = "";
              longitude_hemisphere = "E";
              break;
            case "3":
              lat_prefix = "-";
              latitude_hemisphere = "S";
              lon_prefix = "";
              longitude_hemisphere = "E";
              break;
            case "5":
              lat_prefix = "-";
              latitude_hemisphere = "S";
              lon_prefix = "-";
              longitude_hemisphere = "W";
              break;
            case "7":
              lat_prefix = "";
              latitude_hemisphere = "N";
              lon_prefix = "-";
              longitude_hemisphere = "W";
              break;
            default:
              lat_prefix = "";
              latitude_hemisphere = "?";
              lon_prefix = "";
              longitude_hemisphere = "?";
              break;
          } // switch (quadrant)

          ///////// latitude and longitude in degrees but here still without the hemisphere!
          //
          String lat_degrees =
              latitude.substring(0, 2) + "." + latitude.substring(2, latitude.length());
          String lon_degrees =
              longitude.substring(0, 3) + "." + longitude.substring(3, longitude.length());

          // position marker; NB add hemisphere (+ or -) and leading zeros is not allowed in a
          // position in a leaflet map
          //

          // test the validity of the marker position (other wise with one single marker position
          // error nothing will be dispayed
          Boolean marker_position_ok = false;
          if ((lat_degrees.length() == 4 && lon_degrees.length() == 5)) // e.g. "52.4" and "023.7"
          {
            char lat_one = lat_degrees.charAt(0);
            char lat_two = lat_degrees.charAt(1);
            char lat_four = lat_degrees.charAt(3);

            char lon_one = lon_degrees.charAt(0);
            char lon_two = lon_degrees.charAt(1);
            char lon_three = lon_degrees.charAt(2);
            char lon_five = lon_degrees.charAt(4);

            if (Character.isDigit(lat_one)
                && Character.isDigit(lat_two)
                && Character.isDigit(lat_four)
                && Character.isDigit(lon_one)
                && Character.isDigit(lon_two)
                && Character.isDigit(lon_three)
                && Character.isDigit(lon_five)) {
              marker_position_ok = true;
            }
          } // if ((lat_degrees.length() == 4 && lon_degrees.length() == 5))

          String lat_marker = lat_prefix + lat_degrees.replaceFirst("^0+(?!$)", "");
          String lon_marker = lon_prefix + lon_degrees.replaceFirst("^0+(?!$)", "");

          // position for the pop-up (different from the marker position, see hemisphere)
          //
          String infowindow_lat = lat_degrees + "&#176 " + latitude_hemisphere;
          String infowindow_lon = lon_degrees + "&#176 " + longitude_hemisphere;

          // date time of the IMMT obs
          //
          month_full = DateTimeUtils.shortMonthOrOriginal(month);

          String infowindow_date_time =
              day + " " + month_full + " " + year + "  " + hour + ".00 UTC";

          if (main.OSM_mode.contains(main.OSM_ONLINE_MANUAL)
              || main.OSM_mode.contains(main.OSM_OFFLINE_MANUAL)) {
            // (true) wind dir and speed
            //
            // System.out.println("immt wind dir = " + true_wind_dir);
            // System.out.println("immt wind speed = " + true_wind_speed);

            String infowindow_wind = "wind: ";
            if ((true_wind_dir.trim().length() > 0) && (true_wind_speed.trim().length() > 0)) {
              switch (wind_units_ind) // (WMO code table 1855)
              {
                case "0":
                  wind_units_local = "m/s";
                  break;
                case "1":
                  wind_units_local = "m/s";
                  break;
                case "3":
                  wind_units_local = "kts";
                  break;
                case "4":
                  wind_units_local = "kts";
                  break;
                default:
                  wind_units_local = "";
                  break;
              } // switch

              if (true_wind_dir.equals("99")) {
                true_wind_dir_hulp = "var";
              } else {
                true_wind_dir_hulp = true_wind_dir + "0"; // eg 34 immt-code -> 340
              }

              infowindow_wind =
                  "wind: "
                      + true_wind_dir_hulp
                      + "&#176"
                      + " / "
                      + true_wind_speed
                      + " "
                      + wind_units_local;
            } // if (!true_wind_dir.equals("") && !true_wind_speed.equals(""))

            // air temp
            //
            // System.out.println("immt air temp = " + air_temp);

            String infowindow_air_temp = "air temp: ";
            // System.out.println("air tmp length =  " + air_temp.length()  );
            if (air_temp.trim().length() > 0) {
              air_temp = air_temp.substring(0, 2) + "." + air_temp.substring(2, air_temp.length());
              air_temp =
                  air_temp.replaceFirst(
                      "^0+(?!$)",
                      ""); // remove leading zeros but leaves one if necessary (i.e. it wouldn't
              // just turn "0" to a blank string)

              if (air_temp_sign.equals("1")) {
                infowindow_air_temp = "air temp: -" + air_temp + " &#176 C";
              } else {
                infowindow_air_temp = "air temp: " + air_temp + " &#176 C";
              }
            }

            // sst
            //
            String infowindow_sst = "sst: ";
            if (sst.trim().length() > 0) {
              sst = sst.substring(0, 2) + "." + sst.substring(2, sst.length());
              sst =
                  air_temp.replaceFirst(
                      "^0+(?!$)",
                      ""); // remove leading zeros but leaves one if necessary (i.e. it wouldn't
              // just turn "0" to a blank string)

              if (sst_sign.equals("1")) {
                infowindow_sst = "sst: -" + sst + " &#176 C";
              } else {
                infowindow_sst = "sst: " + sst + " &#176 C";
              }
            }

            // MSL air pressure
            //
            String infowindow_msl_pressure = "slp: ";
            if (pressure_MSL.trim().length() > 0) // there is something present
            {
              try {
                int int_pressure_MSL = Integer.parseInt(pressure_MSL);
                if (int_pressure_MSL > 8000) {
                  infowindow_msl_pressure =
                      "slp: "
                          + pressure_MSL.substring(0, 3)
                          + "."
                          + pressure_MSL.substring(3, pressure_MSL.length())
                          + " hPa";
                } else {
                  infowindow_msl_pressure =
                      "slp: "
                          + "1"
                          + pressure_MSL.substring(0, 3)
                          + "."
                          + pressure_MSL.substring(3, pressure_MSL.length())
                          + " hPa";
                }
              } catch (NumberFormatException e) {
                infowindow_msl_pressure = "slp: ";
              }
            } // if (!pressure_MSL.equals(""))

            // pop-up text
            //
            infowindow_total =
                "\""
                    + "<h3>"
                    + local_call_sign.trim()
                    + " weather observation"
                    + "</h3>"
                    + "<p>"
                    + " position: "
                    + infowindow_lat
                    + ", "
                    + infowindow_lon
                    + "<br>"
                    + " date time: "
                    + infowindow_date_time
                    + "<br>"
                    + infowindow_wind
                    + "<br>"
                    // + infowindow_wind_speed + "<br>"
                    + infowindow_air_temp
                    + "<br>"
                    + infowindow_sst
                    + "<br>"
                    + infowindow_msl_pressure
                    + "<br>"
                    + "</p>"
                    + "\"";
          } // if (main.OSM_mode.contains(main.OSM_ONLINE_MANUAL) ||
          // main.OSM_mode.contains(main.OSM_OFFLINE_MANUAL))
          else if (main.OSM_mode.contains(main.OSM_ONLINE_AWS_VISUAL)
              || main.OSM_mode.contains(main.OSM_OFFLINE_AWS_VISUAL)) {
            // pop-up text
            //
            infowindow_total =
                "\""
                    + "<h3>"
                    + local_call_sign.trim()
                    + " weather observation"
                    + "</h3>"
                    + "<p>"
                    + " position: "
                    + infowindow_lat
                    + ", "
                    + infowindow_lon
                    + "<br>"
                    + " date time: "
                    + infowindow_date_time
                    + "<br>"
                    + "<br>"
                    + "visual data uploaded"
                    + "<br>"
                    + "</p>"
                    + "\"";
          } // else if etc.

          if (marker_position_ok) {
            OsmMapHtmlWriter.writeMarker(out, lat_marker, lon_marker, infowindow_total);
          }
        } // if (obs.length() > 70)
      } //  for (String obs : immt_list)
      OsmMapHtmlWriter.writeDocumentEnd(out);

    } // try
    catch (HeadlessException | IOException e) {
      String info = "[OSM] error creating Obs's Map html file (" + e + ")";
      main.log_turbowin_system_message(info);
    } // catch
  }

  void OSM_display_IMMT_on_offline_map(List<String> immt_list) {
    // called from: OSM_IMMT_on_leaflet_map() [OSM.java]
    //
    // NB this function is still in background thread

    openOfflineMap(
        OBS_OFFLINE_MAP_HTML_FILE,
        () ->
            OSM_IMMT_Obsen_on_Map(
                immt_list, OsmMapPath.file(main.logs_dir, OSM_ROOT_DIR, OBS_OFFLINE_MAP_HTML_FILE)),
        "OSM_display_IMMT_on_offline_map()");
  } // private void OSM_display_IMMT_on_offline_map(List<String> immt_list)

  void OSM_display_AWS_Sensor_on_online_map() {
    // called from: OSM_AWS_Sensor_data_on_leaflet_map() [OSM.java]
    //
    // NB this function is still in background thread

    Desktop desktop = null;

    // Before more Desktop API is used, first check
    // whether the API is supported by this particular
    // virtual machine (VM) on this particular host.

    if ((Desktop.isDesktopSupported())
        && ((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0))) {
      desktop = Desktop.getDesktop();

      try {
        // check internet connection available
        URL url = new URI("http://www.google.com/generate_204").toURL();
        URLConnection con = url.openConnection();
        con.connect();

        String full_path_leaflet_maps_html_file =
            OsmMapPath.file(main.logs_dir, OSM_ROOT_DIR, OBS_ONLINE_MAP_HTML_FILE);
        OSM_AWS_Sensor_Obsen_on_Map(full_path_leaflet_maps_html_file);

        // open the just created leaflet maps html with the default web browser
        try {
          File leaflet_maps_file =
              new File(full_path_leaflet_maps_html_file); // String omzetten naar File

          // if (leaflet_maps_file.exists()) // is niet nodig door het try/catch blok
          desktop.open(leaflet_maps_file);
        } catch (NullPointerException | IllegalArgumentException | IOException ex1) {
          // if file is null
          System.out.println(
              "+++ unable to create dynamic (online) html file for leaflet Maps plot [function: OSM_display_AWS_Sensor_on_online_map()] ("
                  + ex1
                  + ")");
          main.log_turbowin_system_message(
              "[OSM] error creating online AWS sensor Obs's Map html file");
        }

      } // try
      catch (MalformedURLException ex) {
        System.out.println(
            "+++ unable to create dynamic (online) html file for leaflet Maps plot [function: OSM_display_AWS_Sensor_on_online_map()] ("
                + ex
                + ")");
        main.log_turbowin_system_message(
            "[OSM] error creating online AWS sensor Obs's Map html file");
      } catch (URISyntaxException ex) {
        System.out.println(
            "+++ unable to create dynamic (online) html file for leaflet Maps plot [function: OSM_display_AWS_Sensor_on_online_map()] ("
                + ex
                + ")");
        main.log_turbowin_system_message(
            "[OSM] error creating online AWS sensor Obs's Map html file");
      } catch (IOException ioe) {
        System.out.println(
            "+++ unable to create dynamic (online) html file for leaflet Maps plot [function: OSM_display_AWS_Sensor_on_online_map()] ("
                + ioe
                + ")");
        main.log_turbowin_system_message(
            "[OSM] error creating online AWS sensor Obs's Map html file");
      }
    } // if ((Desktop.isDesktopSupported()) && etc.
  }

  void OSM_display_AWS_Sensor_on_offline_map() {
    // called from: called from: OSM_AWS_Sensor_data_on_leaflet_map() [OSM.java]
    //
    // NB this function is still in background thread

    openOfflineMap(
        OBS_OFFLINE_MAP_HTML_FILE,
        () ->
            OSM_AWS_Sensor_Obsen_on_Map(
                OsmMapPath.file(main.logs_dir, OSM_ROOT_DIR, OBS_OFFLINE_MAP_HTML_FILE)),
        "OSM_display_AWS_Sensor_on_offline_map()");
  }

  private void openOfflineMap(String mapFileName, Runnable mapWriter, String functionName) {
    if ((Desktop.isDesktopSupported())
        && ((main.logs_dir != null) && (main.logs_dir.compareTo("") != 0))) {
      Desktop desktop = Desktop.getDesktop();
      String mapPath = OsmMapPath.file(main.logs_dir, OSM_ROOT_DIR, mapFileName);
      mapWriter.run();

      try {
        desktop.open(new File(mapPath));
      } catch (NullPointerException | IllegalArgumentException | IOException ex) {
        System.out.println(
            "+++ unable to create dynamic (offline) html file for leaflet Maps plot [function: "
                + functionName
                + "] ("
                + ex
                + ")");
        main.log_turbowin_system_message(
            "[OSM] error creating offline AWS sensor Obs's Map html file");
      }
    }
  }

  private void OSM_AWS_Sensor_Obsen_on_Map(String full_path_leaflet_maps_html_file) {
    String[][] measurements = LatestMeasurementsData.snapshot();
    // called from: - OSM_display_AWS_Sensor_on_offline_map()
    //              - OSM_display_AWS_Sensor_on_online_map()

    // NB this function is still in background thread
    //
    // NB see also: Input_Position_menu_actionPerformed() [main.java]
    // NB see also: set_latest_obs_values() [DASHBOARD_latest_obs.java]

    // make a dynamic html file
    // NB Before invoking this function the existence of the output dir (the logs dir) is already
    // checked positive
    //
    // voor AIS info see: http://www.marinetraffic.com/ais/nl/addyourarea.aspx?level1=150 (English
    // version: http://www.marinetraffic.com/ais/addyourarea.aspx?level1=150)

    //
    // write HTML/JS code
    //
    try (BufferedWriter out =
        new BufferedWriter(new FileWriter(full_path_leaflet_maps_html_file))) {
      if (main.OSM_mode.equals(main.OSM_ONLINE_AWS_SENSOR)) {
        OsmMapHtmlWriter.writeOnlineShell(out, "TurboWin AWS sensor Map (internet)");
      } // if (main.OSM_mode.equals(main.OSM_ONLINE_AWS_SENSOR))
      else if (main.OSM_mode.equals(main.OSM_OFFLINE_AWS_SENSOR)) {
        OsmMapHtmlWriter.writeOfflineShell(
            out,
            "TurboWin AWS sensor Map (offline)",
            "var iconOptions = { iconUrl: 'marker_red_meet.png', iconAnchor: [10, 2] };");
      } // else if (main.OSM_mode.equals(main.OSM_OFFLINE_AWS_SENSOR))

      for (int i = 0; i < LatestMeasurementsData.AANTAL_AWS_MEASUREMENTS; i++) {
        // System.out.println(obs);

        if ((!measurements[i][LatestMeasurementsData.date_index].equals(""))
            && (!measurements[i][LatestMeasurementsData.time_index].equals(""))) {
          String infowindow_total = "";
          String infowindow_date_time = "";
          String infowindow_lat = "";
          String infowindow_lon = "";
          String infowindow_wind = "wind: ";
          String infowindow_wind_speed = "";
          String infowindow_wind_dir = "";
          String infowindow_air_temp = "air temp: ";
          String infowindow_sst = "sst: ";
          String infowindow_msl_pressure = "slp: ";

          String lon_marker = "";
          String lat_marker = "";
          String lat_degrees = "";
          String lat_degrees_info = ""; // for pop-up info box (without the"-" sign)
          String lon_degrees = "";
          String lon_degrees_info = ""; // for popup info box (without the"-" sign)
          String month_full = "";
          String latitude_hemisphere = "";
          String longitude_hemisphere = "";
          // String true_wind_dir_hulp   = "";
          // String lat_prefix           = "";
          // String lon_prefix           = "";
          String year = "";
          String month = "";
          String day = "";
          String time = "";
          // String latitude             = "";            // tenths of degrees
          // String longitude            = "";            // tenths of degrees
          String pressure_MSL = "";
          // String air_temp_sign        = "";            // WMO code table 3845
          String air_temp = "";
          // String sst_sign             = "";            // WMO code table 3845
          String sst = "";
          // String wind_units_ind       = "";            // WMO code table 1855
          String true_wind_dir = ""; // WMO code table 0877
          String true_wind_speed = "";
          // String local_call_sign      = "";            // local_call_sign to prevent field hiding
          // (global call-sign)

          boolean marker_position_ok = true;

          for (int c = 0; c < LatestMeasurementsData.AANTAL_AWS_PARAMETERS; c++) // columns
          {
            if (c == LatestMeasurementsData.date_index) {
              if (measurements[i][c].length() == 8) {
                // format e.g. 20190221
                year = measurements[i][c].substring(0, 4);
                month = measurements[i][c].substring(4, 6);
                day = measurements[i][c].substring(6, 8);

                month_full = DateTimeUtils.shortMonthOrOriginal(month);
              } // if (measurements[i][c].length() == 8)
            } // if (c == LatestMeasurementsData.date_index
            else if (c == LatestMeasurementsData.time_index) {
              if (measurements[i][c].length() == 6) {
                // format e.g. 0920
                time =
                    measurements[i][c].substring(0, 2) + "." + measurements[i][c].substring(2, 4);
              }

              infowindow_date_time = day + " " + month_full + " " + year + "  " + time + " UTC";
            } else if (c == LatestMeasurementsData.lat_index) {
              double double_lat_degrees = 0;

              // eg 52.405
              lat_degrees = measurements[i][c];

              try {
                double_lat_degrees = Double.parseDouble(lat_degrees);

                if (double_lat_degrees < 0) {
                  latitude_hemisphere = "S";
                  lat_degrees_info = lat_degrees.substring(1); // 'delete' the "-" sign
                } else {
                  latitude_hemisphere = "N";
                  lat_degrees_info = lat_degrees;
                }

                infowindow_lat = lat_degrees_info + "&#176 " + latitude_hemisphere;

                // position marker; NB leading zeros is not allowed in a position in a leaflet map
                //
                lat_marker = lat_degrees.replaceFirst("^0+(?!$)", "");

              } // try
              catch (NumberFormatException e) {
                marker_position_ok = false;
              }
            } // else if (c == LatestMeasurementsData.lat_index)
            else if (c == LatestMeasurementsData.lon_index) {
              double double_lon_degrees = 0;

              // eg -4.405
              lon_degrees = measurements[i][c];

              try {
                double_lon_degrees = Double.parseDouble(lon_degrees);

                if (double_lon_degrees < 0) {
                  longitude_hemisphere = "W";
                  lon_degrees_info = lon_degrees.substring(1); // delete the "-" sign
                } else {
                  longitude_hemisphere = "E";
                  lon_degrees_info = lon_degrees;
                }

                infowindow_lon = lon_degrees_info + "&#176 " + longitude_hemisphere;

                // position marker; NB leading zeros is not allowed in a position in a leaflet map
                //
                lon_marker = lon_degrees.replaceFirst("^0+(?!$)", "");

              } catch (NumberFormatException e) {
                marker_position_ok = false;
              }

            } // else if (c == LatestMeasurementsData.lat_index)
            else if (c == LatestMeasurementsData.true_wind_speed_index) {
              // NB true wind speed wil be read before true wind dir!!

              true_wind_speed = measurements[i][c];
              if (true_wind_speed.length() >= 1) {
                String hulp_true_wind_speed = "";
                if (main.wind_units_dashboard.indexOf(main.KNOTS) != -1) // units is knots
                {
                  double wind_speed_knots_double =
                      Double.parseDouble(true_wind_speed) * main.M_S_KNOT_CONVERSION;
                  hulp_true_wind_speed = String.format("%.2f", wind_speed_knots_double);
                } else {
                  hulp_true_wind_speed = true_wind_speed;
                }

                infowindow_wind_speed = hulp_true_wind_speed + " " + main.wind_units_dashboard;
              } else {
                infowindow_wind_speed = "-";
              }
            } // else if (c == LatestMeasurementsData.true_wind_dir_index)
            else if (c == LatestMeasurementsData.true_wind_dir_index) {
              // NB true wind dir will be read after true wind speed !!

              true_wind_dir = measurements[i][c];
              if (true_wind_dir.length() >= 1) {
                infowindow_wind_dir = true_wind_dir + "&#176" + " / ";
              } else {
                infowindow_wind_dir = "-" + " / ";
              }

              infowindow_wind += infowindow_wind_dir + infowindow_wind_speed;

            } // else if (c == LatestMeasurementsData.true_wind_dir_index)
            else if (c == LatestMeasurementsData.air_temp_index) {
              air_temp = measurements[i][c];

              if (air_temp.length() >= 1) {
                infowindow_air_temp += air_temp + " &#176 C";
              } else {
                infowindow_air_temp += "-";
              }
            } // else if (c == LatestMeasurementsData.air_temp_index)
            else if (c == LatestMeasurementsData.sst_index) {
              sst = measurements[i][c];

              if (sst.length() >= 1) {
                infowindow_sst += sst + " &#176 C";
              } else {
                infowindow_sst += "-";
              }
            } // else if (c == LatestMeasurementsData.sst_index)
            else if (c == LatestMeasurementsData.slp_index) {
              pressure_MSL = measurements[i][c];

              if (pressure_MSL.length() >= 1) {
                infowindow_msl_pressure += pressure_MSL + " hPa";
              } else {
                infowindow_msl_pressure += "-";
              }
            } // else if (c == LatestMeasurementsData.sst_index)
          } // for (int c = 0; c < LatestMeasurementsData.AANTAL_AWS_PARAMETERS; c++)

          // pop-up text
          //
          // infowindow_total = "\"" + "<h3>" + main.call_sign.trim() + " AWS sensor" + "</h3>"
          infowindow_total =
              "\""
                  + "<h3>"
                  + main.station_ID.trim()
                  + " AWS sensor"
                  + "</h3>"
                  + "<p>"
                  + " position: "
                  + infowindow_lat
                  + ", "
                  + infowindow_lon
                  + "<br>"
                  + " date time: "
                  + infowindow_date_time
                  + "<br>"
                  + infowindow_wind
                  + "<br>"
                  // + infowindow_wind_speed + "<br>"
                  + infowindow_air_temp
                  + "<br>"
                  + infowindow_sst
                  + "<br>"
                  + infowindow_msl_pressure
                  + "<br>"
                  + "</p>"
                  + "\"";
          // } // if (main.OSM_mode.contains(main.OSM_ONLINE_MANUAL) ||
          // main.OSM_mode.contains(main.OSM_OFFLINE_MANUAL))

          if (marker_position_ok) {
            OsmMapHtmlWriter.writeMarker(out, lat_marker, lon_marker, infowindow_total);
          }
        } // if ( (!measurements[i][LatestMeasurementsData.date_index].equals(""))
        // && etc.
      } // for (int i = 0; i < AANTAL_AWS_MEASUREMENTS; i++)
      OsmMapHtmlWriter.writeDocumentEnd(out);

    } // try
    catch (HeadlessException | IOException e) {
      String info = "[OSM] error creating Obs's Map html file (" + e + ")";
      main.log_turbowin_system_message(info);
    } // catch
  }

  public static final String OBS_OFFLINE_MAP_HTML_FILE =
      "obs_offline_map.html"; // leaflet maps file
  public static final String OBS_ONLINE_MAP_HTML_FILE = "obs_online_map.html"; // leaflet maps file

  private final String OSM_ROOT_DIR = "OSM";
} // public class OSM
