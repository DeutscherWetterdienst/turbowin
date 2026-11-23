package turbowin;

import static turbowin.main.*;

/** Updates position, course, and speed fields on the main screen. */
final class PositionFieldsUpdater {

  private PositionFieldsUpdater() {}

  static void update() {
    // position
    //
    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS
    {
      if (displayed_aws_data_obsolate) {
        jTextField5.setForeground(obsolate_color_data_from_aws); // gray
      } else {
        jTextField5.setForeground(main.input_color_from_aws);
      }
    } else // no AWS
    {
      if (APR == true) {
        if (main.obsolate_GPS_data_flag == true) {
          jTextField5.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField5.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // no APR
      {
        jTextField5.setForeground(main.input_color_from_observer);
      } // else (no APR)
    } // else (no AWS)

    if ((PositionModel.latitude_degrees.compareTo("") != 0
            && PositionModel.latitude_minutes.compareTo("") != 0
            && PositionModel.latitude_hemisphere.compareTo("") != 0
            && PositionModel.longitude_degrees.compareTo("") != 0
            && PositionModel.longitude_minutes.compareTo("") != 0
            && PositionModel.longitude_hemisphere.compareTo("") != 0)
        && (PositionModel.latitude_degrees != null
            && PositionModel.latitude_minutes != null
            && PositionModel.latitude_hemisphere != null
            && PositionModel.longitude_degrees != null
            && PositionModel.longitude_minutes != null
            && PositionModel.longitude_hemisphere != null)) {
      jTextField5.setText(
          PositionModel.latitude_degrees
              + "\u00B0"
              + " - "
              + PositionModel.latitude_minutes
              + "' "
              + PositionModel.latitude_hemisphere.substring(0, 0 + 1)
              + "  "
              + PositionModel.longitude_degrees
              + "\u00B0"
              + " - "
              + PositionModel.longitude_minutes
              + "' "
              + PositionModel.longitude_hemisphere.substring(0, 0 + 1));
    } else {
      jTextField5.setText("");
    }

    // course and speed
    //
    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected
    {
      if (displayed_aws_data_obsolate) {
        jTextField7.setForeground(obsolate_color_data_from_aws); // gray
      } else {
        jTextField7.setForeground(main.input_color_from_aws);
      }
    } else // no AWS
    {
      // jTextField7.setForeground(main.input_color_from_observer);
      if (APR == true) {
        if (main.obsolate_GPS_data_flag == true) {
          jTextField7.setForeground(main.obsolete_input_color_from_apr);
        } else {
          jTextField7.setForeground(main.input_color_from_apr);
        }
      } // if (APR == true)
      else // no APR
      {
        jTextField7.setForeground(main.input_color_from_observer);
      } // else (no APR)
    }

    if ((PositionModel.course.compareTo("") != 0 && PositionModel.speed.compareTo("") != 0)
        && (PositionModel.course != null && PositionModel.speed != null)) {
      jTextField7.setText(PositionModel.course + "\u00B0" + "  " + PositionModel.speed + " kts");
    } else {
      jTextField7.setText("");
    }

    /* update of the coded obs representation (bottom line main screen) */
    if (APR
        == false) // otherwise, in APR mode, the "next automated meteo report upload..." on botton
    // status line will be overwritten everytime
    {
      coded_obs_update();
    }
  }
}
