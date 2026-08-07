package turbowin;

/** Small date/time helpers that do not depend on the Swing application state. */
final class DateTimeUtils {

  private DateTimeUtils() {}

  static String convert_month(int month_number) {
    String month_name = "";

    if (month_number == 0) {
      month_name = "January";
    } else if (month_number == 1) {
      month_name = "February";
    }
    if (month_number == 2) {
      month_name = "March";
    }
    if (month_number == 3) {
      month_name = "April";
    }
    if (month_number == 4) {
      month_name = "May";
    }
    if (month_number == 5) {
      month_name = "June";
    }
    if (month_number == 6) {
      month_name = "July";
    }
    if (month_number == 7) {
      month_name = "August";
    }
    if (month_number == 8) {
      month_name = "September";
    }
    if (month_number == 9) {
      month_name = "October";
    }
    if (month_number == 10) {
      month_name = "November";
    }
    if (month_number == 11) {
      month_name = "December";
    }

    return month_name;
  }
}
