package turbowin;

import static turbowin.main.*;

/** Updates true and relative wind fields on the main observation screen. */
final class WindFieldsUpdater {

  private WindFieldsUpdater() {}

  static void update() {
    String field17_part1 = ""; // true wind dir
    String field17_part2 = ""; // true wind speed
    String field16_part1 = ""; // apparent wind dir
    String field16_part2 = ""; // apparent wind speed

    //////// true wind direction + true wind speed (main screen)
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.true_wind_dir_from_AWS_present == true
          || main.true_wind_speed_from_AWS_present == true) {
        if (displayed_aws_data_obsolate) {
          jTextField17.setForeground(obsolate_color_data_from_aws); // gray
        } else {
          jTextField17.setForeground(main.input_color_from_aws);
        }
      } else // so: true_wind_dir_from_AWS_present = false AND true_wind_speed_from_AWS_present =
      // false
      {
        if ((jTextField17.getForeground().getRGB() == main.input_color_from_aws.getRGB())
            || (jTextField17.getForeground().getRGB()
                == main.obsolate_color_data_from_aws.getRGB())) {
          // last value was measured by AWS (color was red/gray) but now no new true wind dir and no
          // new true wind speed in last received aws string -> clear text field
          jTextField17.setText("");
          field17_part1 = "";
          field17_part2 = "";
          mywind.int_true_wind_dir = INVALID;
          mywind.int_true_wind_speed = INVALID;
        }
        jTextField17.setForeground(main.input_color_from_observer);
      }
    } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode
    // == 10 || RS232_connection_mode == 11)
    else {
      jTextField17.setForeground(main.input_color_from_observer);
    }

    // true wind dir

    if (mywind.int_true_wind_dir == mywind.WIND_DIR_VARIABLE) {
      field17_part1 = "var";
    } else if ((mywind.int_true_wind_dir != INVALID)
        && (mywind.int_true_wind_dir
            != mywind.WIND_DIR_VARIABLE) /*&& (main.true_wind_dir_from_AWS_present == true)*/) {
      field17_part1 = Integer.toString(mywind.int_true_wind_dir);
    } else {
      field17_part1 = "-";
    }

    // true wind speed
    if (mywind.int_true_wind_speed
        != INVALID /*&& (main.true_wind_speed_from_AWS_present == true)*/) {
      field17_part2 = Integer.toString(mywind.int_true_wind_speed);
    } else {
      field17_part2 = "-";
    }

    // true wind dir + true wind speeed
    if (mywind.int_true_wind_dir != INVALID || mywind.int_true_wind_speed != INVALID) {
      if (main.wind_units.trim().indexOf(main.M_S)
          != -1) // source wind units (as originally measured), e.g. always m/s in case of AWS
      {
        jTextField17.setText(field17_part1 + " \u00B0" + " / " + field17_part2 + " m/s");
      } else // thus if wind speed units knots or wind speed units unknown
      {
        jTextField17.setText(field17_part1 + " \u00B0" + " / " + field17_part2 + " kts");
      }
    } else {
      jTextField17.setText("");
    }

    //////// relative wind direction + relative wind speed (main screen)
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.relative_wind_dir_from_AWS_present == true
          || main.relative_wind_speed_from_AWS_present == true) {
        if (displayed_aws_data_obsolate) {
          jTextField16.setForeground(obsolate_color_data_from_aws); // gray
        } else {
          jTextField16.setForeground(main.input_color_from_aws);
        }
      } else // so: relative_wind_dir_from_AWS_present = false AND
      // relative_wind_speed_from_AWS_present = false
      {
        if ((jTextField16.getForeground().getRGB() == main.input_color_from_aws.getRGB())
            || (jTextField16.getForeground().getRGB()
                == main.obsolate_color_data_from_aws.getRGB())) {
          // last value was measured by AWS (color was red/gray) but now no new relative wind dir
          // and no new relative wind speed in last received aws string -> clear text field
          jTextField16.setText("");
          field16_part1 = "";
          field16_part2 = "";
          mywind.int_relative_wind_dir = INVALID;
          mywind.int_relative_wind_speed = INVALID;
        }
        jTextField16.setForeground(main.input_color_from_observer);
      }
    } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode
    // == 10 || RS232_connection_mode == 11)
    else {
      jTextField16.setForeground(main.input_color_from_observer);
    }

    // relative wind dir
    if (mywind.int_relative_wind_dir == mywind.WIND_DIR_VARIABLE) {
      field16_part1 = "var";
    } else if ((mywind.int_relative_wind_dir != INVALID)
        && (mywind.int_relative_wind_dir
            != mywind.WIND_DIR_VARIABLE) /*&& (main.relative_wind_dir_from_AWS_present == true)*/) {
      field16_part1 = Integer.toString(mywind.int_relative_wind_dir);
    } else {
      field16_part1 = "-";
    }

    // relative wind speed
    if ((mywind.int_relative_wind_speed
        != INVALID) /*&& (main.relative_wind_speed_from_AWS_present == true)*/) {
      field16_part2 = Integer.toString(mywind.int_relative_wind_speed);
    } else {
      field16_part2 = "-";
    }

    // relative wind dir + relative wind speeed
    if (mywind.int_relative_wind_dir != INVALID || mywind.int_relative_wind_speed != INVALID) {
      if (main.wind_units.trim().indexOf(main.M_S)
          != -1) // source wind units (as originally measured), e.g. always m/s in case of AWS with
      // wind sensor
      {
        jTextField16.setText(field16_part1 + " \u00B0" + " / " + field16_part2 + " m/s");
      } else // thus if wind speed units knots or wind speed units unknown
      {
        jTextField16.setText(field16_part1 + " \u00B0" + " / " + field16_part2 + " kts");
      }
    } else {
      jTextField16.setText("");
    }

    // ship ground cource  (not on main screen, only on wind input form)
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.COG_from_AWS_present == false) {
        mywind.ship_ground_course = "";
      }
    }

    // ship ground speed  (not on main screen, only on wind input form)
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.SOG_from_AWS_present == false) {
        mywind.ship_ground_speed = "";
      }
    }

    // true heading  (not on main screen, only on wind input form)
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.true_heading_from_AWS_present == false) {
        mywind.ship_heading = "";
      }
    }

    /* update of the coded obs representation (bottom line main screen) */
    coded_obs_update();
  }
}
