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

    if ((myposition.latitude_degrees.compareTo("") != 0
            && myposition.latitude_minutes.compareTo("") != 0
            && myposition.latitude_hemisphere.compareTo("") != 0
            && myposition.longitude_degrees.compareTo("") != 0
            && myposition.longitude_minutes.compareTo("") != 0
            && myposition.longitude_hemisphere.compareTo("") != 0)
        && (myposition.latitude_degrees != null
            && myposition.latitude_minutes != null
            && myposition.latitude_hemisphere != null
            && myposition.longitude_degrees != null
            && myposition.longitude_minutes != null
            && myposition.longitude_hemisphere != null)) {
      jTextField5.setText(
          myposition.latitude_degrees
              + "\u00B0"
              + " - "
              + myposition.latitude_minutes
              + "' "
              + myposition.latitude_hemisphere.substring(0, 0 + 1)
              + "  "
              + myposition.longitude_degrees
              + "\u00B0"
              + " - "
              + myposition.longitude_minutes
              + "' "
              + myposition.longitude_hemisphere.substring(0, 0 + 1));
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

    if ((myposition.course.compareTo("") != 0 && myposition.speed.compareTo("") != 0)
        && (myposition.course != null && myposition.speed != null)) {
      jTextField7.setText(myposition.course + "\u00B0" + "  " + myposition.speed + " kts");
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
