package turbowin;

import static turbowin.main.*;

/** Updates the temperature-related fields on the main observation screen. */
final class TemperatureFieldsUpdater {

  private TemperatureFieldsUpdater() {}

  static void update() {
    ////// air temp
    //
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.air_temp_from_AWS_present
          == true) // NB was set in function: RS422_Read_AWS_Sensor_Data_For_Display() [file:
      // main_RS232_RS424.java]
      {
        if (displayed_aws_data_obsolate) {
          jTextField36.setForeground(obsolate_color_data_from_aws); // gray
        } else {
          jTextField36.setForeground(main.input_color_from_aws);
        }
      } else {
        // System.out.println("+++ jTextField36.getForeground() = " + jTextField36.getForeground());
        // System.out.println("+++ main.input_color_from_aws = " + main.input_color_from_aws);
        if ((jTextField36.getForeground().getRGB() == main.input_color_from_aws.getRGB())
            || (jTextField36.getForeground().getRGB()
                == main.obsolate_color_data_from_aws.getRGB())) {
          // last value was measured by AWS (color was red/gray) but now no new temp in last
          // received aws string -> clear text field
          jTextField36.setText("");
          // System.out.println("+++ kleuren zijn het zelde");
          mytemp.air_temp = "";
        }

        jTextField36.setForeground(main.input_color_from_observer);
      } // else
    } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode
    // == 10  || RS232_connection_mode == 11)
    else // not AWS connected
    {
      if (APR == true) {
        if ((main.obsolate_data_flag_II == true)
            || ((main.obsolate_data_flag == true)
                && (RS232_connection_mode == 7
                    || RS232_connection_mode == 8))) // NB 7/8 : Mintaka StarX (temp) linked
        {
          jTextField36.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField36.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // no APR
      {
        jTextField36.setForeground(main.input_color_from_observer);
      } // else (no APR)
    }

    if ((mytemp.air_temp.compareTo("") != 0) && (mytemp.air_temp != null)) {
      // Legacy display contract: integer input such as 25 is shown as 25.0 degrees C.
      int pos = mytemp.air_temp.indexOf(".");
      if (pos == -1) // dus geen "." in de air temp string
      {
        jTextField36.setText(mytemp.air_temp + ".0" + " \u00B0" + "C");
      } else {
        jTextField36.setText(mytemp.air_temp + " \u00B0" + "C");
      }
    } else {
      jTextField36.setText("");
    }

    /////// wet bulb temp
    //
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (rh_from_AWS_present == true) {
        if (displayed_aws_data_obsolate) {
          // NB to show "NA" (see below) in color gray
          jTextField37.setForeground(obsolate_color_data_from_aws); // gray
        } else {
          // NB to show "NA" (see below) in color red
          jTextField37.setForeground(main.input_color_from_aws);
        }
      } else {
        jTextField37.setForeground(main.input_color_from_observer);
      }
    } else // not AWS connected
    {
      if (APR == true) {
        // if (main.obsolate_data_flag_II == true)
        if ((main.obsolate_data_flag_II == true)
            || ((main.obsolate_data_flag == true)
                && (RS232_connection_mode == 7
                    || RS232_connection_mode == 8))) // NB 7/8 : Mintaka StarX (temp) linked
        {
          jTextField37.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField37.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // no APR
      {
        jTextField37.setForeground(main.input_color_from_observer);
      } // else (no APR)
    }

    if (rh_from_AWS_present == true) {
      jTextField37.setText("NA"); // Not Applicable
      mytemp.wet_bulb_temp = "";
    } else {
      if ((mytemp.wet_bulb_temp.compareTo("") != 0) && (mytemp.wet_bulb_temp != null)) {
        // Legacy display contract: integer input such as 25 is shown as 25.0 degrees C.
        int pos = mytemp.wet_bulb_temp.indexOf(".");
        if (pos == -1) // dus geen "." in de air temp string
        {
          jTextField37.setText(mytemp.wet_bulb_temp + ".0" + " \u00B0" + "C");
        } else {
          jTextField37.setText(mytemp.wet_bulb_temp + " \u00B0" + "C");
        }
      } else {
        jTextField37.setText("");
      }
    }

    /////// dew point or relative humidity
    //
    // AWS mode displays measured relative humidity because no dew point is available; non-AWS mode
    // displays the calculated dew point instead.
    //
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      // relative humidity (only if in AWS connected mode!)
      //
      if (rh_from_AWS_present == true) {
        if (displayed_aws_data_obsolate) {
          jTextField38.setForeground(obsolate_color_data_from_aws); // gray
        } else {
          jTextField38.setForeground(main.input_color_from_aws);
        }
      } else {
        if ((jTextField38.getForeground().getRGB() == main.input_color_from_aws.getRGB())
            || (jTextField38.getForeground().getRGB()
                == main.obsolate_color_data_from_aws.getRGB())) {
          // last value was measured by AWS (color was red/gray ) but now no rh in last received aws
          // string -> clear text field
          jTextField38.setText("");
          mytemp.double_rv = INVALID;
        }
        jTextField38.setForeground(main.input_color_from_observer);
      }

      // double_rv is stored as a fraction (0.0-1.0); convert it to percent and round the
      // displayed value to one decimal place.
      double hulp_double_rv =
          Math.round(mytemp.double_rv * 10 * 100)
              / 10.0; // nb Math.round(xxx) - > geeft long terug  // NB * 100 for getting %
      if ((mytemp.double_rv != INVALID))
        jTextField38.setText(Double.toString(hulp_double_rv) + " %");
      else jTextField38.setText("");

    } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode
    // == 10 || RS232_connection_mode == 11)
    else // not AWS connected
    {
      // dewpoint (only if in not AWS connected mode!)
      //
      if (APR == true) {
        // if (main.obsolate_data_flag_II == true)
        if ((main.obsolate_data_flag_II == true)
            || ((main.obsolate_data_flag == true)
                && (RS232_connection_mode == 7
                    || RS232_connection_mode == 8))) // NB 7/8 : Mintaka StarX (temp) linked
        {
          jTextField38.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField38.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // no APR
      {
        jTextField38.setForeground(main.input_color_from_observer);
      } // else (no APR)

      if ((mytemp.double_dew_point != INVALID)) {
        // Round the displayed dew point to one decimal place.
        double hulp_double_dew_point =
            Math.round(mytemp.double_dew_point * 10)
                / 10.0; // nb Math.round(xxx) - > geeft long terug
        jTextField38.setText(Double.toString(hulp_double_dew_point) + " \u00B0" + "C");
      } else {
        jTextField38.setText("");
      }
    } // else (not AWS connected)

    //////// sst temp
    //
    //
    if (main.RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (main.SST_from_AWS_present == true) {
        if (displayed_aws_data_obsolate) {
          jTextField40.setForeground(obsolate_color_data_from_aws); // gray
        } else {
          jTextField40.setForeground(main.input_color_from_aws);
        }
      } else {
        if ((jTextField40.getForeground().getRGB() == main.input_color_from_aws.getRGB())
            || (jTextField40.getForeground().getRGB()
                == main.obsolate_color_data_from_aws.getRGB())) {
          // last value was measured by AWS (color was red/gray) but now no sst in last received aws
          // string -> clear text field
          jTextField40.setText("");
          mytemp.sea_water_temp = "";
        }
        jTextField40.setForeground(main.input_color_from_observer);
      }
    } // if (main.RS232_connection_mode == 3 || RS232_connection_mode == 9 || RS232_connection_mode
    // == 10  || RS232_connection_mode == 11)
    else // not aws connected
    {
      jTextField40.setForeground(main.input_color_from_observer);
    }

    if ((mytemp.sea_water_temp.compareTo("") != 0) && (mytemp.sea_water_temp != null)) {
      // Legacy display contract: integer input such as 25 is shown as 25.0 degrees C.
      int pos = mytemp.sea_water_temp.indexOf(".");
      if (pos == -1) // dus geen "." in de air temp string
      {
        jTextField40.setText(mytemp.sea_water_temp + ".0" + " \u00B0" + "C");
      } else {
        jTextField40.setText(mytemp.sea_water_temp + " \u00B0" + "C");
      }
    } else {
      jTextField40.setText("");
    }

    // update of the coded obs representation (bottom line main screen)
    //
    if (APR
        == false) // otherwise, in APR mode, the "next automated meteo report upload..." on botton
    // status line will be overwritten everytime
    {
      coded_obs_update();
    }
  }
}
