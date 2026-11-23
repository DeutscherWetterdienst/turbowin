package turbowin;

import static turbowin.main.INVALID;

import javax.swing.JOptionPane;

/** Existing shared wind state, validation and meteorological calculations. */
public final class WindModel {
  WindView.Input initialValues() {
    // wind_dir and wind_speed are the variables that will be inserted in the text fields
    //
    if ((main.wind_source.equals(main.ESTIMATED_TRUE))
        || main.wind_source.equals(main.MEASURED_TRUE)) {
      // true wind dir
      if (int_true_wind_dir == 0) {
        wind_dir = "calm";
      } else if (int_true_wind_dir == WIND_DIR_VARIABLE) {
        wind_dir = "variable";
      } else if (int_true_wind_dir == main.INVALID) // from version 4.1
      {
        wind_dir = "";
      } else {
        wind_dir = Integer.toString(int_true_wind_dir);
      }

      // true wind speed
      if (int_true_wind_speed == main.INVALID) // from version 4.1
      {
        wind_speed = "";
      } else {
        wind_speed = Integer.toString(int_true_wind_speed);
      }
    } // if ((main.wind_source.equals(main.ESTIMATED_TRUE)) ||
    // main.wind_source.equals(main.MEASURED_TRUE))
    else if ((main.wind_source.equals(main.MEASURED_OFF_BOW))) {
      // relative wind dir
      if (int_relative_wind_dir == 0) {
        wind_dir = "calm";
      } else if (int_relative_wind_dir == WIND_DIR_VARIABLE) {
        wind_dir = "variable";
      } else if (int_relative_wind_dir == INVALID) {
        wind_dir = "";
      } else {
        wind_dir = Integer.toString(int_relative_wind_dir);
      }

      // relative wind speed
      if ((WindModel.int_relative_wind_speed == INVALID)) {
        wind_speed = "";
      } else {
        wind_speed = Integer.toString(WindModel.int_relative_wind_speed);
      }
    } // else if ((main.wind_source.equals(main.MEASURED_OFF_BOW)))
    else {
      wind_dir = "";
      wind_speed = "";
    }

    return new WindView.Input(
        wind_dir,
        wind_speed,
        ship_ground_course,
        ship_ground_speed,
        ship_heading,
        main.max_height_deck_cargo,
        main.diff_sll_wl);
  }

  private void Reset_All_Wind_Vars() {
    // scope this module + myturbowin.java main module (all of type: static)
    // source                    = "";
    wind_dir = "";
    wind_speed = "";
    ship_ground_course = "";
    ship_ground_speed = "";
    ship_heading = "";
    dd_code = "";
    ff_code = "";
    fff00_code = "";
    iw_code = "";
    HDG_code = "";
    COG_code = "";
    SOG_code = "";
    SLL_code = "";
    sl_code = "";
    hh_code = "";
    RWD_code = "";
    RWS_code = "";
    int_true_wind_dir = main.INVALID;
    int_true_wind_speed = main.INVALID;
    int_difference_sll_wl = main.INVALID;
    int_relative_wind_dir = main.INVALID; // only for main screen if AWS connected
    int_relative_wind_speed = main.INVALID; // only for main screen if AWS connected

    // scope only this module (no static)
    // max_height_deck_cargo     = "";
    // difference_sll_wl         = "";
    int_wind_dir = main.INVALID;
    int_wind_speed = main.INVALID;
    int_ship_ground_course = main.INVALID;
    int_ship_heading = main.INVALID;
    int_max_height_deck_cargo = main.INVALID;
    // int_difference_sll_wl     = main.INVALID;
    // int_true_wind_dir         = main.INVALID;
    // int_true_wind_speed       = main.INVALID;
    float_ship_ground_speed = main.INVALID;
    checks_ok = false;

    /* wind velden op hoofdscherm overzicht updaten */
    main.wind_fields_update();
  }

  boolean accept(WindView.Input input) {
    // TODO add your handling code here:

    if ((main.true_wind_dir_from_AWS_present == false)
        && (main.true_wind_speed_from_AWS_present == false)) {

      // initialisation
      Reset_All_Wind_Vars();

      // initialisation
      checks_ok = true; // NB was set to false in Reset_All_Wind_Vars()

      // get text from inserted fields
      wind_dir = input.direction().trim();
      wind_speed = input.speed().trim();
      ship_ground_course = input.course().trim();
      ship_ground_speed = input.groundSpeed().trim();
      ship_heading = input.heading().trim();

      main.max_height_deck_cargo = input.cargo().trim();
      main.diff_sll_wl = input.waterline().trim();

      /*
      //////////////// checks on individual elements
      */

      // but first, convert collected (string) values to ints and floats

      // wind dir
      //
      if (wind_dir.trim().equals("calm") == true) // bijzonder geval
      {
        int_wind_dir = 0;
      } else if (wind_dir.trim().equals("variable") == true) // bijzonder geval
      {
        int_wind_dir = WIND_DIR_VARIABLE;
      } else {
        try {
          int_wind_dir = Integer.parseInt(wind_dir.trim());
        } catch (NumberFormatException e) {
          /* ... */
        }
      } // else

      // wind speed
      //
      try {
        int_wind_speed = Integer.parseInt(wind_speed.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // ship's ground course
      //
      try {
        int_ship_ground_course = Integer.parseInt(ship_ground_course.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // ship's ground speed
      //
      try {
        float_ship_ground_speed = Float.parseFloat(ship_ground_speed.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // ship's heading
      //
      try {
        int_ship_heading = Integer.parseInt(ship_heading.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // max. height deck cargo
      //
      try {
        int_max_height_deck_cargo = Integer.parseInt(main.max_height_deck_cargo.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // difference sll(summer load line) - water line
      //
      try {
        int_difference_sll_wl = Integer.parseInt(main.diff_sll_wl.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      /*
      // checks per element
      */

      // wind direction checks
      //
      if ((wind_dir.trim().compareTo("variable") != 0)
          && (wind_dir.trim().compareTo("calm") != 0)
          && (wind_dir.trim().length() > 0)
          && (int_wind_dir < 1 || int_wind_dir > 360 || int_wind_dir == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Wind direction must be in range 1 - 360 (no decimals), variable, calm or blank",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // wind speed checks
      //
      else if ((wind_speed.trim().length() > 0)
          && (int_wind_speed < 0 || int_wind_speed > 200 || int_wind_speed == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Wind speed must be in range 0 - 200 (no decimals) or blank",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // ship's ground course
      //
      else if ((ship_ground_course.trim().compareTo("stationary") != 0)
          && (ship_ground_course.trim().length() > 0)
          && (int_ship_ground_course < 1
              || int_ship_ground_course > 360
              || int_ship_ground_course == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Ship's ground course must be in range 1 - 360 (no decimals), stationary or blank",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // ship's ground speed
      //
      else if ((ship_ground_speed.trim().length() > 0)
          && (float_ship_ground_speed < 0.0
              || float_ship_ground_speed > 50.0
              || float_ship_ground_speed == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Ship's ground speed must be in range 0 - 50.0 or blank",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // ship's heading
      //
      else if ((ship_heading.trim().length() > 0)
          && (int_ship_heading < 1 || int_ship_heading > 360 || int_ship_heading == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Ship's heading must be in range 1 - 360 (no decimals) or blank",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // max. height deck cargo above sll
      //
      else if (
      /*(main.max_height_deck_cargo.trim().length() > 0) &&*/
      (int_max_height_deck_cargo < 0
          || int_max_height_deck_cargo > 100
          || int_max_height_deck_cargo == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Max. height deck cargo above summer load line must be in range 0 - 100 (no decimals)",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // difference between summer load line and water line
      //
      else if (
      /*(main.diff_sll_wl.trim().length() > 0) &&*/
      (int_difference_sll_wl < -10
          || int_difference_sll_wl > 50
          || int_difference_sll_wl == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Difference between summer load line and water line must be in range -10 - 50 (no decimals)",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      } else {
        checks_ok = true;
      }

      /*
      // consistency checks
      */

      //
      // if one of source, wind speed or wind dir. is present other two must be present too
      //

      if (checks_ok == true) {
        // wind dir. present than wind speed must also present
        if ((wind_dir.trim().length() > 0) && (wind_speed.trim().equals(""))) {
          JOptionPane.showMessageDialog(
              null, "wind speed not inserted", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        }

        // wind speed present than wind dir must also present
        else if ((wind_speed.trim().length() > 0) && (wind_dir.trim().equals(""))) {
          JOptionPane.showMessageDialog(
              null, "wind dir. not inserted", main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        }

        /* als wind speed en/of wind direction ingevuld is(zijn) dan moet 'source' ook bekend zijn */
        // wind speed or dir inserted -> source must be selected
        else if ((wind_dir.trim().length() > 0) && (main.wind_source.equals(""))) {
          JOptionPane.showMessageDialog(
              null,
              "source not selected (select: Maintenance -> Station data)",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        } else if ((wind_speed.trim().length() > 0) && (main.wind_source.equals(""))) {
          JOptionPane.showMessageDialog(
              null,
              "source not selected (select: Maintenance -> Station data)",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        }
        /* if wind richting = variable than wind speed may not be too high */
        else if ((main.wind_units.trim().indexOf(main.M_S) != -1)
            && (wind_dir.trim().equals("variable"))
            && (int_wind_speed >= 3)) // m/s
        {
          JOptionPane.showMessageDialog(
              null,
              "if wind dir. is variable, wind speed must be < 3 m/s",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        } else if ((main.wind_units.trim().indexOf(main.KNOTS) != -1)
            && (wind_dir.trim().equals("variable"))
            && (int_wind_speed >= 5)) // knots
        {
          JOptionPane.showMessageDialog(
              null,
              "if wind dir. is variable, wind speed must be < 5 knots",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        }

        /* direction = calm and speed not 0 */
        else if ((wind_dir.trim().equals("calm")) && (int_wind_speed != 0)) {
          JOptionPane.showMessageDialog(
              null,
              "if wind dir. is calm, wind speed must be 0",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        }

        /* speed = 0 and direction not calm */
        else if ((int_wind_speed == 0) && (wind_dir.trim().compareTo("calm") != 0)) {
          JOptionPane.showMessageDialog(
              null,
              "if wind speed is 0, wind dir. must be calm",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        } else if ((ship_ground_course.trim().equals("stationary"))
            && (float_ship_ground_speed > 0.01)) {
          JOptionPane.showMessageDialog(
              null,
              "if ship's ground course is stationary, ship's ground speed must be 0.0",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        } else if ((float_ship_ground_speed < 0.01)
            && ship_ground_course.trim().equals("stationary") == false) {
          JOptionPane.showMessageDialog(
              null,
              "if ship's ground speed is 0.0, ship's ground course must be stationary",
              main.APPLICATION_NAME,
              JOptionPane.WARNING_MESSAGE);
          Reset_All_Wind_Vars();
        }
      } // if (checks_ok = true)

      /*
      ///////////// determine meteorological code figures
      */
      if (checks_ok == true) {
        int_relative_wind_dir =
            main.INVALID; // only for showing on main screen. Can be overwritten in Function
        // Bereken_True_wind2()
        int_relative_wind_speed =
            main.INVALID; // only for showing on main screen. Can be overwritten in Function
        // Bereken_True_wind2()

        // estimated wind, intermediate results and iw code
        //
        if (main.wind_source.equals(main.ESTIMATED_TRUE) == true) {
          if (main.wind_units.trim().indexOf(main.M_S) != -1) {
            iw_code = "0"; // estimated in m/s
          } else // dus als wind units is knots of niet bekend is dan knots
          {
            iw_code = "3"; // estimated in knots
          }

          // NB wind_dir = calm is eerder al omgezet naar 0

          if (wind_dir.trim().equals("variable") == true) {
            int_true_wind_dir = WIND_DIR_VARIABLE; // consistent met berekende waarde
          } else {
            int_true_wind_dir = int_wind_dir;
          }

          int_true_wind_speed = int_wind_speed;
        } // if (source.equals(ESTIMATED_TRUE) == true)

        // measured apparent wind off the bow, intermediate results and iw code
        //
        if (main.wind_source.equals(main.MEASURED_OFF_BOW) == true) {
          if (main.wind_units.trim().indexOf(main.M_S) != -1) {
            iw_code = "1"; // measured in m/s
          } else // dus als wind units knots of als niet bekend dan knots
          {
            iw_code = "4"; // measured in knots
          }

          /* compute true wind */

          /* wind dir, wind speed, ground course and ground speed must be inserted */
          if ((wind_dir.trim().length() == 0)
              || (wind_speed.trim().length() == 0)
              || (ship_ground_course.trim().length() == 0)
              || (ship_ground_speed.trim().length() == 0)
              || ship_heading.trim().length() == 0) {
            JOptionPane.showMessageDialog(
                null,
                "if source is \"wind measured; apparent speed and apparent direction\" then wind dir, wind speed, ground speed,ground course and ship heading must be inserted",
                main.APPLICATION_NAME,
                JOptionPane.WARNING_MESSAGE);
            Reset_All_Wind_Vars();
          } // if
          else {
            Bereken_True_Wind2(); // int_true_wind_dir [graden]; int_true_wind_speed [knopen]
          } // else
        } // if (source.equals(MEASURED_OFF_BOW) == true)

        // measured true wind, intermediate results and iw code
        //
        if (main.wind_source.equals(main.MEASURED_TRUE) == true) {
          if (main.wind_units.trim().indexOf(main.M_S) != -1) {
            iw_code = "1"; // measured in m/s
          } else // dus als wind units is knots of niet bekend is dan knots
          {
            iw_code = "4"; // measured in knots
          }

          // NB wind_dir = calm is eerder al omgezet naar 0

          if (wind_dir.trim().equals("variable") == true) {
            int_true_wind_dir = WIND_DIR_VARIABLE; // consistent met berekende waarde
          } else {
            int_true_wind_dir = int_wind_dir;
          }

          int_true_wind_speed = int_wind_speed;
        } // if (source.equals(ESTIMATED_TRUE) == true)

        // wind direction code (NB int_true_wind_dir in graden)
        //
        if (int_true_wind_dir == WIND_DIR_VARIABLE) // variable
        {
          dd_code = "99";
        } // if (int_true_wind_dir == 9999)
        else // dus geen variable wind
        {
          int int_hulp_true_wind_dir;
          int int_hulp_dd_code;

          /* om te voorkomen dat 1 - 4 graden 00 in de obs wordt */
          if (int_true_wind_dir >= 1 && int_true_wind_dir <= 4) {
            int_hulp_true_wind_dir = 360;
          } else {
            int_hulp_true_wind_dir = int_true_wind_dir;
          }

          /* afronden op tientallen graden */
          int_hulp_dd_code = (int) Math.round((float) int_hulp_true_wind_dir / 10);

          /* omzetten naar code */
          if (int_hulp_dd_code <= 9) {
            dd_code = "0" + Integer.toString(int_hulp_dd_code);
          } else if ((int_hulp_dd_code >= 10) && (int_hulp_dd_code <= 36)) {
            dd_code = Integer.toString(int_hulp_dd_code);
          } else {
            dd_code = "//";
          }
        } // else (dus geen variable wind)

        // wind speed code (NB int_true_wind_speed in knots or m/s)
        //
        if (int_true_wind_speed <= 9) {
          ff_code = "0" + Integer.toString(int_true_wind_speed);
        } else if ((int_true_wind_speed >= 10) && (int_true_wind_speed <= 99)) {
          ff_code = Integer.toString(int_true_wind_speed);
        }
        // else if (int_true_wind_speed >= 100)
        else if ((int_true_wind_speed >= 100) && (int_true_wind_speed <= 200)) // from versio 4.1
        {
          ff_code = "99";
          fff00_code = Integer.toString(int_true_wind_speed);
        } else {
          ff_code = "//";
        }

        // Heading code (only for IMMT)
        //

        // NOTE according to IMMT-3/4 description "heading = 000 for No Movement" this is a
        // conceptual mistake A ship has ALWAYS a heading !!!
        if (int_ship_heading <= 9) {
          HDG_code = "00" + Integer.toString(int_ship_heading); // 3 char
        } else if (int_ship_heading >= 10 && int_ship_heading <= 99) {
          HDG_code = "0" + Integer.toString(int_ship_heading); // 3 char
        } else if (int_ship_heading >= 100 && int_ship_heading <= 360) {
          HDG_code = Integer.toString(int_ship_heading); // 3 char
        } else {
          HDG_code = "///"; // 3 char
        }

        // ship's ground course (only for IMMT)
        //
        if (ship_ground_course.trim().equals("stationary") == true) {
          COG_code = "000"; // 3 char
        } else if (int_ship_ground_course <= 9) {
          COG_code = "00" + Integer.toString(int_ship_ground_course); // 3 char
        } else if (int_ship_ground_course >= 10 && int_ship_ground_course <= 99) {
          COG_code = "0" + Integer.toString(int_ship_ground_course); // 3 char
        } else if (int_ship_ground_course >= 100 && int_ship_ground_course <= 360) {
          COG_code = Integer.toString(int_ship_ground_course); // 3 char
        } else {
          COG_code = "///"; // 3 char
        }

        // Ship's ground speed (only for IMMT)
        //
        int int_ship_ground_speed = Math.round(float_ship_ground_speed);
        if (int_ship_ground_speed <= 9) {
          SOG_code = "0" + Integer.toString(int_ship_ground_speed); // 2 char
        } else if (int_ship_ground_speed >= 10 && int_ship_ground_speed <= 99) {
          SOG_code = Integer.toString(int_ship_ground_speed); // 2 char
        } else {
          SOG_code = "//"; // 2 char
        }

        // max height deck cargo (only for IMMT)
        //
        if (int_max_height_deck_cargo <= 9) {
          SLL_code = "0" + Integer.toString(int_max_height_deck_cargo); // 2 char
        } else if (int_max_height_deck_cargo >= 10 && int_max_height_deck_cargo <= 99) {
          SLL_code = Integer.toString(int_max_height_deck_cargo); // 2 char
        } else {
          SLL_code = "//"; // 2 char
        }

        // departure of reference level (difference summer load line - water line (only for IMMT)
        //
        if (int_difference_sll_wl >= -10 && int_difference_sll_wl < 0) {
          sl_code = "1"; // negative                      // 1 char

          if (int_difference_sll_wl <= -10) {
            hh_code = Integer.toString(Math.abs(int_difference_sll_wl)); // 2 char
          } else // range -1 - -9
          {
            hh_code = "0" + Integer.toString(Math.abs(int_difference_sll_wl)); // 2 char
          }
        } // if (int_difference_sll_wl >= -10 && int_difference_sll_wl < 0)
        else if (int_difference_sll_wl >= 0 && int_difference_sll_wl < 50) {
          sl_code = "0"; // positive                        // 1 char

          if (int_difference_sll_wl >= 0 && int_difference_sll_wl <= 9) {
            hh_code = "0" + Integer.toString(int_difference_sll_wl); // 2 char
          } else // range 10 - 49
          {
            hh_code = Integer.toString(int_difference_sll_wl); // 2 char
          }
        } // else if (int_difference_sll_wl > 0 && int_difference_sll_wl < 50)
        else {
          sl_code = "/"; // 1 char
          hh_code = "//"; // 2 char
        } // else

        // Relative wind direction
        //
        if ((main.wind_source.equals(main.ESTIMATED_TRUE) == true)
            || (main.wind_source.equals(main.MEASURED_TRUE) == true)) {
          // esimated (true) wind or measured true wind -> no relative wind direction
          RWD_code = "///";
        } else // measured apparent wind
        {
          if (int_wind_dir == WIND_DIR_VARIABLE) // variable
          {
            RWD_code = "999"; // not in IMMT description but possible in MQCS (in IMMT no entry for
            // variable mmeasured wind)
          } else if (int_wind_dir <= 9) {
            RWD_code = "00" + Integer.toString(int_wind_dir); // 3 char
          } else if (int_wind_dir >= 10 && int_wind_dir <= 99) {
            RWD_code = "0" + Integer.toString(int_wind_dir); // 3 char
          } else if (int_wind_dir >= 100 && int_wind_dir <= 360) {
            RWD_code = Integer.toString(int_wind_dir); // 3 char
          } else {
            RWD_code = "///"; // 3 char
          }
        } // else (measued apparent wind)

        // Relative wind speed
        //
        if ((main.wind_source.equals(main.ESTIMATED_TRUE) == true)
            || (main.wind_source.equals(main.MEASURED_TRUE) == true)) {
          // esimated (true) wind or measured true wind -> no relative wind speed
          RWS_code = "///";
        } else // measured apparent wind
        {
          if (int_wind_speed <= 9) {
            RWS_code = "00" + Integer.toString(int_wind_speed); // 3 char
          } else if (int_wind_speed >= 10 && int_wind_speed <= 99) {
            RWS_code = "0" + Integer.toString(int_wind_speed); // 3 char
          } else if (int_wind_speed >= 100 && int_wind_speed <= 200) {
            RWS_code = Integer.toString(int_wind_speed); // 3 char
          } else {
            RWS_code = "///"; // 3 char
          }
        } // else (measued wind)

        // JOptionPane.showMessageDialog(null, int_true_wind_dir, "int_true_wind_dir",
        // JOptionPane.WARNING_MESSAGE);
        // JOptionPane.showMessageDialog(null, int_true_wind_speed, "int_true_wind_speed",
        // JOptionPane.WARNING_MESSAGE);

      } // if (checks_ok == true)

      if (checks_ok == true) {
        // komt tie hier ooit in ????
        if (main.RS232_connection_mode == 3
            || main.RS232_connection_mode == 9
            || main.RS232_connection_mode == 10
            || main.RS232_connection_mode == 11) // AWS connected mode
        {
          main.jTextField16.setForeground(main.input_color_from_observer); // true wind
          main.jTextField17.setForeground(main.input_color_from_observer); // relative wind

          // because now both, true wind (speed and dir) and relative wind (dir and speed) will be
          // overwritten
          main.true_wind_speed_from_AWS_present = false;
          main.true_wind_dir_from_AWS_present = false;

          main.relative_wind_speed_from_AWS_present = false;
          main.relative_wind_dir_from_AWS_present = false;
        }
      } // if (checks_ok == true)

    } // if ( (main.true_wind_dir_from_AWS_present == false) && etc.
    else // so true wind speed and true wind direction from AWS are present
    {
      main.max_height_deck_cargo = input.cargo().trim();
      main.diff_sll_wl = input.waterline().trim();

      // max. height deck cargo
      //
      try {
        int_max_height_deck_cargo = Integer.parseInt(main.max_height_deck_cargo.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // difference sll(summer load line) - water line
      //
      try {
        int_difference_sll_wl = Integer.parseInt(main.diff_sll_wl.trim());
      } catch (NumberFormatException e) {
        /* ... */
      }

      // max. height deck cargo above sll
      //
      if ((int_max_height_deck_cargo < 0
          || int_max_height_deck_cargo > 100
          || int_max_height_deck_cargo == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Max. height deck cargo above summer load line must be in range 0 - 100 (no decimals)",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      }

      // difference between summer load line and water line
      //
      else if ((int_difference_sll_wl < -10
          || int_difference_sll_wl > 50
          || int_difference_sll_wl == main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "Difference between summer load line and water line must be in range -10 - 50 (no decimals)",
            main.APPLICATION_NAME,
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Wind_Vars();
      } else {
        checks_ok = true;
      }
    } // else (so true wind speed and true wind direction from AWS are present)

    //
    // final processing
    //
    if (checks_ok == true) {
      /* max height deck cargo and diff sll-wl can be set in this wind input page but also on station data page (Maintenance)

      /* NB main.configuratie_regels[0] - main.configuratie_regels[14] occupied by station data */
      /* NB main.configuratie_regels[15] - main.configuratie_regels[16] occupied by email settings */
      /* etc. */
      /* for a complete view see function: fill_configuratie_array() [main.java] */

      main.configuratie_regels[13] = "";
      main.configuratie_regels[13] =
          main.MAX_HEIGHT_DECK_CARGO_TXT + main.max_height_deck_cargo.trim();

      main.configuratie_regels[14] = "";
      main.configuratie_regels[14] = main.DIFF_SLL_WL_TXT + main.diff_sll_wl.trim();

      // Save settings to the configuration files.
      main.schrijf_configuratie_regels();

      /* update wind fields on main screen */
      main.wind_fields_update();

      if (main.dashboard_form_APR_radar
          != null) // otherwise the first time (if APR dashboard was never opened before) an
      // exception)
      {
        // NB - this branch is activated if APR dashboard was opened before or is still open!
        //    - if APR Dashboard was never opened before then via initComponents1()
        // [DASHBOARD_view_APR_radar.java]!
        //
        //
        update_APR_dashboard();
      }

      ///////////////////////////////////////////////////////////////////////////////////////////////////////

    }
    return checks_ok;
  }

  private void update_APR_dashboard() {
    DASHBOARD_view_APR_radar.wind_input_APR_from_main_form_or_previous = true;

    DASHBOARD_view_APR_radar.buttonGroup1.clearSelection();
    DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem(""); // NB 0 or -1 doesn't work! whu not??

    // System.out.println("++++++++++++++++++ int_true_wind_speed 2 = "+ int_true_wind_speed);

    // NB Due to the function check_wind_consistency_APR() [DASHBOARD_view_APR_radar.java] wind dir
    // must be updaded before updating Bf class!
    if ((main.wind_source.equals(main.ESTIMATED_TRUE))
        || (main.wind_source.equals(main.MEASURED_TRUE))) {
      // System.out.println("++++++++++++++++++ int_true_wind_speed 3 = "+ int_true_wind_speed);

      if (int_true_wind_dir == main.INVALID) {
        DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem("");
      } else if (int_true_wind_dir == 0) {
        DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem("calm");
      } else if (int_true_wind_dir == WindModel.WIND_DIR_VARIABLE) {
        DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem("variable");
      } else {
        // NB see also initComponents1() [DASHBOARD_view_APR_radar.java]

        // DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem(Integer.toString(WindModel.int_true_wind_dir));
        int tmp_wind_dir = (int) (Math.round(WindModel.int_true_wind_dir / 10.0) * 10);
        DASHBOARD_view_APR_radar.jComboBox1.setSelectedItem(Integer.toString(tmp_wind_dir));
      }

      // System.out.println("++++++++++++++++++ int_true_wind_speed 4 = "+ int_true_wind_speed);
    }

    if ((main.wind_source.equals(main.ESTIMATED_TRUE))
        || (main.wind_source.equals(main.MEASURED_TRUE))) {
      int Bf_number = main.INVALID;
      if (main.wind_units.trim().indexOf(main.M_S) != -1) // m/s
      {
        Bf_number = DASHBOARD_view_APR_radar.convert_ms_to_bf(int_true_wind_speed);
      } else {
        // System.out.println("++++++++++++++++++ int_true_wind_speed 5 = "+ int_true_wind_speed);
        // int temp_wind_speed = int_true_wind_speed;
        // Bf_number = DASHBOARD_view_APR_radar.convert_knots_to_bf(temp_wind_speed);
        // System.out.println("++++++++++++++++++ int_true_wind_speed 6 = "+ int_true_wind_speed);

        Bf_number = DASHBOARD_view_APR_radar.convert_knots_to_bf(int_true_wind_speed);
      }

      if (Bf_number == 0) {
        DASHBOARD_view_APR_radar.jRadioButton1.setSelected(true);
      } else if (Bf_number == 1) {
        DASHBOARD_view_APR_radar.jRadioButton2.setSelected(true);
      } else if (Bf_number == 2) {
        DASHBOARD_view_APR_radar.jRadioButton3.setSelected(true);
      } else if (Bf_number == 3) {
        DASHBOARD_view_APR_radar.jRadioButton4.setSelected(true);
      } else if (Bf_number == 4) {
        DASHBOARD_view_APR_radar.jRadioButton5.setSelected(true);
      } else if (Bf_number == 5) {
        DASHBOARD_view_APR_radar.jRadioButton6.setSelected(true);
      } else if (Bf_number == 6) {
        DASHBOARD_view_APR_radar.jRadioButton7.setSelected(true);
      } else if (Bf_number == 7) {
        DASHBOARD_view_APR_radar.jRadioButton8.setSelected(true);
      } else if (Bf_number == 8) {
        DASHBOARD_view_APR_radar.jRadioButton9.setSelected(true);
      } else if (Bf_number == 9) {
        DASHBOARD_view_APR_radar.jRadioButton10.setSelected(true);
      } else if (Bf_number == 10) {
        DASHBOARD_view_APR_radar.jRadioButton11.setSelected(true);
      } else if (Bf_number == 11) {
        DASHBOARD_view_APR_radar.jRadioButton12.setSelected(true);
      } else if (Bf_number == 12) {
        DASHBOARD_view_APR_radar.jRadioButton13.setSelected(true);
      } else // if (Bf_number == main.INVALID)
      {
        DASHBOARD_view_APR_radar.jRadioButton14.setSelected(true);
      }
    } // if ( (main.wind_source.equals(main.ESTIMATED_TRUE)) ||
    // (main.wind_source.equals(main.MEASURED_TRUE)) )

    DASHBOARD_view_APR_radar.wind_input_APR_from_main_form_or_previous = false;
  }

  private void Bereken_True_Wind2() {
    /* int_ship_ground_course  : hele graden */
    /* float_ship_ground_speed : hele + tienden knopen */
    /* int_wind_dir            : ingevulde wind; hier: apparent wind richting (hele graden ook variable9 mogelijk) ALTIJD T.O.V. TRUE NORTH */
    /* int_wind_speed          : ingevulde wind; hier: apparent wind snelheid (hele knopen) */
    /* int_true_wind_dir       : true wind direction (hele graden) */
    /* int_true_wind_speed     : true wind speed  (hele knopen of hele m/s) */

    /* int_wind_dir en/of int_wind_speed == leeg veld is hier niet meer mogelijk */
    /* dan was al eerder een Error gegeven */

    //
    // Ao' = 270 - (ho + Ro + Po)
    //
    // Ao': apparent wind direction [mathemetical]
    // ho: heading vessel
    // Ro: zero reference (altijd 0 gaan er van uit voor schepen van VOS)
    // Po: platform-relative wind direction
    //
    //
    // Co' = 90 - Co
    //
    // Co': COG [mathematical]
    // Co: COG (course over ground)
    //
    //
    // Tu = Tu' = |A|cos(Ao') + |C|cos(Co')
    // Tv = Tv' = |A|sin(Ao') + |C|sin(Co')
    //
    // A: apparent wind speed
    // C: SOG (speed over ground)
    // Tu: eastward component true wind
    // Tv: northward component true wind
    //
    //
    // |T| = sqrt(Tu** + Tv**)
    // To = 270 - atan Tv/Tu
    //
    // T: true wind speed
    // To: true wind direction
    //
    //

    double ho = int_ship_heading;
    double Co = int_ship_ground_course;
    double C = float_ship_ground_speed; // dus in knots kan verderop omgezet worden naar m/s
    double A = int_wind_speed;
    double Po = int_wind_dir;
    double Ro = 0; // per definitie (aanname bij VOS)
    double Ao_mat; // apparent wind speed [mathemetical]
    double Co_mat; // ground course ship [mathemetical]
    double Tu; // eastward component true wind
    double Tv; // northward component true wind
    double T; // true wind speed
    double To; // tue wind direction
    int_relative_wind_dir = int_wind_dir; // only for showing on main screen
    int_relative_wind_speed = int_wind_speed; // only for showing on main screen

    // indien windspeed in m/s dan ook C (float_ship_ground_speed) omzetten naar m/s
    if (main.wind_units.trim().indexOf(main.M_S) != -1) {
      C = float_ship_ground_speed * 0.514444; // nu C in m/s
    }

    // variabele wind en stilligend schip
    //
    if ((wind_dir.trim().equals("variable") == true)
        && (ship_ground_course.trim().equals("stationary") == true)) {
      int_true_wind_dir = WIND_DIR_VARIABLE; // variable
      int_true_wind_speed = int_wind_speed;
    }

    // variable wind en geen stilligend schip
    //
    if ((wind_dir.trim().equals("variable") == true)
        && (ship_ground_course.trim().compareTo("stationary") != 0)) {
      int hulp = int_ship_ground_course + 180;
      if (hulp > 360) hulp -= 360;

      int_true_wind_dir = hulp;

      if (main.wind_units.trim().indexOf(main.M_S) != -1) {
        // dan ook float_ship_ground_speed naar m/s
        int_true_wind_speed =
            (int) Math.round(float_ship_ground_speed * 0.514444); // afronden kan > 100 zijn)
      } else {
        int_true_wind_speed = (int) Math.round(float_ship_ground_speed); // afronden kan > 100 zijn)
      }
    } // if ((wind_dir.trim().equals("variable") == true) etc.

    // geen variable wind
    //
    if (wind_dir.trim().compareTo("variable") != 0) {
      Ao_mat = 270 - (ho + Ro + Po);
      Co_mat = 90 - Co;

      Tu = A * Math.cos(Ao_mat * BOOGGRAAD) + C * Math.cos(Co_mat * BOOGGRAAD);
      Tv = A * Math.sin(Ao_mat * BOOGGRAAD) + C * Math.sin(Co_mat * BOOGGRAAD);

      T = Math.sqrt(Tu * Tu + Tv * Tv);

      if (Tu != 0.0) To = 270 - Math.atan2(Tv, Tu) / BOOGGRAAD;
      else To = 0;

      if (To > 360) To -= 360; // kan groter dan 360 graden zijn !
      if (To < 0) To += 360; // komt hij hier ooit ?

      int_true_wind_speed = (int) Math.round(T); // afronden ; kan > 100 zijn)
      int_true_wind_dir = (int) Math.round(To);

      // voor consistentie
      //
      if (int_true_wind_speed == 0) int_true_wind_dir = 0;

      if ((int_true_wind_speed != 0) && (int_true_wind_dir == 0)) int_true_wind_dir = 360;

      /* NB int_true_wind_dir = 0 - 4 (Noord) wordt in aanroepende programma op 360 gezet */

    } // if (wind_dir.trim().compareTo("variable") != 0)
  }

  // constants global
  public static final int WIND_DIR_VARIABLE = 9999;
  // public static final int INVALID              = 9999999;
  public static final double BOOGGRAAD = 0.0174532925; // voor Berekenwind

  // var's scope this module + myturbowin.java main module (all of type: static)
  public static String wind_dir = "";
  public static String wind_speed = "";
  public static String ship_ground_course = "";
  public static String ship_ground_speed = "";
  public static String ship_heading = "";
  public static String dd_code = "";
  public static String ff_code = "";
  public static String fff00_code =
      ""; // var name '00fff_code' not possible/accepted so used 'fff00_code' as var name
  public static String iw_code = "";
  public static String HDG_code = "";
  public static String COG_code = "";
  public static String SOG_code = "";
  public static String SLL_code = "";
  public static String sl_code = "";
  public static String hh_code = "";
  public static String RWD_code = "";
  public static String RWS_code = "";
  public static int int_true_wind_dir = main.INVALID;
  public static int int_true_wind_speed = main.INVALID;
  public static int int_difference_sll_wl = main.INVALID;
  public static int int_true_wind_gust_speed = main.INVALID; // only for dashboard
  public static int int_true_wind_gust_dir = main.INVALID; // only for dashboard
  public static int int_relative_wind_dir = main.INVALID; // only for main screen if AWS connected
  public static int int_relative_wind_speed = main.INVALID; // only for main screen if AWS connected
  public static double double_true_wind_speed = main.INVALID; // only for dashboard;
  public static double double_relative_wind_speed = main.INVALID; // only for dashboard;
  public static double double_true_wind_gust_speed = main.INVALID; // only for dashboard;

  // var's scope only this module (no static)
  private int int_wind_dir = main.INVALID;
  private int int_wind_speed = main.INVALID;
  private int int_ship_ground_course = main.INVALID;
  private int int_ship_heading = main.INVALID;
  private int int_max_height_deck_cargo = main.INVALID;
  // private int int_difference_sll_wl            = main.INVALID;
  private float float_ship_ground_speed = main.INVALID;
  private boolean checks_ok = false;
}
