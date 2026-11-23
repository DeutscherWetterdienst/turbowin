package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/** Position observation state and compatibility controls used by legacy GPS readers. */
public final class myposition {
  public static ButtonGroup buttonGroup1, buttonGroup2, buttonGroup3, buttonGroup4;
  public static JTextField jTextField1, jTextField2, jTextField3, jTextField4;
  public static JRadioButton jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4;
  public static JRadioButton jRadioButton5,
      jRadioButton6,
      jRadioButton7,
      jRadioButton8,
      jRadioButton9;
  public static JRadioButton jRadioButton10, jRadioButton11, jRadioButton12, jRadioButton13;
  public static JRadioButton jRadioButton14,
      jRadioButton15,
      jRadioButton16,
      jRadioButton17,
      jRadioButton18;
  public static JRadioButton jRadioButton19,
      jRadioButton20,
      jRadioButton21,
      jRadioButton22,
      jRadioButton23;
  private boolean plotPosition;
  private boolean mapAvailable;
  private String courseLabel;
  private String speedLabel;

  public myposition() {
    jTextField1 = new JTextField();
    jTextField2 = new JTextField();
    jTextField3 = new JTextField();
    jTextField4 = new JTextField();
    for (JTextField field : new JTextField[] {jTextField1, jTextField2, jTextField3, jTextField4})
      NumericInputWorkflow.decimal(field);
    jRadioButton1 = new JRadioButton();
    jRadioButton2 = new JRadioButton();
    jRadioButton3 = new JRadioButton();
    jRadioButton4 = new JRadioButton();
    jRadioButton5 = new JRadioButton();
    jRadioButton6 = new JRadioButton();
    jRadioButton7 = new JRadioButton();
    jRadioButton8 = new JRadioButton();
    jRadioButton9 = new JRadioButton();
    jRadioButton10 = new JRadioButton();
    jRadioButton11 = new JRadioButton();
    jRadioButton12 = new JRadioButton();
    jRadioButton13 = new JRadioButton();
    jRadioButton14 = new JRadioButton();
    jRadioButton15 = new JRadioButton();
    jRadioButton16 = new JRadioButton();
    jRadioButton17 = new JRadioButton();
    jRadioButton18 = new JRadioButton();
    jRadioButton19 = new JRadioButton();
    jRadioButton20 = new JRadioButton();
    jRadioButton21 = new JRadioButton();
    jRadioButton22 = new JRadioButton();
    jRadioButton23 = new JRadioButton();
    buttonGroup1 = new ButtonGroup();
    buttonGroup2 = new ButtonGroup();
    buttonGroup3 = new ButtonGroup();
    buttonGroup4 = new ButtonGroup();
    for (JRadioButton button : new JRadioButton[] {jRadioButton1, jRadioButton2})
      buttonGroup1.add(button);
    for (JRadioButton button : new JRadioButton[] {jRadioButton3, jRadioButton4})
      buttonGroup2.add(button);
    for (JRadioButton button :
        new JRadioButton[] {
          jRadioButton5,
          jRadioButton6,
          jRadioButton7,
          jRadioButton8,
          jRadioButton9,
          jRadioButton10,
          jRadioButton11,
          jRadioButton12,
          jRadioButton13
        }) buttonGroup3.add(button);
    for (JRadioButton button :
        new JRadioButton[] {
          jRadioButton14,
          jRadioButton15,
          jRadioButton16,
          jRadioButton17,
          jRadioButton18,
          jRadioButton19,
          jRadioButton20,
          jRadioButton21,
          jRadioButton22,
          jRadioButton23
        }) buttonGroup4.add(button);
    int mode = main.RS232_connection_mode;
    local_AWS_connected = mode == 3 || mode == 9 || mode == 10 || mode == 11;
    boolean readOnly = local_AWS_connected || main.APR;
    for (JTextField field : new JTextField[] {jTextField1, jTextField2, jTextField3, jTextField4})
      field.setEditable(!readOnly);
    mapAvailable = !readOnly;
    boolean tenMinutes = local_AWS_connected || main.obs_format.equals(main.FORMAT_101);
    String period =
        readOnly ? (tenMinutes ? "10 minutes" : "3 hours") : (tenMinutes ? "10 min" : "3 hrs");
    courseLabel = "course made good during last " + period + " (degr)";
    speedLabel = "speed made good during last " + period + " (knots)";
    initSynopparameters();
  }

  private void Reset_All_Position_Vars() {
    // scope this module + myturbowin.java main module (all of type: static)
    latitude_degrees = "";
    latitude_minutes = "";
    latitude_hemisphere = "";
    longitude_degrees = "";
    longitude_minutes = "";
    longitude_hemisphere = "";
    course = "";
    speed = "";
    lalala_code = "";
    lolololo_code = "";
    Qc_code = "";
    Ds_code = "";
    vs_code = "";
    int_latitude_degrees = main.INVALID;
    int_latitude_minutes = main.INVALID;
    int_longitude_degrees = main.INVALID;
    int_longitude_minutes = main.INVALID;
    // NB do not reset SOG_APR and COG_APR here

    // local var's with initialisation
    checks_ok = false;

    /* position velden op hoofdscherm overzicht updaten */
    main.position_fields_update();
  }

  boolean accept() {
    // TODO add your handling code here:

    if ((main.RS232_connection_mode != 3)
        && (main.RS232_connection_mode != 9)
        && (main.RS232_connection_mode != 10)
        && (main.RS232_connection_mode != 11)) // not AWS connected
    {

      // initialisation
      Reset_All_Position_Vars();

      // initialisation
      checks_ok = true;

      // latitude
      latitude_degrees = jTextField1.getText().trim();
      latitude_minutes = jTextField2.getText().trim();

      if (jRadioButton1.isSelected() == true) {
        latitude_hemisphere = HEMISPHERE_NORTH;
      } else if (jRadioButton2.isSelected() == true) {
        latitude_hemisphere = HEMISPHERE_SOUTH;
      } else {
        latitude_hemisphere = "";
      }

      // longitude
      longitude_degrees = jTextField3.getText().trim();
      longitude_minutes = jTextField4.getText().trim();

      if (jRadioButton3.isSelected() == true) {
        longitude_hemisphere = HEMISPHERE_EAST;
      } else if (jRadioButton4.isSelected() == true) {
        longitude_hemisphere = HEMISPHERE_WEST;
      } else {
        longitude_hemisphere = "";
      }

      // course
      if (jRadioButton5.isSelected() == true) {
        course = COURSE_STATIONARY;
      } else if (jRadioButton6.isSelected() == true) {
        course = COURSE_023_067;
      } else if (jRadioButton7.isSelected() == true) {
        course = COURSE_068_112;
      } else if (jRadioButton8.isSelected() == true) {
        course = COURSE_113_157;
      } else if (jRadioButton9.isSelected() == true) {
        course = COURSE_158_202;
      } else if (jRadioButton10.isSelected() == true) {
        course = COURSE_203_247;
      } else if (jRadioButton11.isSelected() == true) {
        course = COURSE_248_292;
      } else if (jRadioButton12.isSelected() == true) {
        course = COURSE_293_337;
      } else if (jRadioButton13.isSelected() == true) {
        course = COURSE_338_022;
      } else {
        course = "";
      }

      // speed
      if (jRadioButton14.isSelected() == true) {
        speed = SPEED_0;
      } else if (jRadioButton15.isSelected() == true) {
        speed = SPEED_1_5;
      } else if (jRadioButton16.isSelected() == true) {
        speed = SPEED_6_10;
      } else if (jRadioButton17.isSelected() == true) {
        speed = SPEED_11_15;
      } else if (jRadioButton18.isSelected() == true) {
        speed = SPEED_16_20;
      } else if (jRadioButton19.isSelected() == true) {
        speed = SPEED_21_25;
      } else if (jRadioButton20.isSelected() == true) {
        speed = SPEED_26_30;
      } else if (jRadioButton21.isSelected() == true) {
        speed = SPEED_31_35;
      } else if (jRadioButton22.isSelected() == true) {
        speed = SPEED_36_40;
      } else if (jRadioButton23.isSelected() == true) {
        speed = SPEED_MORE_40;
      } else {
        speed = "";
      }

      /*
      //////////////// checks
      */

      // String latitude convert to integer
      try {
        int_latitude_degrees = Integer.parseInt(latitude_degrees.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      try {
        int_latitude_minutes = Integer.parseInt(latitude_minutes.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // String longitude omzetten naar integer
      try {
        int_longitude_degrees = Integer.parseInt(longitude_degrees.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      try {
        int_longitude_minutes = Integer.parseInt(longitude_minutes.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // latitude check error messages
      if (int_latitude_degrees < 0 || int_latitude_degrees > 90 || int_latitude_degrees == 99999) {
        JOptionPane.showMessageDialog(
            null,
            "latitude degrees must be in range 0 - 90",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      } else if (int_latitude_minutes < 0
          || int_latitude_minutes > 59
          || int_latitude_minutes == 99999) {
        JOptionPane.showMessageDialog(
            null,
            "latitude minutes must be in range 0 - 59",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      } else if (latitude_hemisphere.equals("") == true) {
        JOptionPane.showMessageDialog(
            null,
            "latitude hemisphere not set",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      }

      // longitude check error messages
      else if (int_longitude_degrees < 0
          || int_longitude_degrees > 180
          || int_longitude_degrees == 99999) {
        JOptionPane.showMessageDialog(
            null,
            "longitude degrees must be in range 0 - 180",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      } else if (int_longitude_minutes < 0
          || int_longitude_minutes > 59
          || int_longitude_minutes == 99999) {
        JOptionPane.showMessageDialog(
            null,
            "longitude minutes must be in range 0 - 59",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      } else if (longitude_hemisphere.equals("") == true) {
        JOptionPane.showMessageDialog(
            null,
            "longitude hemisphere not set",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      }

      // course check
      else if (course.equals("") == true) {
        JOptionPane.showMessageDialog(
            null,
            "course made good not set",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      }

      // speed check
      else if (speed.equals("") == true) {
        JOptionPane.showMessageDialog(
            null,
            "speed made good not set",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Position_Vars();
      } else {
        checks_ok = true;
      }

      // combined checks
      if (checks_ok == true) {
        if ((course.equals(COURSE_STATIONARY) == true) && (speed.equals(SPEED_0) == false)) {
          JOptionPane.showMessageDialog(
              null,
              "if course made good = 'stationary' than speed made good must be '0'",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Position_Vars();
        } else if ((speed.equals(SPEED_0) == true) && (course.equals(COURSE_STATIONARY) == false)) {
          JOptionPane.showMessageDialog(
              null,
              "if speed made good = '0' than course made good must be 'stationary'",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Position_Vars();
        }
      } // if (checks_ok == true)

      if (checks_ok == true) {
        // determine code figure quadrant of the globe
        //
        if ((latitude_hemisphere.equals(HEMISPHERE_NORTH) == true)
            && (longitude_hemisphere.equals(HEMISPHERE_EAST) == true)) {
          Qc_code = "1";
        } else if ((latitude_hemisphere.equals(HEMISPHERE_SOUTH) == true)
            && (longitude_hemisphere.equals(HEMISPHERE_EAST) == true)) {
          Qc_code = "3";
        } else if ((latitude_hemisphere.equals(HEMISPHERE_SOUTH) == true)
            && (longitude_hemisphere.equals(HEMISPHERE_WEST) == true)) {
          Qc_code = "5";
        } else if ((latitude_hemisphere.equals(HEMISPHERE_NORTH) == true)
            && (longitude_hemisphere.equals(HEMISPHERE_WEST) == true)) {
          Qc_code = "7";
        } else {
          JOptionPane.showMessageDialog(
              null,
              "internal error: Qc_code",
              main.APPLICATION_NAME + " error",
              JOptionPane.ERROR_MESSAGE); // bug check message
        }

        // determine code figures (latitude)
        //
        int int_latitude_minutes_6 =
            int_latitude_minutes / 6; // devide the minutes by six and disregard the remainder
        String latitude_minutes_6 = Integer.toString(int_latitude_minutes_6); // convert to String

        if (latitude_minutes_6.length() != 1) // debug check
        {
          JOptionPane.showMessageDialog(
              null,
              "internal error: latitude_minutes_6",
              main.APPLICATION_NAME + " error",
              JOptionPane.ERROR_MESSAGE);
        }

        // lalala_code = latitude_degrees.trim() + latitude_minutes_6;
        lalala_code = latitude_degrees.trim().replaceFirst("^0+(?!$)", "") + latitude_minutes_6;

        int len = 3; // always 3 chars
        if (lalala_code.length() < len) {
          // pad on left with zeros
          lalala_code = "0000000000".substring(0, len - lalala_code.length()) + lalala_code;
        }

        // determine code figures (longitude)
        //
        int int_longitude_minutes_6 =
            int_longitude_minutes / 6; // devide the minutes by six and disregard the remainder
        String longitude_minutes_6 = Integer.toString(int_longitude_minutes_6); // convert to String

        if (longitude_minutes_6.length() != 1) // debug check
        {
          JOptionPane.showMessageDialog(
              null,
              "internal error: longitude_minutes_6",
              main.APPLICATION_NAME + " error",
              JOptionPane.ERROR_MESSAGE);
        }

        // lolololo_code = longitude_degrees.trim() + longitude_minutes_6;
        lolololo_code = longitude_degrees.trim().replaceFirst("^0+(?!$)", "") + longitude_minutes_6;

        len = 4; // always 4 chars
        if (lolololo_code.length() < len) // pad on left with zeros
        {
          lolololo_code = "0000000000".substring(0, len - lolololo_code.length()) + lolololo_code;
        }

        // determine code figure course
        //
        if (course.equals(COURSE_STATIONARY) == true) // stopped
        {
          Ds_code = "0";
        } else if (course.equals(COURSE_023_067) == true) // NE
        {
          Ds_code = "1";
        } else if (course.equals(COURSE_068_112) == true) // E
        {
          Ds_code = "2";
        } else if (course.equals(COURSE_113_157) == true) // SE
        {
          Ds_code = "3";
        } else if (course.equals(COURSE_158_202) == true) // S
        {
          Ds_code = "4";
        } else if (course.equals(COURSE_203_247) == true) // SW
        {
          Ds_code = "5";
        } else if (course.equals(COURSE_248_292) == true) // W
        {
          Ds_code = "6";
        } else if (course.equals(COURSE_293_337) == true) // NW
        {
          Ds_code = "7";
        } else if (course.equals(COURSE_338_022) == true) // N
        {
          Ds_code = "8";
        } else {
          Ds_code =
              "9"; // no information (could be Ds_code = "/" also, because two options to code Ds
          // undefined)
        }

        // determine code figure speed
        //
        if (speed.equals(SPEED_0) == true) {
          vs_code = "0";
        } else if (speed.equals(SPEED_1_5) == true) {
          vs_code = "1";
        } else if (speed.equals(SPEED_6_10) == true) {
          vs_code = "2";
        } else if (speed.equals(SPEED_11_15) == true) {
          vs_code = "3";
        } else if (speed.equals(SPEED_16_20) == true) {
          vs_code = "4";
        } else if (speed.equals(SPEED_21_25) == true) {
          vs_code = "5";
        } else if (speed.equals(SPEED_26_30) == true) {
          vs_code = "6";
        } else if (speed.equals(SPEED_31_35) == true) {
          vs_code = "7";
        } else if (speed.equals(SPEED_36_40) == true) {
          vs_code = "8";
        } else if (speed.equals(SPEED_MORE_40) == true) {
          vs_code = "9";
        } else {
          vs_code = "/";
        }
      } // if (checks_ok == true)

      /* put ship position on a leaflet map (but only if an internet connection is available (and appropriate checkbox ticked and a logs_dir is available */
      if ((checks_ok == true) && (plotPosition)) {
        PositionLeafletMapWorkflow.start(() ->
            Position_In_leaflet_Maps(
                main.logs_dir + java.io.File.separator + main.LEAFLET_MAPS_HTML_FILE));
      }

    } // if (main.RS232_connection_mode != 3) etc.
    else // AWS connected mode
    {
      checks_ok = true;
    }

    // BELOW MAYBE IN NEXT STAGE
    // if (checks_ok && (main.APR == true))
    // {
    //   // so the manual position will most probably be entered by the observer if in APR mode and
    // if the position from the GPS is not ok or if obsolete
    //   main.obsolate_GPS_data_flag = false;
    //
    //   // for timer checking the GPS data is not obsolete (see Function:
    // RS232_GPS_NMEA_0183_init_new_data_received_check_timer)
    //   main_RS232_RS422.last_new_GPS_data_received_TimeMillis = System.currentTimeMillis();
    //
    // } // if (checks_ok && (main.APR == true))

    if (checks_ok == true) {
      /* position velden op hoofdscherm overzicht updaten */
      main.position_fields_update();
    } // if (checks_ok == true)
    return checks_ok;
  } // GEN-LAST:event_OK_button_actionPerformed

  void setPlotPosition(boolean selected) {
    plotPosition = selected;
  }

  boolean plotPositionAvailable() {
    return mapAvailable;
  }

  String courseLabel() {
    return courseLabel;
  }

  String speedLabel() {
    return speedLabel;
  }

  void cancelCleanup() {
    if (!main.APR) myposition_windowClosing(null);
  }

  void windowClosingCleanup() {
    myposition_windowClosing(null);
  }

  private void Position_In_leaflet_Maps(String full_path_leaflet_maps_html_file) {
    // called from: OK_button_actionPerformed() --doInBackground--- [myposition.java]

    // make a dynamic html file
    // NB Before invoking this function the existence of the output dir (the logs dir) is already
    // checked positive
    //
    // voor AIS info see: http://www.marinetraffic.com/ais/nl/addyourarea.aspx?level1=150 (English
    // version: http://www.marinetraffic.com/ais/addyourarea.aspx?level1=150)

    float float_leaflet_maps_lat = int_latitude_degrees + (float) int_latitude_minutes / 60;
    if (latitude_hemisphere.equals(HEMISPHERE_SOUTH) == true) {
      float_leaflet_maps_lat *= -1;
    }

    float float_leaflet_maps_lon = int_longitude_degrees + (float) int_longitude_minutes / 60;
    if (longitude_hemisphere.equals(HEMISPHERE_WEST) == true) {
      float_leaflet_maps_lon *= -1;
    }

    String leaflet_maps_lat = Float.toString(float_leaflet_maps_lat);
    String leaflet_maps_lon = Float.toString(float_leaflet_maps_lon);

    //
    // data for the (pop-up) infoWindow (attached to the position marker on the leaflet map)
    //
    String infowindow_lat =
        latitude_degrees + "&#176 " + latitude_minutes + "' " + latitude_hemisphere.substring(0, 1);
    String infowindow_lon =
        longitude_degrees
            + "&#176 "
            + longitude_minutes
            + "' "
            + longitude_hemisphere.substring(0, 1);

    // date time
    String infowindow_date_time = "";
    if (!main.leaflet_maps_obs_day.equals(
        "")) // NB if day is not blank also the other date/time parameters are not blank
    {
      infowindow_date_time =
          main.leaflet_maps_obs_day
              + " "
              + main.leaflet_maps_obs_month
              + " "
              + main.leaflet_maps_obs_year
              + "  "
              + main.leaflet_maps_obs_hour
              + ".00 UTC";
    }

    // wind dir
    String infowindow_wind_dir = "wind dir: ";
    if (!main.leaflet_maps_obs_wind_dir.equals("")) {
      infowindow_wind_dir = "wind dir: " + main.leaflet_maps_obs_wind_dir;
    }

    // wind speed
    String infowindow_wind_speed = "wind speed: ";
    if (!main.leaflet_maps_obs_wind_speed.equals("")) {
      infowindow_wind_speed = "wind speed: " + main.leaflet_maps_obs_wind_speed;
    }

    // air temp
    String infowindow_air_temp = "air temp: ";
    if (!main.leaflet_maps_obs_air_temp.equals("")) {
      infowindow_air_temp = "air temp: " + main.leaflet_maps_obs_air_temp;
    }

    // sst
    String infowindow_sst = "sea surface temp: ";
    if (!main.leaflet_maps_obs_sst.equals("")) {
      infowindow_sst = "sea surface temp: " + main.leaflet_maps_obs_sst;
    }

    // MSL air pressure
    String infowindow_msl_pressure = "MSL air pressure: ";
    if (!main.leaflet_maps_obs_msl_pressure.equals("")) {
      infowindow_msl_pressure = "MSL air pressure: " + main.leaflet_maps_obs_msl_pressure;
    }

    // name vessel
    String vessel = main.ship_name;

    String infowindow_total =
        "\""
            + "<h3>"
            + vessel
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
            + infowindow_wind_dir
            + "<br>"
            + infowindow_wind_speed
            + "<br>"
            + infowindow_air_temp
            + "<br>"
            + infowindow_sst
            + "<br>"
            + infowindow_msl_pressure
            + "<br>"
            + "</p>"
            + "\"";

    //
    // write HTML/JS code
    //
    try (BufferedWriter out =
        new BufferedWriter(new FileWriter(full_path_leaflet_maps_html_file))) {
      out.write("<html>");
      out.newLine();
      out.write("<head>");
      out.newLine();
      out.write("  <meta charset=utf-8 />");
      out.newLine();
      out.newLine();
      out.write("  <title>TurboWin</title>");
      out.newLine();
      out.write(
          "  <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />");
      out.newLine();
      out.newLine();
      out.write("  <!-- Load Leaflet from CDN -->");
      out.newLine();
      out.write(main.LEAFLET_CSS_URL);
      out.newLine();
      out.write(main.LEAFLET_CSS_INTEGRITY);
      out.newLine();
      out.write("  crossorigin=\"\"/>");
      out.newLine();
      out.write(main.LEAFLET_JS_URL);
      out.newLine();
      out.write(main.LEAFLET_JS_INTEGRITY);
      out.newLine();
      out.write("  crossorigin=\"\"></script>");
      out.newLine();
      out.newLine();
      out.write("  <!-- Load Esri Leaflet from CDN -->");
      out.newLine();
      out.write(main.LEAFLET_ESRI_URL);
      out.newLine();
      out.write(main.LEAFLET_ESRI_INTEGRITY);
      out.newLine();
      out.write("  crossorigin=\"\"></script>");
      out.newLine();
      out.newLine();
      out.write("  <style>");
      out.newLine();
      out.write("    body { margin:0; padding:0; }");
      out.newLine();
      out.write("    #map { position: absolute; top:0; bottom:0; right:0; left:0; }");
      out.newLine();
      out.write("  </style>");
      out.newLine();
      out.newLine();
      out.write("</head>");
      out.newLine();
      out.newLine();
      out.write("<body>");
      out.newLine();
      out.write("<div id=\"map\"></div>");
      out.newLine();
      out.newLine();
      out.write("<script>");
      out.newLine();
      out.write(
          "  var map = L.map(\"map\").setView(["
              + leaflet_maps_lat
              + ","
              + leaflet_maps_lon
              + "], 3);");
      out.newLine();
      out.write("  L.esri.basemapLayer(\"Topographic\").addTo(map);");
      out.newLine();
      out.newLine();
      // out.write("  L.marker([" + leaflet_maps_lat + "," + leaflet_maps_lon +
      // "]).addTo(map).bindPopup(\"<b>Hello world!</b><br />I am a
      // popup.\").openPopup();");out.newLine();
      out.write(
          "  L.marker(["
              + leaflet_maps_lat
              + ","
              + leaflet_maps_lon
              + "]).addTo(map).bindPopup("
              + infowindow_total
              + ").openPopup();");
      out.newLine();
      out.newLine();
      out.write("</script>");
      out.newLine();
      out.newLine();
      out.write("</body>");
      out.newLine();
      out.write("</html>");
      out.newLine();

    } // try
    catch (IOException ex) {
      String info =
          "[GENERAL] unable to create dynamic html file for leaflet maps plot (" + ex + ")";
      main.log_turbowin_system_message(info);
    } // catch
  }

  private void myposition_windowClosing(
      java.awt.event.WindowEvent evt) { // GEN-FIRST:event_myposition_windowClosing
    // TODO add your handling code here:

    // NB when pressing OK button the program will not enter this function
    // myposition_windowClosing()
    // JOptionPane.showMessageDialog(null, "myposition_windowClosing",  main.APPLICATION_NAME + "
    // test", JOptionPane.WARNING_MESSAGE);

    if (main.RS232_GPS_connection_mode == 1 && GPS_ok) // GPS connected  and fix ok
    {
      // see function initSynopparameters(), in this function the myposition vars are set if GPS
      // connected and fix OK
      Reset_All_Position_Vars();
    }
  } // GEN-LAST:event_myposition_windowClosing

  private void initSynopparameters() {
    // initialisation
    GPS_ok = true;

    // GPS connection
    if (main.RS232_GPS_connection_mode == 4) // GPS integrated Mintaka StarX
    {
      if (main.obs_format.equals(main.FORMAT_101) || main.obs_format.equals(main.FORMAT_FM13)) {
        // for correct compting obs format must beset before to format101 or FM13
        boolean StarX = true;
        RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs("MANUAL", StarX);
      } else {
        String message = "[GPS] obs format not set to format101 or FM13";
        JOptionPane.showMessageDialog(
            null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      }
      // NB inserting values in jTextfields etc was done in the function:
      // RS232_Mintaka_Star_Read_Sensor_Data_GPS_For_Obs("MANUAL");
    } else if (main.RS232_GPS_connection_mode == 3) // GPS integrated Mintaka Star
    {
      if (main.obs_format.equals(main.FORMAT_101) || main.obs_format.equals(main.FORMAT_FM13)) {
        // for correct compting obs format must be set before to format101 or FM13
        boolean StarX = false;
        RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_GPS_For_Obs("MANUAL", StarX);
      } else {
        String message = "[GPS] obs format not set to format101 or FM13";
        JOptionPane.showMessageDialog(
            null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
      }
      // NB inserting values in jTextfields etc was done in the function:
      // RS232_Mintaka_Star_Read_Sensor_Data_GPS_For_Obs("MANUAL");
    } else if (main.RS232_GPS_connection_mode
        == 1) // GPS NMEA 0183 (separate GPS, not barometer integrated)
    {
      GPS_ok = main_RS232_RS422.RS232_GPS_NMEA_0183_Date_Position_Parsing("MANUAL");

      if (GPS_ok) {
        // latitude
        if (latitude_degrees.compareTo("") != 0) {
          jTextField1.setText(latitude_degrees);
        }

        if (latitude_minutes.compareTo("") != 0) {
          jTextField2.setText(latitude_minutes);
        }

        if (latitude_hemisphere.compareTo("") != 0) {
          if (latitude_hemisphere.equals(HEMISPHERE_NORTH)) {
            jRadioButton1.setSelected(true);
          } else if (latitude_hemisphere.equals(HEMISPHERE_SOUTH)) {
            jRadioButton2.setSelected(true);
          }
        } // if (latitude_hemisphere != "")

        // longitude
        if (longitude_degrees.compareTo("") != 0) {
          jTextField3.setText(longitude_degrees);
        }

        if (longitude_minutes.compareTo("") != 0) {
          jTextField4.setText(longitude_minutes);
        }

        if (longitude_hemisphere.compareTo("") != 0) {
          if (longitude_hemisphere.equals(HEMISPHERE_EAST)) {
            jRadioButton3.setSelected(true);
          } else if (longitude_hemisphere.equals(HEMISPHERE_WEST)) {
            jRadioButton4.setSelected(true);
          }
        } // if (latitude_hemisphere != "")

        if (SOG_APR < 1.0) // stopped
        {
          // by default: in case SOG is indicating stopped then SOG always stopped also!
          myposition.jRadioButton5.setSelected(true); // COG = stopped
        }
        // else if (SOG >= 1.0 && SOG <= 999.9) // not stopped (halverwege de test maar veranderd om
        // toch een richting tijdens laatste testjes te krijgen)
        else if (SOG_APR >= 1.0 && SOG_APR <= 999999.9) // not stopped
        {
          if (COG_APR > 23.0 && COG_APR <= 67.0) {
            myposition.jRadioButton6.setSelected(true);
          } else if (COG_APR > 67.0 && COG_APR <= 112.0) {
            myposition.jRadioButton7.setSelected(true);
          } else if (COG_APR > 112.0 && COG_APR <= 157.0) {
            myposition.jRadioButton8.setSelected(true);
          } else if (COG_APR > 157.0 && COG_APR <= 202.0) {
            myposition.jRadioButton9.setSelected(true);
          } else if (COG_APR > 202.0 && COG_APR <= 247.0) {
            myposition.jRadioButton10.setSelected(true);
          } else if (COG_APR > 247.0 && COG_APR <= 292.0) {
            myposition.jRadioButton11.setSelected(true);
          } else if (COG_APR > 292.0 && COG_APR <= 337.0) {
            myposition.jRadioButton12.setSelected(true);
          } else if (COG_APR > 337.0 && COG_APR <= 360.0 || COG_APR > 0.0 && COG_APR <= 23.0) {
            myposition.jRadioButton13.setSelected(true);
          }
        } // else if (SOG >= 1.0 && SOG <= 99.9)

        // speed
        // SOG
        //
        //
        // NB WMO_NO_306.pdf
        //
        // WMO table  4451
        // vs Ships average speed made good during the three hours preceding the time of observation
        // Code
        // figure
        // 0 0 knot 0 km h 1
        // 1 1 5 knots 1 10 km h 1
        // 2 6 10 knots 11 19 km h 1
        // 3 11 15 knots 20 28 km h 1
        // 4 16 20 knots 29 37 km h 1
        // 5 21 25 knots 38 47 km h 1
        // 6 26 30 knots 48 56 km h 1
        // 7 31 35 knots 57 65 km h 1
        // 8 36 40 knots 66 75 km h 1
        // 9 Over 40 knots Over 75 km h 1
        /// Not applicable (report from a coastal land station) or not reported (see Regulation
        // 12.3.1.2 (b))
        if (SOG_APR < 1.0) {
          myposition.jRadioButton14.setSelected(true);
        } else if (SOG_APR >= 1.0 && SOG_APR <= 5.0) {
          myposition.jRadioButton15.setSelected(true);
        } else if (SOG_APR > 5.0 && SOG_APR <= 10.0) {
          myposition.jRadioButton16.setSelected(true);
        } else if (SOG_APR > 10.0 && SOG_APR <= 15.0) {
          myposition.jRadioButton17.setSelected(true);
        } else if (SOG_APR > 15.0 && SOG_APR <= 20.0) {
          myposition.jRadioButton18.setSelected(true);
        } else if (SOG_APR > 20.0 && SOG_APR <= 25.0) {
          myposition.jRadioButton19.setSelected(true);
        } else if (SOG_APR > 25.0 && SOG_APR <= 30.0) {
          myposition.jRadioButton20.setSelected(true);
        } else if (SOG_APR > 30.0 && SOG_APR <= 35.0) {
          myposition.jRadioButton21.setSelected(true);
        } else if (SOG_APR > 35.0 && SOG_APR <= 40.0) {
          myposition.jRadioButton22.setSelected(true);
        } else if (SOG_APR > 40 && SOG_APR <= 99) {
          myposition.jRadioButton23.setSelected(true);
        }
      } // if (GPS_date_time_ok )

    } // if (main.RS232_GPS_connection_mode == 1)
    else if (main.RS232_GPS_connection_mode
        == 0) // no GPS connected (noat a separate GPS and not a baromyter integrated GPS)
    {
      // put back earlier inserted values (if applicable)
      //

      // latitude
      if (latitude_degrees.compareTo("") != 0) jTextField1.setText(latitude_degrees);

      if (latitude_minutes.compareTo("") != 0) jTextField2.setText(latitude_minutes);

      if (latitude_hemisphere.compareTo("") != 0) {
        if (latitude_hemisphere.equals(HEMISPHERE_NORTH)) jRadioButton1.setSelected(true);
        else if (latitude_hemisphere.equals(HEMISPHERE_SOUTH)) jRadioButton2.setSelected(true);
      } // if (latitude_hemisphere != "")

      // longitude
      if (longitude_degrees.compareTo("") != 0) jTextField3.setText(longitude_degrees);

      if (longitude_minutes.compareTo("") != 0) jTextField4.setText(longitude_minutes);

      if (longitude_hemisphere.compareTo("") != 0) {
        if (longitude_hemisphere.equals(HEMISPHERE_EAST)) jRadioButton3.setSelected(true);
        else if (longitude_hemisphere.equals(HEMISPHERE_WEST)) jRadioButton4.setSelected(true);
      } // if (latitude_hemisphere != "")
    } // else if (main.RS232_GPS_connection_mode == 0)
    else {
      String info = "RS232_GPS_connection_mode unknown";
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
      System.out.println(info);
    }

    // course
    if (course.compareTo("") != 0) {
      if (course.equals(COURSE_STATIONARY)) jRadioButton5.setSelected(true);
      else if (course.equals(COURSE_023_067)) jRadioButton6.setSelected(true);
      else if (course.equals(COURSE_068_112)) jRadioButton7.setSelected(true);
      else if (course.equals(COURSE_113_157)) jRadioButton8.setSelected(true);
      else if (course.equals(COURSE_158_202)) jRadioButton9.setSelected(true);
      else if (course.equals(COURSE_203_247)) jRadioButton10.setSelected(true);
      else if (course.equals(COURSE_248_292)) jRadioButton11.setSelected(true);
      else if (course.equals(COURSE_293_337)) jRadioButton12.setSelected(true);
      else if (course.equals(COURSE_338_022)) jRadioButton13.setSelected(true);
    } // if (course.compareTo("") != 0)

    // speed
    if (speed.compareTo("") != 0) {
      if (speed.equals(SPEED_0)) jRadioButton14.setSelected(true);
      else if (speed.equals(SPEED_1_5)) jRadioButton15.setSelected(true);
      else if (speed.equals(SPEED_6_10)) jRadioButton16.setSelected(true);
      else if (speed.equals(SPEED_11_15)) jRadioButton17.setSelected(true);
      else if (speed.equals(SPEED_16_20)) jRadioButton18.setSelected(true);
      else if (speed.equals(SPEED_21_25)) jRadioButton19.setSelected(true);
      else if (speed.equals(SPEED_26_30)) jRadioButton20.setSelected(true);
      else if (speed.equals(SPEED_31_35)) jRadioButton21.setSelected(true);
      else if (speed.equals(SPEED_36_40)) jRadioButton22.setSelected(true);
      else if (speed.equals(SPEED_MORE_40)) jRadioButton23.setSelected(true);
    } // if (speed.compareTo("") != 0)
  } // private void initSynopparameters()

  public static final String HEMISPHERE_NORTH = "North";
  public static final String HEMISPHERE_SOUTH = "South";
  public static final String HEMISPHERE_EAST = "East";
  public static final String HEMISPHERE_WEST = "West";

  public static final String COURSE_STATIONARY = "stationary";
  public static final String COURSE_023_067 = "023 - 067";
  public static final String COURSE_068_112 = "068 - 112";
  public static final String COURSE_113_157 = "113 - 157";
  public static final String COURSE_158_202 = "158 - 202";
  public static final String COURSE_203_247 = "203 - 247";
  public static final String COURSE_248_292 = "248 - 292";
  public static final String COURSE_293_337 = "293 - 337";
  public static final String COURSE_338_022 = "338 - 022";

  public static final String SPEED_0 = "0";
  public static final String SPEED_1_5 = "1 - 5";
  public static final String SPEED_6_10 = "6 - 10";
  public static final String SPEED_11_15 = "11- 15";
  public static final String SPEED_16_20 = "16 - 20";
  public static final String SPEED_21_25 = "21 - 25";
  public static final String SPEED_26_30 = "26 - 30";
  public static final String SPEED_31_35 = "31 - 35";
  public static final String SPEED_36_40 = "36 - 40";
  public static final String SPEED_MORE_40 = "> 40";

  // public static final int INVALID              = 9999999;

  // global var's
  public static String latitude_degrees = "";
  public static String latitude_minutes = "";
  public static String latitude_hemisphere = "";
  public static String longitude_degrees = "";
  public static String longitude_minutes = "";
  public static String longitude_hemisphere = "";
  public static String course = "";
  public static String speed = "";
  public static String lalala_code = ""; // always 3 chars
  public static String lolololo_code = ""; // always 4 chars
  public static String Qc_code = ""; // always 1 chars
  public static String Ds_code = "";
  public static String vs_code = "";
  public static int int_latitude_degrees =
      main.INVALID; // also used for position sequence check and cloud height advice computation
  public static int int_latitude_minutes = main.INVALID; // also used for position sequence check
  public static int int_longitude_degrees =
      main.INVALID; // also used for position sequence check and cloud height advice computation
  public static int int_longitude_minutes = main.INVALID; // also used for position sequence check
  public static double SOG_APR = Double.MAX_VALUE; // used for "APR + format101 and FM13"
  public static double COG_APR = Double.MAX_VALUE; // used for "APR + format101 and FM13"
  public static double SOG_APR_wind =
      Double.MAX_VALUE; // used for "APR + FM13" (APR SOG is based on 3 hrs if FM13, so extra 10 min
  // SOG for wind necessary)
  public static double COG_APR_wind =
      Double.MAX_VALUE; // used for "APR + FM13" (APR COG is based on 3 hrs if FM13, so extra 10 min
  // COG for wind necessary, also used if port mode option is set)

  // locale var's with initialisation
  boolean checks_ok = false;
  private boolean GPS_ok = true;
  private boolean local_AWS_connected;
}
