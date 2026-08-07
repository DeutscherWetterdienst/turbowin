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

  static String shortMonth(String month) {
    switch (month) {
      case "01":
        return "Jan";
      case "02":
        return "Feb";
      case "03":
        return "Mar";
      case "04":
        return "Apr";
      case "05":
        return "May";
      case "06":
        return "Jun";
      case "07":
        return "Jul";
      case "08":
        return "Aug";
      case "09":
        return "Sep";
      case "10":
        return "Oct";
      case "11":
        return "Nov";
      case "12":
        return "Dec";
      default:
        return null;
    }
  }

  /**
   * Returns the abbreviated month used by the dashboard and OSM output.
   *
   * <p>November intentionally remains lowercase {@code nov} to preserve the legacy dashboard/OSM
   * output format.
   */
  static String shortMonthOrOriginal(String month) {
    String result = shortMonth(month);
    if ("Nov".equals(result)) {
      return "nov";
    }
    return result == null ? month : result;
  }
}
