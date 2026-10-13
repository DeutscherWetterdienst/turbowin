package turbowin;

import static turbowin.main_RS232_RS422.*;

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

final class Rs232GpsNmeaWindCogSogWorkflow {

  private Rs232GpsNmeaWindCogSogWorkflow() {}

  static void start(String mode, double[] positions) {
    new SwingWorker<Boolean, Void>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        boolean COG_SOG_wind_ok = true;

        // calculate COG_wind and SOG_wind (and course and speed for main scren)
        if ((positions[0] < Double.MAX_VALUE - 1)
            && (positions[1] < Double.MAX_VALUE - 1)
            && (positions[2] < Double.MAX_VALUE - 1)
            && (positions[3] < Double.MAX_VALUE - 1)) {
          // NB computing COG_APR_wind and SOG_APR_wind happens every minute in UPDATE_MINUTE
          // mode
          RS232_compute_APR_COG_SOG_wind(positions[0], positions[1], positions[2], positions[3]);

          // publish course and speed on main screen (indirect via myposition.course and
          // myposition.speed)
          if ((myposition.COG_APR_wind >= 0.0 && myposition.COG_APR_wind <= 360.0)
              && (myposition.SOG_APR_wind >= 0.0 && myposition.SOG_APR_wind <= 100.0)) {
            // NB myposition.course and myposition.speed only for main screen
            //    see position_fields_update()[main.java] called by
            // RS232_GPS_NMEA_0183_init_new_data_received_check_timer() [RS232_RS422.java]
            // myposition.course = Double.toString(myposition.COG_APR);
            // myposition.speed = Double.toString(myposition.SOG_APR);
          } else {
            // reset the course ansd speed vars
            myposition.COG_APR_wind = Double.MAX_VALUE;
            myposition.SOG_APR_wind = Double.MAX_VALUE;
            // myposition.course = "";
            // myposition.speed = "";
            COG_SOG_wind_ok = false;
          } // else
        } // if ( (positions_SOG_COG_wind[0] < Double.MAX_VALUE -1) etc.
        else {
          // reset the course ansd speed vars
          myposition.COG_APR_wind = Double.MAX_VALUE;
          myposition.SOG_APR_wind = Double.MAX_VALUE;
          // myposition.course = "";
          // myposition.speed = "";
          COG_SOG_wind_ok = false;
        } // else

        return COG_SOG_wind_ok;
      } // protected Void doInBackground() throws Exception

      @Override
      protected void done() {
        try {
          boolean COG_SOG_wind_result = get();

          // only in APR mode (1, 3, 6 hours) or manual mode, otherwise every minute message
          // lines in the log
          if (mode.equals("APR") || mode.equals("MANUAL")) {
            if (COG_SOG_wind_result == true) {
              main.log_turbowin_system_message(
                  "[GPS] For APTR dashboard wind [10 min].: COG and SOG calculation ok; "
                      + myposition.COG_APR_wind
                      + "\u00B0 "
                      + myposition.SOG_APR_wind
                      + " kts");
            } else {
              main.log_turbowin_system_message(
                  "[GPS] For APTR dashboard wind [10 min]: COG and SOG calculation error or start/end position not (yet) available");
            }
          } // if (mode.equals("APR") || mode.equals("MANUAL"))
        } // try
        catch (InterruptedException | ExecutionException ex) {
          System.out.println("+++ Error in Function: RS232_GPS_NMEA_0183_Date_Position_Parsing()");
        }
      } // protected void done()
    }.execute(); // new SwingWorker<Void, Void>()
  }
}
