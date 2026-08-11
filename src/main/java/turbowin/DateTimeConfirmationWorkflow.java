package turbowin;

import static turbowin.main.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/** Owns confirmation and application of the current system observation date/time. */
final class DateTimeConfirmationWorkflow {

  private DateTimeConfirmationWorkflow() {}

  interface DialogService {
    int showConfirmation(String message);

    void showWarning(String message);
  }

  private static final DialogService SYSTEM_DIALOGS =
      new DialogService() {
        @Override
        public int showConfirmation(String message) {
          JFrame jf = new JFrame();
          jf.setAlwaysOnTop(true);
          return JOptionPane.showConfirmDialog(
              jf,
              message,
              "Date and Time",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.INFORMATION_MESSAGE);
        }

        @Override
        public void showWarning(String message) {
          JOptionPane.showMessageDialog(
              null, message, main.APPLICATION_NAME + " warning", JOptionPane.WARNING_MESSAGE);
        }
      };

  static String formatTwoDigitCode(int value) {
    String code = Integer.toString(value);
    if (code.length() < 2) {
      code = "0000000000".substring(0, 2 - code.length()) + code;
    }
    return code;
  }

  static void checkAndSet() {
    checkAndSet(
        new GregorianCalendar(),
        new GregorianCalendar(new SimpleTimeZone(0, "UTC")),
        SYSTEM_DIALOGS);
  }

  static void checkAndSet(
      GregorianCalendar systemDateTimeLT,
      GregorianCalendar systemDateTimeUTC,
      DialogService dialogService) {
    // NB Never mind the computer is set to UTC or not. SimpleTimeZone with argument UTC convert
    // system date time
    //    always to UTC !!
    //
    String hulp_system_minute_LT = "";
    String hulp_system_minute_UTC = "";

    ///////// local computer time (LT) (eg
    // http://www.egmdss.com/gmdss-courses/mod/resource/view.php?id=2231)
    //
    cal_systeem_datum_tijd_LT = systemDateTimeLT;
    cal_systeem_datum_tijd_LT.getTime(); // effectueren

    int system_year_LT = cal_systeem_datum_tijd_LT.get(Calendar.YEAR);
    int system_month_LT =
        cal_systeem_datum_tijd_LT.get(
            Calendar.MONTH); // The first month of the year is JANUARY which is 0
    int system_day_of_month_LT =
        cal_systeem_datum_tijd_LT.get(
            Calendar.DAY_OF_MONTH); // The first day of the month has value 1
    int system_hour_of_day_LT =
        cal_systeem_datum_tijd_LT.get(
            Calendar.HOUR_OF_DAY); // HOUR_OF_DAY: 24 hour clock; HOUR : 12 hour clock
    int system_minute_LT = cal_systeem_datum_tijd_LT.get(Calendar.MINUTE);

    if (system_minute_LT <= 9) {
      hulp_system_minute_LT = "0" + Integer.toString(system_minute_LT);
    } else {
      hulp_system_minute_LT = Integer.toString(system_minute_LT);
    }

    String system_LT_string =
        "system: "
            + convert_month(system_month_LT)
            + " "
            + system_day_of_month_LT
            + ", "
            + system_year_LT
            + " "
            + system_hour_of_day_LT
            + "."
            + hulp_system_minute_LT
            + " LT";

    ///////// UTC computer time
    //
    cal_systeem_datum_tijd_UTC = systemDateTimeUTC;
    cal_systeem_datum_tijd_UTC.getTime(); // effectueren

    int system_year_UTC = cal_systeem_datum_tijd_UTC.get(Calendar.YEAR);
    int system_month_UTC =
        cal_systeem_datum_tijd_UTC.get(
            Calendar.MONTH); // The first month of the year is JANUARY which is 0
    int system_day_of_month_UTC =
        cal_systeem_datum_tijd_UTC.get(
            Calendar.DAY_OF_MONTH); // The first day of the month has value 1
    int system_hour_of_day_UTC =
        cal_systeem_datum_tijd_UTC.get(
            Calendar.HOUR_OF_DAY); // HOUR_OF_DAY: 24 hour clock; HOUR : 12 hour clock
    int system_minute_UTC = cal_systeem_datum_tijd_UTC.get(Calendar.MINUTE);

    if (system_minute_UTC <= 9) {
      hulp_system_minute_UTC = "0" + Integer.toString(system_minute_UTC);
    } else {
      hulp_system_minute_UTC = Integer.toString(system_minute_UTC);
    }

    String system_UTC_string =
        "system: "
            + convert_month(system_month_UTC)
            + " "
            + system_day_of_month_UTC
            + ", "
            + system_year_UTC
            + " "
            + system_hour_of_day_UTC
            + "."
            + hulp_system_minute_UTC
            + " UTC";

    ///////// obs time
    //
    if (system_minute_UTC > 30) {
      cal_systeem_datum_tijd_UTC.add(Calendar.HOUR_OF_DAY, 1); // add 1 hour
      cal_systeem_datum_tijd_UTC.getTime();
    }

    int obs_year = cal_systeem_datum_tijd_UTC.get(Calendar.YEAR);
    int obs_month =
        cal_systeem_datum_tijd_UTC.get(
            Calendar.MONTH); // The first month of the year is JANUARY which is 0
    int obs_day_of_month =
        cal_systeem_datum_tijd_UTC.get(
            Calendar.DAY_OF_MONTH); // The first day of the month has value 1
    int obs_hour_of_day =
        cal_systeem_datum_tijd_UTC.get(
            Calendar.HOUR_OF_DAY); // HOUR_OF_DAY: 24 hour clock; HOUR : 12 hour clock

    String obs_UTC_string =
        "obs: "
            + convert_month(obs_month)
            + " "
            + obs_day_of_month
            + ", "
            + obs_year
            + " "
            + obs_hour_of_day
            + ".00 UTC";

    // set this date-time required confirmation pop-up JOptionPane always on top // from version 4.4
    if (dialogService.showConfirmation(
            obs_UTC_string
                + "\n\n"
                + "("
                + system_UTC_string
                + ")"
                + "\n"
                + "("
                + system_LT_string
                + ")")
        == JOptionPane.YES_OPTION)
    // if (JOptionPane.showConfirmDialog(null, obs_UTC_string + "\n\n" + "(" + system_UTC_string +
    // ")" + "\n" + "(" + system_LT_string + ")", "Date and Time", JOptionPane.YES_NO_OPTION,
    // JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
    {
      // So date time of the confirmation message confirmed by the user

      mydatetime.year = Integer.toString(obs_year); // for progress main screen and IMMT
      mydatetime.month = convert_month(obs_month); // for progress main screen
      mydatetime.day = Integer.toString(obs_day_of_month); // for progress main screen
      mydatetime.hour = Integer.toString(obs_hour_of_day); // for progress main screen

      /* update date and time on TurboWin+ main screen */
      date_time_fields_update();

      // determine code figures for month (not for operational obs only for IMMT storage)
      //
      mydatetime.MM_code =
          formatTwoDigitCode(obs_month + 1); // +1 because obs_month started with 0 (January)

      // determine code figures for day of the month (for obs and IMMT)
      //
      mydatetime.YY_code =
          formatTwoDigitCode(obs_day_of_month); // obs_day_of_month e.g. 1,2,11,23,29

      // determine code figures for hour of day (for obs and IMMT)
      //
      mydatetime.GG_code = formatTwoDigitCode(obs_hour_of_day); // obs_hour_of_day e.g. 1,2,11,23

      use_system_date_time_for_updating = true;

      // set start-up sequence finished flag (used by pop-up screen reminder visual obs)
      // turbowin_start_up_sequence_finished = true;

    } // if (JOptionPane.showConfirmDialog(null, datum_tijd_string, "Date and time of observation",
    // JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION)
    else {
      use_system_date_time_for_updating =
          false; // see Function: main_RS232_RS422.set_datetime_while_collecting_sensor_data()

      // warning that APR will not work!!
      if (APR == true) {
        String info =
            "If this computer is not running on the correct date/time, APR (Automated Pressure Reports) will stop working!";
        dialogService.showWarning(info);
      }
    } // else

    /* clear memory as soon as possible */
    // cal_systeem_datum_tijd         = null;
  }
}
