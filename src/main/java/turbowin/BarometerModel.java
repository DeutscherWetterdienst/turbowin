package turbowin;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

/** Barometer observation state, calculations and compatibility fields for sensor readers. */
public final class BarometerModel {
  public static JTextField jTextField1;
  public static JTextField jTextField2;
  private final String notice;

  public BarometerModel() {
    int mode = main.RS232_connection_mode;
    local_AWS_connected = mode == 3 || mode == 9 || mode == 10 || mode == 11;
    local_barometer_connected = mode == 1 || mode == 2 || (mode >= 4 && mode <= 8);
    jTextField1 = new JTextField();
    jTextField2 = new JTextField();
    NumericInputWorkflow.decimal(jTextField1);
    NumericInputWorkflow.decimal(jTextField2);
    jTextField1.setEditable(!local_AWS_connected && !main.APR);
    jTextField2.setEditable(!local_AWS_connected && !main.APR);
    jTextField2.setEnabled(!main.PRESSURE_READING_MSL_YES.equals(main.pressure_reading_msl_yes_no));
    if (mode == 0) {
      notice =
          "--- Ensure the bridge wing door is opened to equalize the pressure before reading ---";
    } else if (!main.APR && (mode == 1 || mode == 2 || mode == 4 || mode == 5 || mode == 6)) {
      notice =
          "--- Ensure the bridge wing door was opened to equalize the pressure before this page was opened ---";
    } else {
      notice = "";
    }
    initSynopparameters();
  }

  String notice() {
    return notice;
  }

  private record ManualReduction(
      double pressure, double height, double lakeCorrection, String previousResult) {}

  private static ManualReduction manualReduction;

  static void clearManualReduction() {
    manualReduction = null;
  }

  // Temperature is normally entered after pressure in the observation sequence.
  static boolean refreshManualPressureReduction() {
    ManualReduction reduction = manualReduction;
    if (reduction == null
        || !reduction.previousResult().equals(pressure_msl_corrected)
        || !Double.toString(reduction.pressure()).equals(pressure_reading_corrected)
        || PPPP_code.isEmpty()
        || !main.PRESSURE_READING_MSL_NO.equals(main.pressure_reading_msl_yes_no)) {
      return false;
    }
    double pressure =
        reduction.pressure()
            + SeaLevelPressureReduction.heightCorrection(
                reduction.pressure(), reduction.height(), TemperatureModel.air_temp)
            + reduction.lakeCorrection();
    pressure_msl_corrected = Double.toString(Math.floor(pressure * 10 + 0.5) / 10);
    PPPP_code = PressureCodeFormatter.format(pressure);
    manualReduction =
        new ManualReduction(
            reduction.pressure(),
            reduction.height(),
            reduction.lakeCorrection(),
            pressure_msl_corrected);
    return true;
  }

  private void MSL_Herleiding_Luchtdruk() {
    clearManualReduction();
    double hoogte_corr;
    double double_hoogte_barometer;
    double double_barometer_above_sll = 0.0;
    double double_keel_sll = 0.0;
    // double double_pressure_msl;

    // NB double_hoogte_barometer in meters

    if (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_YES) == true) {
      // NB dat "main.pressure_reading_msl_yes_no" een "yes" of "no" waarde heeft was al op
      // gechecked

      // barometer reading indicate MSL pressure
      pressure_msl_corrected = pressure_reading_corrected;
      double_pressure_msl_corrected = double_pressure_reading_corrected;
    } else // barometer reading doesn't indicate MSL pressure
    {
      /*
      /////////// checks
      */
      if ((main.barometer_above_sll.equals(""))
          || (main.barometer_above_sll.trim().length() == 0)) {
        JOptionPane.showMessageDialog(
            null,
            "barometer height above SLL not available (Maintenance -> Station data)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Barometer_Vars(); // hier wordt ook "checks_ok" op false gezet
      } else if ((main.keel_sll.equals("")) || (main.keel_sll.trim().length() == 0)) {
        JOptionPane.showMessageDialog(
            null,
            "distance keel - SLL not available (Maintenance -> Station data)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Barometer_Vars(); // hier wordt ook "checks_ok" op false gezet
      }

      if (checks_ok == true) {

        // convert to double: distance barometer - keel
        try {
          double_barometer_above_sll = Double.parseDouble(main.barometer_above_sll.trim());
        } catch (NumberFormatException e) {
          /* ... */
          JOptionPane.showMessageDialog(
              null,
              "Internal error (converting distance barometer - SLL to double)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // hier wordt ook "checks_ok" op false gezet
        }

        // convert to double: distance keel - sll
        try {
          double_keel_sll = Double.parseDouble(main.keel_sll.trim());
        } catch (NumberFormatException e) {
          /* ... */
          JOptionPane.showMessageDialog(
              null,
              "Internal error (converting distance keel - SLL to double)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // in this function "checks_ok" set false
        }
      } // if (checks_ok == true)

      if (checks_ok == true) {
        double_hoogte_barometer =
            double_keel_sll + double_barometer_above_sll - double_deepest_draft;

        hoogte_corr =
            SeaLevelPressureReduction.heightCorrection(
                double_pressure_reading_corrected,
                double_hoogte_barometer,
                TemperatureModel.air_temp);

        // at Great lakes? (for extra height correction value)
        double great_lakes_corr = main_RS232_RS422.check_at_Great_Lakes();
        if (great_lakes_corr < 50.0) // so at Great Lakes
        {
          hoogte_corr += great_lakes_corr;
          String info =
              "at Great Lakes; extra air pressure height correction was applied ("
                  + great_lakes_corr
                  + " hPa)";
          JOptionPane.showMessageDialog(
              null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
        } else if ((great_lakes_corr > main_RS232_RS422.AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS - 1)
            && (great_lakes_corr
                < main_RS232_RS422.AT_GREAT_LAKES_OUTSIDE_MAIN_AREAS
                    + 1)) // so at Great Lakes but in transition between two lakes (locks)
        {
          Reset_All_Barometer_Vars(); // in this function "checks_ok" set false
          String info =
              "at Great Lakes but outside main areas (locks); click Cancel on input screen to continue";
          JOptionPane.showMessageDialog(
              null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
        }

        if (checks_ok == true) {
          double_pressure_msl_corrected = double_pressure_reading_corrected + hoogte_corr;

          // convert to string
          // pressure_msl = Double.toString(double_pressure_msl);

          // NB onderstaande werkt wel echter door nederlands windows systeem wordt het bv 1002,3
          // i.p.v. 1002.3
          // java.text.DecimalFormat df1 = new java.text.DecimalFormat("#####0.0");
          // pressure_msl = df1.format(double_pressure_msl);

          int hulp_int_pressure_msl = (int) Math.floor(double_pressure_msl_corrected * 10 + 0.5);
          double hulp_double_pressure_msl = (double) hulp_int_pressure_msl / 10;

          pressure_msl_corrected = Double.toString(hulp_double_pressure_msl); // convert to string
          manualReduction =
              new ManualReduction(
                  double_pressure_reading_corrected,
                  double_hoogte_barometer,
                  great_lakes_corr < 50.0 ? great_lakes_corr : 0.0,
                  pressure_msl_corrected);
        } // if (checks_ok == true)
      } // if (checks_ok == true)
    } // reading doesn't indicate MSL pressure
  }

  private void initSynopparameters() {

    // AWS
    //
    if ((local_AWS_connected)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_YES))) {
      // AWS connected
      String info =
          "air pressure meta data setting 'does the reading indicate MSL pressure' not correct\n";
      info += "Please change the appropriate settings\n";
      info +=
          "(see: Maintenance -> Station data -> air pressure meta data -> does the reading indicate MSL pressure: no)";

      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
    }

    // RS232/WiFi barometer connected
    //
    if ((local_barometer_connected)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_YES))) {
      // PTB220, PTB330, MintakaDuo, Mintaka Star USB or Mintaka Star WiFi connected
      String info =
          "TurboWin will always retrieve the pressure at instrument level (never at MSL) of the connected barometer\n";
      info += "Please change the appropriate settings\n";
      info +=
          "(see: Maintenance -> Station data -> air pressure meta data -> does the reading indicate MSL pressure: no)";

      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
    } else if ((main.RS232_connection_mode == 1 || main.RS232_connection_mode == 2)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))) {
      // PTB220 or PTB330 connected
      boolean local_tray_icon_clicked = false;
      RS232_vaisala.RS232_Read_Sensor_Data_PPPP_For_Obs(
          local_tray_icon_clicked); // this function will set 'pressure_reading' in jTextfield1

    } else if ((main.RS232_connection_mode == 4)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))) {
      // Mintaka Duo connected
      boolean local_tray_icon_clicked = false;
      RS232_mintaka.RS232_Mintaka_Duo_Read_Sensor_Data_PPPP_For_Obs(
          local_tray_icon_clicked); // this function will set 'pressure_reading'
    } else if ((main.RS232_connection_mode == 5)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))) {
      // Mintaka Star (USB) connected
      boolean local_tray_icon_clicked = false;
      boolean StarX = false;
      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
          local_tray_icon_clicked, StarX); // this function will set 'pressure_reading'
    } else if ((main.RS232_connection_mode == 6)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))) {
      // Mintaka Star (WiFi) connected
      boolean local_tray_icon_clicked = false;
      boolean StarX = false;
      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
          local_tray_icon_clicked, StarX); // this function will set 'pressure_reading'
    } else if ((main.RS232_connection_mode == 7)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))) {
      // Mintaka Star + StarX (USB) connected
      boolean local_tray_icon_clicked = false;
      boolean StarX = true;
      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
          local_tray_icon_clicked, StarX); // this function will set 'pressure_reading'
    } else if ((main.RS232_connection_mode == 8)
        && (main.pressure_reading_msl_yes_no.equals(main.PRESSURE_READING_MSL_NO))) {
      // Mintaka Star + StarX (WiFi) connected
      boolean local_tray_icon_clicked = false;
      boolean StarX = true;
      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_PPPP_For_Obs(
          local_tray_icon_clicked, StarX); // this function will set 'pressure_reading'
    }

    // pressure_reading, only if no barometer connected (because if barometer connected the then
    // RS232 applicable see above eg
    // main_RS232_RS422.RS232_Read_Sensor_Data_PPPP_For_Obs(local_tray_icon_clicked))
    if ((pressure_reading.compareTo("") != 0) && (local_barometer_connected == false)) {
      jTextField1.setText(pressure_reading);
    }

    if (deepest_draft.compareTo("") != 0) {
      jTextField2.setText(deepest_draft);
    }
  }

  private void Reset_All_Barometer_Vars() {
    // scope this module + myturbowin.java main module (all of type: static)
    clearManualReduction();
    PPPP_code = "";
    pressure_reading_corrected = "";
    pressure_msl_corrected = "";
    pressure_reading = "";
    pressure_msl = "";
    deepest_draft = "";
    // barometer_instrument_correction_new          = "";          // NB there is also:
    // main.barometer_instrument_correction

    // scope only this module (no static)
    double_pressure_reading = main.INVALID;
    // double_pressure_msl                          = main.INVALID;
    double_barometer_instrument_correction = main.INVALID;
    double_pressure_reading_corrected = main.INVALID;
    double_pressure_msl_corrected = main.INVALID;
    double_deepest_draft = main.INVALID;
    checks_ok = false;

    /* update progress fields on main screen */
    main.barometer_fields_update();
  }

  boolean accept() {
    // TODO add your handling code here:

    if (local_AWS_connected == false) // not AWS
    {

      // initialisation
      Reset_All_Barometer_Vars();

      // initialisation (will be set to false in function Reset_All_Barometer_Vars())
      checks_ok = true;

      // read inserted values
      pressure_reading = jTextField1.getText().trim();
      deepest_draft = jTextField2.getText().trim();

      /*
      /////////// checks on reading indication
      */
      if ((main.pressure_reading_msl_yes_no.compareTo(main.PRESSURE_READING_MSL_YES) != 0)
          && (main.pressure_reading_msl_yes_no.compareTo(main.PRESSURE_READING_MSL_NO) != 0)) {
        JOptionPane.showMessageDialog(
            null,
            "barometer reading (yes or no MSL) indication not available (Maintenance -> Station data)",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Barometer_Vars(); // "checks_ok" -> false
      }

      /*
      // if reading is present
      */
      if (pressure_reading.trim().length() == 0) {
        {
          JOptionPane.showMessageDialog(
              null,
              "pressure reading not inserted",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // "checks_ok" -> false
        } // if ( (main.pressure_reading_msl_yes_no.compareTo(main.PRESSURE_READING_MSL_NO) != 0)
        // etc.
      }

      /*
      // if reading is present also barometer instrument correction must be present
      */
      if ((pressure_reading.trim().length() > 0)
          && (main.barometer_instrument_correction.trim().length() == 0)) {
        {
          JOptionPane.showMessageDialog(
              null,
              "instrument correction not available (Maintenance -> Station data)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // "checks_ok" -> false
        } // if ( (main.pressure_reading_msl_yes_no.compareTo(main.PRESSURE_READING_MSL_NO) != 0)
        // etc.
      }

      /*
      // if reading do not indicate pressure at msl (and pressure reading is present) -> current deepest draft must be inserted!
      */
      if (checks_ok == true) {
        if ((main.pressure_reading_msl_yes_no.compareTo(main.PRESSURE_READING_MSL_NO) == 0)
            && (pressure_reading.trim().length() > 0)
            && (deepest_draft.trim().length() == 0)) {
          JOptionPane.showMessageDialog(
              null,
              "current deepest draft not inserted",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // "checks_ok" -> false
        } // if ( (main.pressure_reading_msl_yes_no.compareTo(main.PRESSURE_READING_MSL_NO) != 0)
        // etc.
      } // if (checks_ok == true)

      /*
      // convert collected (string) values to floats
      */
      if (checks_ok == true) {
        // convert collected (string) values to floats

        // air pressure reading
        try {
          double_pressure_reading = Double.parseDouble(pressure_reading.trim());
        } catch (NumberFormatException e) {
        }

        // barometer instrument correction
        try {
          double_barometer_instrument_correction =
              Double.parseDouble(main.barometer_instrument_correction.trim());
        } catch (NumberFormatException e) {
        }

        // deepest draft
        try {
          double_deepest_draft = Double.parseDouble(deepest_draft.trim());
        } catch (NumberFormatException e) {
        }
      } // if (checks_ok == true)

      /*
      //////////////// checks on limits
      */
      if (checks_ok == true) {
        // barometer reading limits
        if ((pressure_reading.trim().length() > 0)
            && (double_pressure_reading < 860.0 || double_pressure_reading > 1070.0)) {
          JOptionPane.showMessageDialog(
              null,
              "barometer reading must be in range 860.0 - 1070.0 or blank",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // hier wordt ook "checks_ok" op false gezet
        }

        // barometer instrument correction
        else if ((main.barometer_instrument_correction.trim().length() > 0)
            && (double_barometer_instrument_correction < -4.0
                || double_barometer_instrument_correction > 4.0)) {
          JOptionPane.showMessageDialog(
              null,
              "instrument correction must be in range -4.0 - 4.0 (Maintenance -> Station data)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // hier wordt ook "checks_ok" op false gezet
        }

        // deepest draft
        else if ((deepest_draft.trim().length() > 0)
            && (double_deepest_draft < 0.0 || double_deepest_draft > 50.0)) {
          JOptionPane.showMessageDialog(
              null,
              "deepest draft must be in range 0.0 - 50.0 or blank",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Barometer_Vars(); // hier wordt ook "checks_ok" op false gezet
        }
      } // if (checks_ok == true)

      /*
      //////////// apply instrument correction
      */
      double_pressure_reading_corrected =
          double_pressure_reading + double_barometer_instrument_correction;
      double_pressure_reading_corrected =
          Math.round(double_pressure_reading_corrected * 10) / 10.0d; // always 1 decimal!!
      pressure_reading_corrected = Double.toString(double_pressure_reading_corrected);

      /*
      //////////// compute pressure at MSL
      */
      if (checks_ok == true) {
        // compute pressure at MSL (Mean Sea level) required for observation
        MSL_Herleiding_Luchtdruk(); // in this function "checks_ok" could be set to false
      } // if (checks_ok == true)

      /*
      ///////////// determine meteorological code figures
      */
      if (checks_ok == true) {
        // if ( (double_pressure_msl != main.INVALID) && (double_barometer_instrument_correction !=
        // main.INVALID) )
        if (double_pressure_msl_corrected != main.INVALID) {
          PPPP_code = PressureCodeFormatter.format(double_pressure_msl_corrected);
        } // if (double_pressure_msl != INVALID)
      } // if (checks_ok == true)

    } // if (local_AWS_connected == false)
    else // AWS connected mode
    {
      checks_ok = true;
    }

    // BELOW MAYBE IN A NEXT STAGE
    // if (checks_ok && (main.APR == true))
    // {
    //   // so the barometer data will most probably be manually entered by the observer if APR
    // automated barometer data is not available
    //   main.obsolate_data_flag = false;
    //
    //   // for timer checking the barometer data is not obsolete (see Function:
    // RS232_And_WiFi_init_new_data_received_check_timer)
    //   main_RS232_RS422.last_new_data_received_TimeMillis = System.currentTimeMillis();
    //
    // } // if (checks_ok && (main.APR == true))

    /*
    ////// finish
    */
    if (checks_ok == true) {
      /* save ic (instrument correction) barometer */
      if (local_AWS_connected == false) // NOT AWS connected mode
      {
        // Save settings to the configuration files.
        main.schrijf_configuratie_regels();
      } // if (local_AWS_connected == false)

      /* update progress fields on main screen */
      main.barometer_fields_update();
    } // if (checks_ok == true)
    return checks_ok;
  } // GEN-LAST:event_OK_button_actionPerformed

  // scope this module + myturbowin.java main module (all of type: static)
  public static String PPPP_code = "";
  public static String pressure_reading_corrected = "";
  public static String pressure_msl_corrected = "";
  public static String pressure_reading = "";
  public static String pressure_msl = "";
  public static String deepest_draft = "";
  // public static String barometer_instrument_correction_new    = "";

  // scope only this module (no static)
  private double double_pressure_reading = main.INVALID;
  // private double double_pressure_msl                          = main.INVALID;
  private double double_barometer_instrument_correction = main.INVALID;
  private double double_pressure_reading_corrected = main.INVALID;
  private double double_pressure_msl_corrected =
      main.INVALID; // also used in Tdew computation [TemperatureModel.java]
  private double double_deepest_draft = main.INVALID;
  private boolean checks_ok = false;
  private boolean local_AWS_connected;
  private boolean local_barometer_connected;
}
