package turbowin;

import static turbowin.main.*;

/** Owns instrument-specific startup initialization and related UI state. */
final class ConnectionInitializationWorkflow {

  private ConnectionInitializationWorkflow() {}

  static void configureReportCheckboxes(
      int connectionMode, javax.swing.JCheckBox aprCheckBox, javax.swing.JCheckBox awsrCheckBox) {
    aprCheckBox.setEnabled(true);
    awsrCheckBox.setEnabled(true);

    // Mode 3 is the EUCAWS instrument; it does not expose APR or AWSR settings.
    if (connectionMode == 3) {
      aprCheckBox.setEnabled(false);
      awsrCheckBox.setEnabled(false);
    }
    // Modes 9/10/11 are OMC-140 serial, OMC-140 LAN, and AMOS2X serial AWS instruments.
    if (connectionMode == 9 || connectionMode == 10 || connectionMode == 11) {
      aprCheckBox.setEnabled(false);
    }
    // AWSR settings require an AWS connection (EUCAWS, OMC-140, or AMOS2X).
    if (connectionMode != 3
        && connectionMode != 9
        && connectionMode != 10
        && connectionMode != 11) {
      awsrCheckBox.setEnabled(false);
    }
    // Mode 0 means that no instrument is connected; neither report mode is available.
    if (connectionMode == 0) {
      aprCheckBox.setEnabled(false);
      awsrCheckBox.setEnabled(false);
    }
  }

  static void initialize(
      boolean themeChanged,
      int connectionMode,
      int secondaryConnectionMode,
      int gpsConnectionMode,
      main_RS232_RS422 serial,
      javax.swing.JLabel instructionLabel,
      javax.swing.JLabel humidityLabel,
      javax.swing.JLabel sensorLabel,
      javax.swing.JLabel gpsLabel,
      javax.swing.JCheckBox aprCheckBox,
      javax.swing.JCheckBox awsrCheckBox) {

    // called from: - read_muffin() [main.java]
    //              - lees_configuratie_regels() [main.java]

    //
    //////// RS232/RS422/WiFi //////
    //
    if (themeChanged) {
      // NB if transparent scheme all the necessary start-up items were all ready done
      //    so not necessary to invoke e.g. serial.RS232_initComponents(); etc becuase they are
      // still running

      // text field labels
      if (connectionMode == 3
          || connectionMode == 9
          || connectionMode == 11) // EUCOS AWS serial or OMC AWS serial or AMOS2X serial
      {
        // in AWS mode no dew point but relative humidity
        humidityLabel.setText("relative humidity");

        // in AWS mode update info
        sensorLabel.setText("--- sensor data updated every minute ---");
      } else if (connectionMode == 10) // OMC-140 ethernet LAN
      {
        // in AWS mode no dew point but relative humidity
        humidityLabel.setText("relative humidity");

        // in AWS mode update info
        sensorLabel.setText("--- sensor data updated every minute ---");
      }

      // in transparant theme mode no popup menu avaialable so over write the appropriate text on
      // the main menu
      instructionLabel.setText(
          "--- adding data: input menu, toolbar icons or click on the text labels or fields ---");

      // Text on label6 was set only once at start up in function
      // RS232_GPS_NMEA_0183_initComponents() [main_serial]
      if (main_RS232_RS422.GPS_defaultPort != null) {
        // System.out.println("+++ GPS_defaultPort = " + GPS_defaultPort);
        // info text on (bottom) main screen
        gpsLabel.setText(
            main.APPLICATION_NAME + " receiving GPS data via serial communication.....");
      }

    } // if (themeChanged)
    else // no Theme change
    {
      // NB at first time start-up of this application it will never be the trnsparent scheme so all
      // items below will be invoked/set the first time
      if (connectionMode == 1
          || connectionMode == 2
          || connectionMode == 4
          || connectionMode == 5
          || connectionMode
              == 7) // PTB220 or PTB330 or Mintaka Duo or Mintaka Star USB or Mintaka Star + StarX
      // USB
      {
        serial.RS232_initComponents(); // for Vaisala/Mintaka barometers (not Mintaka Star Wifi)
      } else if (connectionMode == 3
          || connectionMode == 9
          || connectionMode == 11) // EUCOS AWS serial or OMC AWS serial or AMOS2X serial
      {
        /* in AWS mode input items like call sign, position, date/time etc. are not asked */
        // disable_aws_input_menu_items();

        /* in AWS mode Output -> obs to file etc. disable */
        // disable_aws_output_menu_items();

        /* in AWS mode no dew point but relative humidity */
        humidityLabel.setText("relative humidity");

        /* in AWS mode update info */
        // jLabel8.setText("--- sensor data updated every minute ---");
        sensorLabel.setText("--- sensor data updated every minute ---");

        serial.RS422_initComponents();
      } else if (connectionMode == 6
          || connectionMode == 8) // Mintaka Star WiFi or Mintaka Star + StarX WiFi
      {
        serial.WiFi_initComponents();
      } else if (connectionMode == 10) // OMC-140 ethernet LAN
      {
        /* in AWS mode no dew point but relative humidity */
        humidityLabel.setText("relative humidity");

        /* in AWS mode update info */
        // jLabel8.setText("--- sensor data updated every minute ---");
        sensorLabel.setText("--- sensor data updated every minute ---");

        serial.Ethernet_initComponents();
      }

      //
      //////// 2nd RS232 //////
      //

      /////////////////////// TEST BEGIN /////////////////
      // secondaryConnectionMode = 1;
      ////////////////////// TEST END ////////////////

      if (secondaryConnectionMode == 1) {
        serial.RS232_initComponents_II();
      }

      //
      ////////// GPS connected ?
      //
      if (gpsConnectionMode == 1) // 0 = no GPS; 1 = GPS (NMEA 1083)
      {
        serial.RS232_GPS_NMEA_0183_initComponents();
      }

      //
      ////////// check date and time (and ask observer for confirmation)
      //
      if (connectionMode != 3
          && connectionMode != 9
          && connectionMode != 10
          && connectionMode != 11) // not AWS connected mode
      {
        check_and_set_datetime_v2(); // NB var use_system_date_time_for_updating = true can be set
        // here, used in case of an connected barometer [static]
      } else // AWS connected
      {
        // in AWS connected mode the date time is updated when reading the incoming AWS measured
        // data
        use_system_date_time_for_updating =
            false; // in AWS connected mode the date time is always! updated when reading the
        // incoming AWS measured data [static]

        // but if an instrument is connected but this is not an  AWS, so a barometer is connected
        // then the date time will be automatically inserted on main screen
      }
    } // else (no Theme change)

    configureReportCheckboxes(connectionMode, aprCheckBox, awsrCheckBox);

    // set start-up sequence finished flag
    turbowin_start_up_sequence_finished = true;
  }
}
