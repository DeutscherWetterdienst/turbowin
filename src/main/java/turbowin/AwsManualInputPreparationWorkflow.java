package turbowin;

import static turbowin.main.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/** Prepares AWS date/time and position data for the IMMT log. */
final class AwsManualInputPreparationWorkflow {

  private AwsManualInputPreparationWorkflow() {}

  static void prepare() {
    // On request of Meteo France also store extra manually entered AWS parameters in the IMMT log;
    // observers are paid for these extra inserted parameters.
    // Manual parameters such as waves can be added by the observer, as can AWS parameters missing
    // from the incoming data, except pressure, which can never be inserted in AWS EUCAWS mode.

    boolean position_ok = true;
    int len;
    int hulp_month;

    // Date/time and position values are refreshed continuously from the AWS input.
    switch (mydatetime.month) {
      case "January":
        hulp_month = 1;
        break;
      case "February":
        hulp_month = 2;
        break;
      case "March":
        hulp_month = 3;
        break;
      case "April":
        hulp_month = 4;
        break;
      case "May":
        hulp_month = 5;
        break;
      case "June":
        hulp_month = 6;
        break;
      case "July":
        hulp_month = 7;
        break;
      case "August":
        hulp_month = 8;
        break;
      case "September":
        hulp_month = 9;
        break;
      case "October":
        hulp_month = 10;
        break;
      case "November":
        hulp_month = 11;
        break;
      case "December":
        hulp_month = 12;
        break;
      default:
        hulp_month = 0;
        break;
    }

    // The IMMT observation time is the AWS incoming-data time plus one hour.
    // GregorianCalendar intentionally uses the default time zone and locale here.
    if (mydatetime.year.equals("") == false
        && mydatetime.year != null
        && mydatetime.day.equals("") == false
        && mydatetime.day != null
        && mydatetime.hour.equals("") == false
        && mydatetime.hour != null
        && hulp_month != 0) {
      GregorianCalendar cal_obs_datum_tijd =
          new GregorianCalendar(
              Integer.parseInt(mydatetime.year),
              hulp_month - 1,
              Integer.parseInt(mydatetime.day),
              Integer.parseInt(mydatetime.hour),
              0);
      cal_obs_datum_tijd.add(Calendar.HOUR_OF_DAY, 1);
      String obs_date_time =
          new SimpleDateFormat("yyyyMMddHH").format(cal_obs_datum_tijd.getTime());

      mydatetime.year = obs_date_time.substring(0, 4);
      mydatetime.MM_code = obs_date_time.substring(4, 6);
      mydatetime.YY_code = obs_date_time.substring(6, 8);
      mydatetime.GG_code = obs_date_time.substring(8, 10);
    } else {
      mydatetime.year = "    ";
      mydatetime.MM_code = "  ";
      mydatetime.YY_code = "  ";
      mydatetime.GG_code = "  ";
    }

    try {
      myposition.int_latitude_degrees = Integer.parseInt(myposition.latitude_degrees.trim());
    } catch (NumberFormatException e) {
      position_ok = false;
    }
    try {
      myposition.int_latitude_minutes = Integer.parseInt(myposition.latitude_minutes.trim());
    } catch (NumberFormatException e) {
      position_ok = false;
    }
    try {
      myposition.int_longitude_degrees = Integer.parseInt(myposition.longitude_degrees.trim());
    } catch (NumberFormatException e) {
      position_ok = false;
    }
    try {
      myposition.int_longitude_minutes = Integer.parseInt(myposition.longitude_minutes.trim());
    } catch (NumberFormatException e) {
      position_ok = false;
    }

    if (position_ok) {
      // Qc_code identifies the globe quadrant: 1=N/E, 3=S/E, 5=S/W, and 7=N/W.
      if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH)
          && myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST)) {
        myposition.Qc_code = "1";
      } else if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH)
          && myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_EAST)) {
        myposition.Qc_code = "3";
      } else if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_SOUTH)
          && myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST)) {
        myposition.Qc_code = "5";
      } else if (myposition.latitude_hemisphere.equals(myposition.HEMISPHERE_NORTH)
          && myposition.longitude_hemisphere.equals(myposition.HEMISPHERE_WEST)) {
        myposition.Qc_code = "7";
      } else {
        myposition.Qc_code = " ";
      }

      // IMMT latitude codes are three characters and longitude codes are four characters.
      // Divide minutes by six using integer division so any remainder is intentionally discarded.
      String latitude_minutes_6 = Integer.toString(myposition.int_latitude_minutes / 6);
      if (latitude_minutes_6.length() != 1) {
        position_ok = false;
      }
      myposition.lalala_code =
          myposition.latitude_degrees.trim().replaceFirst("^0+(?!$)", "") + latitude_minutes_6;
      len = 3;
      if (myposition.lalala_code.length() < len) {
        myposition.lalala_code =
            "0000000000".substring(0, len - myposition.lalala_code.length())
                + myposition.lalala_code;
      }

      String longitude_minutes_6 = Integer.toString(myposition.int_longitude_minutes / 6);
      if (longitude_minutes_6.length() != 1) {
        position_ok = false;
      }
      myposition.lolololo_code =
          myposition.longitude_degrees.trim().replaceFirst("^0+(?!$)", "") + longitude_minutes_6;
      len = 4;
      if (myposition.lolololo_code.length() < len) {
        myposition.lolololo_code =
            "0000000000".substring(0, len - myposition.lolololo_code.length())
                + myposition.lolololo_code;
      }
    }

    if (position_ok == false) {
      myposition.Qc_code = " ";
      myposition.lalala_code = "   ";
      myposition.lolololo_code = "    ";
    }
  }
}
