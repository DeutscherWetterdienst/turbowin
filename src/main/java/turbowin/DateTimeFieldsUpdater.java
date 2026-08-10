package turbowin;

import static turbowin.main.*;

/** Updates the date/time field on the main observation screen. */
final class DateTimeFieldsUpdater {

  private DateTimeFieldsUpdater() {}

  static void update() {

    if (RS232_connection_mode == 3
        || RS232_connection_mode == 9
        || RS232_connection_mode == 10
        || RS232_connection_mode == 11) // AWS connected mode
    {
      if (displayed_aws_data_obsolate) {
        jTextField3.setForeground(obsolate_color_data_from_aws); // gray
      } else {
        jTextField3.setForeground(input_color_from_aws);
      }
    } else {
      jTextField3.setForeground(input_color_from_observer);
    }

    if ((mydatetime.day.compareTo("") != 0
            && mydatetime.month.compareTo("") != 0
            && mydatetime.year.compareTo("") != 0
            && mydatetime.hour.compareTo("") != 0
            && mydatetime.minute.compareTo("") != 0)
        && (mydatetime.day != null
            && mydatetime.month != null
            && mydatetime.year != null
            && mydatetime.hour != null
            && mydatetime.minute != null)) {
      jTextField3.setText(
          mydatetime.day
              + " "
              + mydatetime.month
              + " "
              + mydatetime.year
              + "  "
              + mydatetime.hour
              + "."
              + mydatetime.minute
              + " UTC");
    } else {
      jTextField3.setText("");
    }

    /* update of the coded obs representation (bottom line main screen) */
    coded_obs_update();
  }
}
