package turbowin;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/** Temperature calculations and the existing observation state shared with sensor workflows. */
public final class mytemp {
  public static JTextField jTextField1;
  public static JTextField jTextField2;
  public static JTextField jTextField4;
  public static JRadioButton jRadioButton3;
  public static JRadioButton jRadioButton4;
  final JTextField jTextField3 = new JTextField();
  private final JRadioButton jRadioButton1 = new JRadioButton();
  private final JRadioButton jRadioButton2 = new JRadioButton();
  private final JRadioButton jRadioButton5 = new JRadioButton();
  private final JRadioButton jRadioButton6 = new JRadioButton();
  private final JRadioButton jRadioButton7 = new JRadioButton();
  private final JRadioButton jRadioButton8 = new JRadioButton();
  private final JRadioButton jRadioButton9 = new JRadioButton();
  private final JRadioButton jRadioButton10 = new JRadioButton();
  private final JRadioButton jRadioButton11 = new JRadioButton();
  private final JRadioButton jRadioButton12 = new JRadioButton();

  mytemp() {
    jTextField1 = new JTextField();
    jTextField2 = new JTextField();
    jTextField4 = new JTextField();
    jRadioButton3 = new JRadioButton();
    jRadioButton4 = new JRadioButton();
    ButtonGroup frozen = new ButtonGroup();
    frozen.add(jRadioButton3);
    frozen.add(jRadioButton4);
    local_thermometer_StarX_connected =
        main.RS232_connection_mode == 7 || main.RS232_connection_mode == 8;
    local_thermometer_HMP155_connected =
        !local_thermometer_StarX_connected && main.RS232_connection_mode_II == 1;
    initSynopparameters();
  }

  private void Compute_Dewpoint_From_RH(double num_RH, double temp) {
    double_dew_point =
        DewPointCalculator.calculate(num_RH, temp, mybarometer.pressure_msl_corrected);

    // System.out.println("temp = " + temp);
    // System.out.println("num_RH = " + num_RH);
    // System.out.println("double_dew_point = " + double_dew_point);
  }

  private double SVP(double temp) {
    // double f_p = 1.0016 + 0.00000315 * SP - 0.074 / SP;
    // JOptionPane.showMessageDialog(null, "f_p zonder haakjes = " + f_p, main.APPLICATION_NAME + "
    // error", JOptionPane.WARNING_MESSAGE);
    //
    // f_p = 1.0016 + (0.00000315 * SP) - (0.074 / SP);
    // JOptionPane.showMessageDialog(null, "f_p met haakjes = " + f_p, main.APPLICATION_NAME + "
    // error", JOptionPane.WARNING_MESSAGE);
    //
    // NB answer for both above: 1.0047187

    double ew = f_p * c1 * Math.exp(c2 * temp / (c3 + temp));

    return ew;
  }

  private double SVP_ijs(double temp) {
    double ei = f_p * c1_ijs * Math.exp(c2_ijs * temp / (c3_ijs + temp));

    return ei;
  }

  private void Bereken_Dauwpunt_en_RV() {
    double A_water;
    double A_ice;

    /* uk marine screens werken voor het berekenen van dew point niet met ijs ja dan nee aan natte bol      */
    /* als Twet < 0 dan moet bij Marine Screens altijd de boven ijs formule gebruikt worden,                */
    /* ongeacht ze al of niet bevroren natte bol hebben gemeld (zie: tabellen in Marine Observers Handbook) */

    /* translation: */
    /* UK Marine Screens do not use the info yes or no ice on wet bulb to calculate dew point      */
    /* If Twet < 0 then always above the ice formula must be used                                  */
    /* whether or not they have reported frozen wet bulb (see tables in Marine Observers Handbook) */

    if ((main.air_temp_exposure.equals(main.MARINE_SCREEN) == true) && (double_wet_bulb_temp < 0.0))
      wet_bulb_frozen = true; // artificial/trick; to become the right formula

    /* voor sling psychrometers wordt er wel van uitgegaan dat een Twet < 0 wel met ijs maar ook */
    /* zonder ijs aan de natte bol kan voorkomen; ingang is melding van de waarnemer             */
    /* al dan niet bevroren natte bol (zie: Handleiding voor het verrichten van meteorologische  */
    /* waarnemingen op zee)                                                                      */

    /* translation: */
    /* for sling psychrometers it is assumed that if Twet < 0 this is either with ice  */
    /* or no ice on the wet bulb, as has been reported by the observer                 */
    /* whether or not frozen wet bulb (see: Guide for the provision of meteorological  */
    /* observations at sea)                                                            */

    /* air pressure  */
    // NB if available (earlier inserted bt the observer) use the actual inserted MSL air pressure
    // otherwise the standard pressure (SP)

    // initialisation
    pressure = SP;
    f_p = PRESSURE_DEPENDENCY;

    if (mybarometer.pressure_msl_corrected.equals("") == false
        && mybarometer.pressure_msl_corrected != null) {
      hulp_double_pressure_msl_corrected = Double.parseDouble(mybarometer.pressure_msl_corrected);
      if (hulp_double_pressure_msl_corrected > 900.0
          && hulp_double_pressure_msl_corrected < 1100.0) {
        pressure = hulp_double_pressure_msl_corrected;
        f_p = 1.0016 + 0.00000315 * pressure - 0.074 / pressure;
      }
    }

    /* ABOVE WATER */
    if (wet_bulb_frozen == false) {
      // determine psychrometric coefficient (A)
      if (main.air_temp_exposure.equals(main.MARINE_SCREEN)
          == true) // num_Twet_total is hier dus altijd >= 0 !!
      A_water = A_SCREEN_WATER;
      else A_water = 0.000653 * (1 + 0.000944 * double_wet_bulb_temp);

      double_rv =
          (SVP(double_wet_bulb_temp)
                  - A_water * pressure * (double_air_temp - double_wet_bulb_temp))
              / SVP(double_air_temp);
      // rh not in in %, to achieve this * 100

      // relative humity must be between 1 - 100 %
      if (double_rv >= 0.01 && double_rv <= 1.0) {
        double term = Math.log(double_rv) + (c2 * double_air_temp / (c3 + double_air_temp));
        double_dew_point = c3 * term / (c2 - term);
      } else // so (num_rv < 0.01 or num_rv > 1.0)
      {
        double_dew_point = main.INVALID; // make invalid
        double_rv = main.INVALID; // make invalid
      }
    } // if (wet_bulb_frozen == false)

    /* ABOVE ICE */
    else if (wet_bulb_frozen = true) {
      // determine psychrometric coefficient (A)
      if (main.air_temp_exposure.equals(main.MARINE_SCREEN) == true) A_ice = A_SCREEN_ICE;
      else A_ice = A_SLING_ICE;

      double_rv =
          (SVP_ijs(double_wet_bulb_temp)
                  - A_ice * pressure * (double_air_temp - double_wet_bulb_temp))
              / SVP(double_air_temp);

      if (double_rv >= 0.01 && double_rv <= 1.0) {
        double term = Math.log(double_rv) + (c2 * double_air_temp / (c3 + double_air_temp));
        double_dew_point = c3 * term / (c2 - term);
      } else // so (num_rv < 0.01 of num_rv > 1.0)
      {
        double_dew_point = main.INVALID; // make invalid
        double_rv = main.INVALID; // make invalid
      }
    } // else (dus ijs aan de nattebol)
  }

  boolean accept() {
    // TODO add your handling code here:

    if ((main.air_temp_from_AWS_present == false)
        || (main.rh_from_AWS_present == false)
        || (main.SST_from_AWS_present == false)) {
      /* initialisation  */
      Reset_All_Temperatures_Vars(); // in this function: checks_ok = false

      /* initialisation  */
      checks_ok = true; // NB was set to false in Reset_All_Temperatures_Vars();

      /*
      ////// get text from inserted fields and retrieve other inserted/checked values
      */
      air_temp = jTextField1.getText().trim();
      wet_bulb_temp = jTextField2.getText().trim();
      sea_water_temp = jTextField3.getText().trim();
      RH = jTextField4.getText().trim();

      // NB So wet-bulb state if nothing checked/ticked assume wet-bulb not frozen
      if (jRadioButton4.isSelected() == true) {
        wet_bulb_frozen = true;
      } else {
        wet_bulb_frozen = false;
      }

      try {
        double_air_temp = Double.parseDouble(air_temp.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      try {
        double_wet_bulb_temp = Double.parseDouble(wet_bulb_temp.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      try {
        double_sea_water_temp = Double.parseDouble(sea_water_temp.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      try {
        double_rv = Double.parseDouble(RH.trim()) / 100.0; // double_rv range: 0.0 - 1.0
      } catch (NumberFormatException e) {
        /* ... */
      }

      /*
      //////////// stand-alone checks
      */

      // air-temp limits
      if ((air_temp.trim().length() > 0) && (double_air_temp < -50.0 || double_air_temp > 60.0)) {
        JOptionPane.showMessageDialog(
            null,
            "Air temperature must be in range -50.0 - 60.0 or blank",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Temperatures_Vars();
      }

      // wet-bulb limits
      else if ((wet_bulb_temp.trim().length() > 0)
          && (double_wet_bulb_temp < -50.0 || double_wet_bulb_temp > 50.0)) {
        JOptionPane.showMessageDialog(
            null,
            "Wet-bulb temperature must be in range -50.0 - 50.0 or blank",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Temperatures_Vars();
      }

      // RH limits
      else if ((RH.trim().length() > 0)
          && (double_rv < 0.0 || double_rv > 1.0)) // // double_rv range: 0.0 - 1.0
      {
        JOptionPane.showMessageDialog(
            null,
            "RH must be in range 0.0 - 100.0 or blank",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Temperatures_Vars();
      }

      // sea water temp limits
      else if ((sea_water_temp.trim().length() > 0)
          && (double_sea_water_temp < -50.0 || double_sea_water_temp > 50.0)) {
        JOptionPane.showMessageDialog(
            null,
            "Sea water temperature must be in range -2.0 - 50.0 or blank",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Temperatures_Vars();
      }

      /*
      //////////// consistency checks
      */
      if (checks_ok == true) {
        // air temp versus wet-bulb temp
        if ((air_temp.trim().length() > 0)
            && (wet_bulb_temp.trim().length() > 0)
            && (double_air_temp < double_wet_bulb_temp)) {
          JOptionPane.showMessageDialog(
              null,
              "Air temperature must be >= wet-bulb temperature",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Temperatures_Vars();
        }

        // exposure air temp
        else if ((air_temp.trim().length() > 0)
            && (jRadioButton1.isSelected() == false)
            && (jRadioButton2.isSelected() == false)) {
          JOptionPane.showMessageDialog(
              null,
              "Air temperature exposure not available (Maintenance -> Station data)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Temperatures_Vars();
        }

        // exposure sea temp
        else if ((sea_water_temp.trim().length() > 0)
            && (jRadioButton5.isSelected() == false)
            && (jRadioButton6.isSelected() == false)
            && (jRadioButton7.isSelected() == false)
            && (jRadioButton8.isSelected() == false)
            && (jRadioButton9.isSelected() == false)
            && (jRadioButton10.isSelected() == false)
            && (jRadioButton11.isSelected() == false)
            && (jRadioButton12.isSelected() == false)) {
          JOptionPane.showMessageDialog(
              null,
              "Sea water temperature exposure not available (Maintenance -> Station data)",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Temperatures_Vars();
        }

        // wet bulb value versus frozen wet bulb indication
        if ((wet_bulb_temp.trim().length() > 0)
            && (double_wet_bulb_temp > 0)
            && (wet_bulb_frozen == true)) {
          JOptionPane.showMessageDialog(
              null,
              "Combination wet-bulb temp > 0 and state wet-bulb frozen not possible",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Temperatures_Vars();
        }
      } // if (checks_ok == true)

      /*
      //////////// compute dew point
      */
      if (checks_ok == true) {
        if ((checks_ok == true)
            && (air_temp.trim().length() > 0)
            && (wet_bulb_temp.trim().length() > 0)
            && (double_air_temp >= double_wet_bulb_temp)) {
          Bereken_Dauwpunt_en_RV();
        } else if ((checks_ok == true)
            && (air_temp.trim().length() > 0)
            && (RH.trim().length() > 0)) {
          Compute_Dewpoint_From_RH(double_rv, double_air_temp);
          double_wet_bulb_temp =
              main.INVALID; // wet-bulb will not be computed (altough it would be possibel, like in
          // TurboWin, butin TurboWin ather approach)
        }
      } // if (checks_ok == true)

      /*
      ///////////// determine meteorological code figures
      */
      if (checks_ok == true) {
        // air temp
        //
        if (double_air_temp != main.INVALID) {
          // Sn of TTT
          if (double_air_temp < 0) sn_TTT_code = "1";
          else sn_TTT_code = "0";

          // TTT
          int num_TTT_code = (int) Math.floor(Math.abs(double_air_temp) * 10 + 0.5);
          TTT_code =
              Integer.toString(
                  num_TTT_code); // convert to string      // or (alternative): Hw_code =
          // String.valueOf(num_Hw_code);

          // NB TTT_code always 3 characters width e.g. 027 (accomplished via construction below)
          int len = 3;
          if (TTT_code.length() < len) // pad on left with zeros
          TTT_code = "0000000000".substring(0, len - TTT_code.length()) + TTT_code;

          // JOptionPane.showMessageDialog(null, TTT_code, main.APPLICATION_NAME + " TTT code",
          // JOptionPane.WARNING_MESSAGE);
        } // if (float_air_temp != INVALID)

        // wet-bulb temp
        //
        if (double_wet_bulb_temp != main.INVALID) {
          // sn of wet-bulb (TbTbTb)
          if (double_wet_bulb_temp < 0.0) {
            if (wet_bulb_frozen == true) {
              sn_TbTbTb_code = "2"; // measured; iced wet bulb
            } else {
              sn_TbTbTb_code = "1"; // measured; negative
            }
          } // if (double_wet_bulb_temp < 0.0)
          else {
            sn_TbTbTb_code = "0"; // measured; positive or zero
          } // else

          // TbTbTb
          int num_TbTbTb_code = (int) Math.floor(Math.abs(double_wet_bulb_temp) * 10 + 0.5);
          TbTbTb_code =
              Integer.toString(
                  num_TbTbTb_code); // convert to string      // or (alternative): Hw_code =
          // String.valueOf(num_Hw_code);

          // NB TbTbTb_code always 3 characters width e.g. 127 (accomplish via construction below)
          int len = 3;
          if (TbTbTb_code.length() < len) // pad on left with zeros
          TbTbTb_code = "0000000000".substring(0, len - TbTbTb_code.length()) + TbTbTb_code;
        } // if (float_wet_bulb != INVALID)

        // sea water temp
        //
        if (double_sea_water_temp != main.INVALID) {
          // ss of TsTsTs
          if (double_sea_water_temp < 0.0) {
            if (main.sst_exposure.equals(main.INTAKE) == true) {
              ss_TsTsTs_code = "1"; // for obs and indirect also for immt (Sn calculation)
              immt_sst_indicator = "1"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.BUCKET) == true) {
              ss_TsTsTs_code = "3"; // for obs and indirect also for immt
              immt_sst_indicator = "0"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.HULL_CONTACT_SENSOR) == true) {
              ss_TsTsTs_code = "5"; // for obs and indirect also for immt
              immt_sst_indicator = "3"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.TRAILING_THERMISTOR) == true) {
              ss_TsTsTs_code = "7"; // trailing thermistor not for obs but for IMMT
              immt_sst_indicator = "2"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.THROUGH_HULL_SENSOR) == true) {
              ss_TsTsTs_code = "7"; // through hull sensor not for obs but for IMMT
              immt_sst_indicator = "4"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.RADIATION_THERMOMETER) == true) {
              ss_TsTsTs_code = "7"; // radiation thermometer not for obs but for IMMT
              immt_sst_indicator = "5"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.BAIT_TANKS_THERMOMETER) == true) {
              ss_TsTsTs_code = "7"; // bait tanks thermometer not for obs but for IMMT
              immt_sst_indicator = "6"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.OTHER) == true) {
              ss_TsTsTs_code = "7"; // for obs and indirect also for immt
              immt_sst_indicator = "7"; // only for immt log not for operational obs
            }
          } // if (double_sea_water_temp < 0.0
          else // sst >= 0
          {
            if (main.sst_exposure.equals(main.INTAKE) == true) {
              ss_TsTsTs_code = "0";
              immt_sst_indicator = "1"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.BUCKET) == true) {
              ss_TsTsTs_code = "2";
              immt_sst_indicator = "0"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.HULL_CONTACT_SENSOR) == true) {
              ss_TsTsTs_code = "4";
              immt_sst_indicator = "3"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.TRAILING_THERMISTOR) == true) {
              ss_TsTsTs_code = "6";
              immt_sst_indicator = "2"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.THROUGH_HULL_SENSOR) == true) {
              ss_TsTsTs_code = "6";
              immt_sst_indicator = "4"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.RADIATION_THERMOMETER) == true) {
              ss_TsTsTs_code = "6";
              immt_sst_indicator = "5"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.BAIT_TANKS_THERMOMETER) == true) {
              ss_TsTsTs_code = "6";
              immt_sst_indicator = "6"; // only for immt log not for operational obs
            } else if (main.sst_exposure.equals(main.OTHER) == true) {

              ss_TsTsTs_code = "6";
              immt_sst_indicator = "7"; // only for immt log not for operational obs
            }
          } // else (sst >= 0)

          // TsTsTs
          int num_TsTsTs_code = (int) Math.floor(Math.abs(double_sea_water_temp) * 10 + 0.5);
          TsTsTs_code =
              Integer.toString(
                  num_TsTsTs_code); // convert to string      // or (alternative): Hw_code =
          // String.valueOf(num_Hw_code);

          // NB TsTsTs_code always 3 characters width e.g. 127 (accomplish via construction below)
          int len = 3;
          if (TsTsTs_code.length() < len) // pad on left with zeros
          TsTsTs_code = "0000000000".substring(0, len - TsTsTs_code.length()) + TsTsTs_code;
        } // if (float_sea_water != INVALID)

        // dew point
        //
        if (double_dew_point != main.INVALID) {
          // sn van dew point (TdTdTd)
          if (double_dew_point < 0.0) sn_TdTdTd_code = "1";
          else sn_TdTdTd_code = "0";

          // TdTdTd
          int num_TdTdTd_code = (int) Math.floor(Math.abs(double_dew_point) * 10 + 0.5);
          TdTdTd_code =
              Integer.toString(
                  num_TdTdTd_code); // convert to string      // or (alternative): Hw_code =
          // String.valueOf(num_Hw_code);

          // NB TdTdTd_code always 3 characters width e.g. 127 (accomplish via construction below)
          int len = 3;
          if (TdTdTd_code.length() < len) // pad on left with zeros
          TdTdTd_code = "0000000000".substring(0, len - TdTdTd_code.length()) + TdTdTd_code;
        } // if (double_dew_point != INVALID)
      } // if (checks_ok == true)
    } // if ( (main.air_temp_from_AWS_present == false) || etc.
    else // air temp and rh and sst present in AWS measured data
    {
      checks_ok = true;
    }

    /*
    ////////// finish
    */
    if (mybarometer.refreshManualPressureReduction()) {
      main.barometer_fields_update();
    }
    if (checks_ok == true) {
      /* update temperature fields on main screen */
      main.temperatures_fields_update();
    }
    return checks_ok;
  }

  private void initSynopparameters() {
    if (local_thermometer_StarX_connected) {
      RS232_mintaka
          .RS232_Mintaka_StarX_Read_Sensor_Data_Air_Temp_et_al_For_Obs(); // field values will be
      // set in this function
    } else if (local_thermometer_HMP155_connected) {
      RS232_vaisala
          .RS232_Vaisala_HMP155_Read_Sensor_Data_Air_Temp_et_al_For_Obs(); // field values will be
      // set in this function
    } else {
      if (air_temp.compareTo("") != 0) jTextField1.setText(air_temp);

      if (wet_bulb_temp.compareTo("") != 0) jTextField2.setText(wet_bulb_temp);

      if (RH.compareTo("") != 0) {
        jTextField4.setText(RH);
      }

      // NB default is wet-bulb not frozen (dus wanneer niets bekend is dan standaard wet-bulb not
      // frozen button checken)
      if (wet_bulb_frozen == true) jRadioButton4.setSelected(true); // wet-bulb frozen radio button
      else jRadioButton3.setSelected(true); // wet-bulb not frozen radio button
    } // else

    // exposure settings
    //
    if (main.air_temp_exposure.equals(main.SLING_PSYCHROMETER)) jRadioButton1.setSelected(true);
    else if (main.air_temp_exposure.equals(main.MARINE_SCREEN)) jRadioButton2.setSelected(true);

    // SST
    //
    if (sea_water_temp.compareTo("") != 0) jTextField3.setText(sea_water_temp);

    if (main.sst_exposure.equals(main.INTAKE)) jRadioButton5.setSelected(true);
    else if (main.sst_exposure.equals(main.BUCKET)) jRadioButton6.setSelected(true);
    else if (main.sst_exposure.equals(main.HULL_CONTACT_SENSOR)) jRadioButton7.setSelected(true);
    else if (main.sst_exposure.equals(main.OTHER)) jRadioButton8.setSelected(true);
    else if (main.sst_exposure.equals(main.TRAILING_THERMISTOR)) jRadioButton9.setSelected(true);
    else if (main.sst_exposure.equals(main.THROUGH_HULL_SENSOR)) jRadioButton10.setSelected(true);
    else if (main.sst_exposure.equals(main.RADIATION_THERMOMETER)) jRadioButton11.setSelected(true);
    else if (main.sst_exposure.equals(main.BAIT_TANKS_THERMOMETER))
      jRadioButton12.setSelected(true);
  }

  private void Reset_All_Temperatures_Vars() {
    // scope this module + myturbowin.java main module (all of type: static)
    //
    air_temp = ""; // if not ="" -> null pointer exception if update button clicked on main page
    wet_bulb_temp = "";
    sea_water_temp = "";
    RH = "";

    sn_TTT_code = "";
    TTT_code = "";
    sn_TbTbTb_code = "";
    TbTbTb_code = "";
    ss_TsTsTs_code = "";
    TsTsTs_code = "";
    sn_TdTdTd_code = "";
    TdTdTd_code = "";
    immt_sst_indicator = "";
    double_dew_point = main.INVALID;
    double_rv = main.INVALID;
    wet_bulb_frozen = false;

    // scope only this module (no static)
    //
    double_air_temp = main.INVALID;
    double_wet_bulb_temp = main.INVALID;
    double_sea_water_temp = main.INVALID;
    checks_ok = false;

    /* update temperature fields on main screen */
    main.temperatures_fields_update();
  }

  private final double c1 = 6.112; // voor vochtigheid
  private final double c2 = 17.62; // voor vochtigheid
  private final double c3 = 243.12; // voor vochtigheid
  private final double c1_ijs = 6.112; // voor vochtigheid
  private final double c2_ijs = 22.46; // voor vochtigheid
  private final double c3_ijs = 272.62; // voor vochtigheid
  private final double SP = 1013.25; // voor vochtigheid
  private final double A_SLING_ICE = 0.000575; // voor vochtigheid
  private final double A_SCREEN_ICE = 0.000711; // (0.720 / 1013.25) voor vochtigheid
  private final double A_SCREEN_WATER = 0.000789; // (0.799 / 1013.25) voor vochtigheid
  private final double PRESSURE_DEPENDENCY =
      1.0047187; // see "recommended algorithms for the computation of marine meteorological
  // variables" (JCOMM TR No. 63)

  // scope this module + myturbowin.java main module (all of type: static)
  public static String air_temp =
      ""; // if not ="" -> nullpointerexception if update button clicked on main page
  public static String wet_bulb_temp = "";
  public static String RH = ""; // range 0.0 - 100.0 %
  public static String sea_water_temp = "";
  public static String sn_TTT_code = "";
  public static String TTT_code = "";
  public static String sn_TbTbTb_code = ""; // Tb = Twet
  public static String TbTbTb_code = "";
  public static String ss_TsTsTs_code = ""; // Ts = Tsea
  public static String TsTsTs_code = "";
  public static String sn_TdTdTd_code = "";
  public static String TdTdTd_code = "";
  public static String immt_sst_indicator = "";
  public static double double_dew_point = main.INVALID;
  public static double double_rv = main.INVALID; // range 0.0 - 1.0 (or invalid)
  public static boolean wet_bulb_frozen = false;

  // scope only this module (no static)
  private double pressure;
  private double f_p;
  private double hulp_double_pressure_msl_corrected = main.INVALID;
  private double double_air_temp = main.INVALID;
  private double double_wet_bulb_temp = main.INVALID;
  private double double_sea_water_temp = main.INVALID;
  private boolean checks_ok = false;
  private boolean local_thermometer_StarX_connected;
  private boolean local_thermometer_HMP155_connected;
}
