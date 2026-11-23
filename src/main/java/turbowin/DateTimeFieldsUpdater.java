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

    if ((DateTimeModel.day.compareTo("") != 0
            && DateTimeModel.month.compareTo("") != 0
            && DateTimeModel.year.compareTo("") != 0
            && DateTimeModel.hour.compareTo("") != 0
            && DateTimeModel.minute.compareTo("") != 0)
        && (DateTimeModel.day != null
            && DateTimeModel.month != null
            && DateTimeModel.year != null
            && DateTimeModel.hour != null
            && DateTimeModel.minute != null)) {
      jTextField3.setText(
          DateTimeModel.day
              + " "
              + DateTimeModel.month
              + " "
              + DateTimeModel.year
              + "  "
              + DateTimeModel.hour
              + "."
              + DateTimeModel.minute
              + " UTC");
    } else {
      jTextField3.setText("");
    }

    /* update of the coded obs representation (bottom line main screen) */
    coded_obs_update();
  }
}
