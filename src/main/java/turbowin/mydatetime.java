package turbowin;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

/** Observation date and time state and manual-entry validation. */
public final class mydatetime {
  private String[] selected;
  private final boolean readOnly;

  public mydatetime() {
    int mode = main.RS232_connection_mode;
    readOnly = mode == 3 || mode == 9 || mode == 10 || mode == 11;
    selectValues(new String[] {year, month, day, hour});
  }

  String[] selectedValues() {
    return selected.clone();
  }

  void selectValues(String[] values) {
    String[][] options = DateTimeOptions.all();
    selected = new String[options.length];
    for (int i = 0; i < options.length; i++) {
      if (Arrays.asList(options[i]).contains(values[i])) selected[i] = values[i];
    }
  }

  boolean isReadOnly() {
    return readOnly;
  }

  private void Reset_All_DateTime_Vars() {
    // scope this module + myturbowin.java main module (all of type: static)
    year = "";
    month = "";
    day = "";
    hour = "";
    MM_code = ""; // month of year (only for IMMT)
    YY_code = ""; // day of the month
    GG_code = ""; // hour of obs

    // scope only this module (no static)
    checks_ok = false;

    /* update date/time fields on main screen */
    main.date_time_fields_update();
  }

  boolean accept() {
    // TODO add your handling code here:
    int len;

    if (main.RS232_connection_mode != 3
        && main.RS232_connection_mode != 9
        && main.RS232_connection_mode != 10
        && main.RS232_connection_mode != 11) // not AWS connected mode
    {
      // initialisation
      Reset_All_DateTime_Vars();

      // initialisation
      checks_ok = true; // was set to false in Reset_All_DateTime_Vars();

      // read selections from listboxes
      year = selected[0]; // for progress main screen and IMMT and format 101
      month = selected[1]; // for progress main screen
      day = selected[2]; // for progress main screen and format 101
      hour = selected[3]; // for progress main screen and format 101

      //
      /////// checks
      //
      if (year == null) {
        JOptionPane.showMessageDialog(
            null,
            "year not selected",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_DateTime_Vars();
      } else if (month == null) {
        JOptionPane.showMessageDialog(
            null,
            "month not selected",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_DateTime_Vars();
      } else if (day == null) {
        JOptionPane.showMessageDialog(
            null,
            "day not selected",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_DateTime_Vars();
      } else if (hour == null) {
        JOptionPane.showMessageDialog(
            null,
            "hour not selected",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_DateTime_Vars();
      } else {
        checks_ok = true;
      }

      if (checks_ok == true) {
        // check max number of days in a particular month
        //
        int iYear = Integer.parseInt(year);
        int iMonth =
            java.util.Arrays.asList(DateTimeOptions.all()[1]).indexOf(selected[1]); // 0-based
        int iDay =
            1; // only for determining max number of days in a particular month -> 1 is always ok
        // for testing (filled in day e.g. 31 could be misleading!)

        // Create a calendar object and set year and month
        Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

        // Get the number of days in that month
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28

        // test
        // System.out.println("daysInMonth = " + daysInMonth);

        if (daysInMonth
            < Integer.parseInt(
                day)) // daysInMonth = max possible in that month; day = as inserted by the user
        {
          JOptionPane.showMessageDialog(
              null,
              "combination month and day of month not valid",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          Reset_All_DateTime_Vars();
        } // if (daysInMonth < day)
      }

      if (checks_ok == true) {
        // NB String variable year can directly used in IMMT (So no conversion like the month var
        // necessary)
        //           (and year is not used in obs to be send)

        // determine code figures for month (for IMMT storage and format 101)
        //
        int int_MM_code =
            java.util.Arrays.asList(DateTimeOptions.all()[1]).indexOf(selected[1]) + 1;
        MM_code = Integer.toString(int_MM_code);

        len = 2; // MM_code always 2 characters width e.g. 03
        if (MM_code.length() < len) // pad on left with zeros
        {
          MM_code = "0000000000".substring(0, len - MM_code.length()) + MM_code;
        }

        // determine code figures for day of the month (for obs and IMMT)
        //
        YY_code = day; // day e.g. 1,2,11,23,29

        len = 2; // YY_code always 2 characters width e.g. 03
        if (YY_code.length() < len) // pad on left with zeros
        {
          YY_code = "0000000000".substring(0, len - YY_code.length()) + YY_code;
        }

        // determine code figures for hour of obs (for obs and IMMT)
        //
        GG_code = hour; // hour e.g. 1,2,11,23

        len = 2; // GG_code always 2 characters width e.g. 03
        if (GG_code.length() < len) // pad on left with zeros
        {
          GG_code = "0000000000".substring(0, len - GG_code.length()) + GG_code;
        }
      } // if (checks_ok == true)
    } // if (main.RS232_connection_mode != 3)
    else // AWS connected mode
    {
      checks_ok = true;
    }

    if (checks_ok == true) {
      /* update date/time fields on (progress) main screen */
      main.date_time_fields_update();
    } // if (checks_ok == true)
    return checks_ok;
  } // GEN-LAST:event_OK_button_actionPerformed

  // scope this module + myturbowin.java main module (all of type: static)
  public static String year = ""; // always 4 char
  public static String month = "";
  public static String day = "";
  public static String hour = "";
  public static String minute =
      "00"; // 00 default for manual obs but overwritten by AWS (SMD output data displayed on
  // TurboWin main screen)
  public static String MM_code = ""; // month of year (always 2 char)
  public static String YY_code = ""; // day of the month (always 2 char)
  public static String GG_code = ""; // hour of obs       (always 2 char)

  // scope only this module
  boolean checks_ok = false;
}
