package turbowin;

import static turbowin.main.*;

/** Updates pressure fields on the main observation screen. */
final class BarometerFieldsUpdater {

  private BarometerFieldsUpdater() {}

  static void update() {
    // input text color setting 'air pressure reading' and 'air pressure MSL'
    //
    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected
    {
      if (displayed_aws_data_obsolate) // set in Function:
      // RS422_init_new_aws_data_received_check_timer()[main_RS232_RS422.java]
      {
        jTextField9.setForeground(obsolate_color_data_from_aws); // gray
        jTextField10.setForeground(obsolate_color_data_from_aws); // gray
      } else {
        jTextField9.setForeground(main.input_color_from_aws);
        jTextField10.setForeground(main.input_color_from_aws);
      }
    } else // no AWS
    {
      if (APR == true) {
        if (main.obsolate_data_flag == true) {
          jTextField9.setForeground(main.obsolete_input_color_from_apr);
          jTextField10.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField9.setForeground(main.input_color_from_apr);
          jTextField10.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // no APR
      {
        jTextField9.setForeground(main.input_color_from_observer);
        jTextField10.setForeground(main.input_color_from_observer);
      } // else (no APR)
    } // else (no AWS)

    // air pressure reading
    //
    if ((mybarometer.pressure_reading_corrected.compareTo("") != 0)
        && (mybarometer.pressure_reading_corrected != null)) {
      jTextField9.setText(mybarometer.pressure_reading_corrected + " hPa");
    } else {
      jTextField9.setText("");
    }

    // air pressure MSL
    //
    if ((mybarometer.pressure_msl_corrected.compareTo("") != 0)
        && (mybarometer.pressure_msl_corrected != null)) {
      jTextField10.setText(mybarometer.pressure_msl_corrected + " hPa");
    } else {
      jTextField10.setText("");
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
