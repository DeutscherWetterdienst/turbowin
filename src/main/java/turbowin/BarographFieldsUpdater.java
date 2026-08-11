package turbowin;

import static turbowin.main.*;

/** Updates pressure-tendency fields on the main observation screen. */
final class BarographFieldsUpdater {

  private BarographFieldsUpdater() {}

  static void update() {
    // input text color setting 'amount of pressure tendency' and 'characteristic pressure tendency'
    //
    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected
    {
      if (displayed_aws_data_obsolate) {
        jTextField11.setForeground(obsolate_color_data_from_aws); // gray
        jTextField12.setForeground(obsolate_color_data_from_aws); // gray
      } else {
        jTextField11.setForeground(main.input_color_from_aws);
        jTextField12.setForeground(main.input_color_from_aws);
      }
    } else // no AWS
    {
      // jTextField11.setForeground(main.input_color_from_observer);
      // jTextField12.setForeground(main.input_color_from_observer);

      if (APR == true) {
        if (main.obsolate_data_flag == true) {
          jTextField11.setForeground(main.obsolete_input_color_from_apr);
          jTextField12.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField11.setForeground(main.input_color_from_apr);
          jTextField12.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // NO APR
      {
        jTextField11.setForeground(main.input_color_from_observer);
        jTextField12.setForeground(main.input_color_from_observer);
      } // else (no APR)
    } // else (no AWS)

    // amount of pressure tendency
    //
    if ((mybarograph.pressure_amount_tendency.compareTo("") != 0)
        && (mybarograph.pressure_amount_tendency != null)) {
      jTextField11.setText(mybarograph.pressure_amount_tendency + " hPa");
    } else {
      jTextField11.setText("");
    }

    // characteristic pressure tendency (a code)
    //
    if ((mybarograph.a_code.compareTo("") != 0) && (mybarograph.a_code != null)) {
      jTextField12.setText(mybarograph.a_code + " (code)");
    } else {
      jTextField12.setText("");
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
